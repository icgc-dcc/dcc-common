/*
 * Copyright (c) 2016 The Ontario Institute for Cancer Research. All rights reserved.                             
 *                                                                                                               
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with                                  
 * this program. If not, see <http://www.gnu.org/licenses/>.                                                     
 *                                                                                                               
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY                           
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES                          
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT                           
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,                                
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED                          
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;                               
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER                              
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN                         
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.common.test.file;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static java.util.Optional.of;
import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.util.Separators.NEWLINE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.icgc.dcc.common.core.io.Files2;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class FileTests {

	/**
	 * Recursively compare directory structure and it's contents.<br>
	 * <b>NB:</b> Only text content is supported.
	 */
	public static void compareDirectories(@NonNull String expectedDirPath, @NonNull String actualDirPath) {
		val expectedDir = new File(expectedDirPath);
		val actualDir = new File(actualDirPath);
		compareDirectories(expectedDir, actualDir);
	}

	/**
	 * Recursively compare directory structure and it's contents.<br>
	 * <b>NB:</b> Only text content is supported.
	 */
	public static void compareDirectories(@NonNull File expectedDir, @NonNull File actualDir) {
		log.info("Comparing directory '{}' to '{}'", expectedDir, actualDir);
		ensureFileReadability(expectedDir);
		ensureFileReadability(actualDir);

		File[] expectedFiles = expectedDir.listFiles();
		log.info("'{}' directory contents: {}", expectedDir, expectedFiles);
		for (val expectedFile : expectedFiles) {
			val actualFile = new File(actualDir, expectedFile.getName());
			if (expectedFile.isDirectory()) {
				compareDirectories(expectedFile, actualFile);
			} else {
				compareFiles(expectedFile.getAbsolutePath(), actualFile.getAbsolutePath());
			}
		}
	}

	/**
	 * Compares {@code expectedFilePath} with {@code actualFilePath}. Sorts the files before comparison. The files may be
	 * {@code gzip} or {@code bzip} compressed.<br>
	 * <b>NB:</b> Only text content is supported.
	 */
	public static void compareFiles(@NonNull String expectedFilePath, @NonNull String actualFilePath) {
		val expectedFile = getFile(expectedFilePath);
		val actualFile = getFile(actualFilePath);
		log.debug("Comparing files. Expected: {}. Actual: {}", expectedFile.getAbsolutePath(), actualFile.getAbsolutePath());

		val sortedExpectedFile = sortFile(expectedFile);
		val sortedActualFile = sortFile(actualFile);
		compareSortedFiles(sortedExpectedFile, sortedActualFile);
	}

	@SneakyThrows
	public static File getFile(String fileName, boolean create) {
		val file = new File(fileName);
		if (create && !file.exists()) {
			file.createNewFile();
		}
		FileTests.ensureFileReadability(file);

		return file;
	}

	public static File getFile(String fileName) {
		return getFile(fileName, false);
	}

	public static void ensureFileReadability(File testFile) {
		checkState(testFile.canRead(), "File '%s' is not readable.", testFile.getAbsolutePath());
	}

	public static void ensureFileWritability(File testFile) {
		checkState(testFile.canWrite(), "File '%s' is not writable.", testFile.getAbsolutePath());
	}

	@SneakyThrows
	public static BufferedWriter getBufferedWriter(File file) {
		ensureFileWritability(file);

		return new BufferedWriter(new FileWriter(file));
	}

	@SneakyThrows
	public static File getTempFile() {
		val tempFile = File.createTempFile("test-", "tmp");
		tempFile.deleteOnExit();

		return tempFile;
	}

	@SneakyThrows
	public static List<String> readFileToMutableList(File sourceFile) {
		ensureFileReadability(sourceFile);

		@Cleanup
		val sourceReader = Files2.getCompressionAgnosticBufferedReader(sourceFile.getAbsolutePath());
		val linesList = Lists.<String> newArrayList();
		String line = null;
		while ((line = sourceReader.readLine()) != null) {
			linesList.add(line);
		}

		return linesList;
	}

	/**
	 * <b>NB:</b> Don't use on large files, as the {@code sourceFile} is read into memory.
	 */
	@SneakyThrows
	public static File sortFile(File sourceFile) {
		val lines = readFileToMutableList(sourceFile);
		Collections.sort(lines);

		val sortedFile = getTempFile();
		@Cleanup
		val sortedFileWriter = getBufferedWriter(sortedFile);
		for (val line : lines) {
			sortedFileWriter.write(line);
			sortedFileWriter.write(NEWLINE);
		}

		return sortedFile;
	}

	@SneakyThrows
	public static void compareSortedFiles(File expected, File actual) {
		@Cleanup
		val expectedFileReader = Files2.getCompressionAgnosticBufferedReader(expected.getAbsolutePath());
		@Cleanup
		val actualFileReader = Files2.getCompressionAgnosticBufferedReader(actual.getAbsolutePath());
		int lineNumber = 1;
		String expectedLine = null;
		while ((expectedLine = expectedFileReader.readLine()) != null) {
			val actualLine = actualFileReader.readLine();
			log.debug("Comparing line:\n{}\n with \n{}", expectedLine, actualLine);

			if (!expectedLine.equals(actualLine)) {
				throw new AssertionError(getAssersionErrorMessage(lineNumber, expectedLine, actualLine));
			}
			lineNumber++;
		}
	}

	public static File getDataTypeFile(File dataTypeDir) {
		val dataTypeName = dataTypeDir.getName();

		// TODO: create gz extension
		return new File(dataTypeDir, dataTypeName + ".gz");
	}

	public static void concatenatePartFiles(File partFilesDir) {
		log.info("Concatenating 'part' files in {} ...", partFilesDir);
		File[] partFiles = partFilesDir.listFiles((dir, name) -> isPartFile(name));

		concatenateFiles(of(getDataTypeFile(partFilesDir).getAbsolutePath()), ImmutableList.copyOf(partFiles));
	}

	@SneakyThrows
	public static File concatenateFiles(@NonNull Optional<String> outFile, @NonNull List<File> inFiles) {
		if (inFiles.isEmpty()) {
			log.error("The input files list is empty. Skipping files concatenation...");
		}

		val outputFile = outFile.isPresent() ? getFile(outFile.get(), true) : getTempFile();
		log.debug("Concatenating files. Out file: {}. Input files: {}", outputFile, inFiles);
		ensureFileWritability(outputFile);

		@Cleanup
		val outStream = new FileOutputStream(outputFile);
		for (val inFile : inFiles) {
			Files.copy(inFile, outStream);
		}

		return outputFile;
	}

	private static boolean isPartFile(String name) {
		return name.startsWith("part-");
	}

	private static String getAssersionErrorMessage(int lineNumber, String expectedLine, String actualLine) {
		return format("Assertion failed at line %s. Expected line:%n%s%n" + "Actual line:%n%s", lineNumber, expectedLine,
				actualLine);
	}

}
