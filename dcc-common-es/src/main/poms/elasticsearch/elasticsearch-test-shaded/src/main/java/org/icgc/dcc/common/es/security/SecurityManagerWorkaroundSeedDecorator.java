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
package org.icgc.dcc.common.es.security;

import com.carrotsearch.randomizedtesting.SeedDecorator;

/**
 * Very ugly workaround to disable the SecurityManager that Elasticsearch now injects in Tests, it is cumbersome to set
 * up the security policy everywhere and causes lots of test-failures and strange side-effects, e.g.
 * https://issues.gradle.org/browse/GRADLE-2170, which hangs junit test runs in Gradle as a result
 * 
 * @see https://github.com/Dynatrace/Dynatrace-Elasticsearch-Plugin/blob/master/testsrcES5/org/elasticsearch/test/SecurityManagerWorkaroundSeedDecorator.java
 */
public class SecurityManagerWorkaroundSeedDecorator implements SeedDecorator {

  @Override
  public void initialize(Class<?> suiteClass) {
    System.setProperty("tests.security.manager", "false");
  }

  @Override
  public long decorate(long seed) {
    return seed;
  }

}
