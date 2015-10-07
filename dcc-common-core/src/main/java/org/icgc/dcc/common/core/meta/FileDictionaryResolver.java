/*
 * Copyright (c) 2015 The Ontario Institute for Cancer Research. All rights reserved.                             
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
package org.icgc.dcc.common.core.meta;

import static org.icgc.dcc.common.core.io.Files2.checkExistsAndReadable;
import static org.icgc.dcc.common.core.io.Files2.getCompressionAgnosticFirstLine;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.icgc.dcc.common.core.meta.Resolver.DictionaryResolver;
import org.icgc.dcc.common.core.util.Jackson;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;

/**
 * An implementation of the {@link DictionaryResolver} that reads the dictionary from a file.
 */
@RequiredArgsConstructor
public class FileDictionaryResolver implements DictionaryResolver {

  @NonNull
  private final String dictionaryFileName;

  @Override
  public ObjectNode get() {
    checkExistsAndReadable(dictionaryFileName);

    return readDictionary(dictionaryFileName);
  }

  @Override
  public ObjectNode apply(Optional<String> input) {
    throw new UnsupportedOperationException();
  }

  private static ObjectNode readDictionary(String dictionaryFileName) {
    val dictionary = getCompressionAgnosticFirstLine(dictionaryFileName);

    return Jackson.toObjectNode(dictionary);
  }

}
