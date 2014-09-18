/*
 * Copyright (c) 2014 The Ontario Institute for Cancer Research. All rights reserved.                             
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
package org.icgc.dcc.icgc.client.impl.cgp;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.icgc.dcc.icgc.client.api.ICGCClientConfig;
import org.icgc.dcc.icgc.client.api.cgp.CGPClient;
import org.icgc.dcc.icgc.client.api.cgp.CancerGenomeProject;
import org.icgc.dcc.icgc.client.api.cgp.DataLevelProject;
import org.icgc.dcc.icgc.client.api.cgp.User;
import org.icgc.dcc.icgc.client.impl.BaseOAuthICGCClient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

/**
 * Implementation of CGPClient
 */
@Slf4j
public class DefaultCGPClient extends BaseOAuthICGCClient implements CGPClient {

  private static final String DETAILS_PARAM_VALUE = "details";
  private static final String MEMBERSHIPS_PARAM_VALUE = "memberships";
  private static final String ENTITY_ID_PARAM_NAME = "entity-id";
  private static final String ENTITY_TYPE_PARAM_NAME = "entity-type";
  private static final String ENTITY_TYPE_PARAM_VALUE = "cgp";
  private static final String EXPAND_PARAM_NAME = "expand";

  private static enum UserType {
    LEADERS, MEMBERS, DATA_SUBMITTERS;
  }

  /** Add <code>expand=details</code> to the request */
  private boolean details;

  /** Add <code>expand=memberships</code> to the request */
  private boolean memberships;

  private final WebResource resourse;

  public DefaultCGPClient(@NonNull ICGCClientConfig config) {
    super(config);
    checkStringArguments(config.getCgpServiceUrl());
    this.resourse = jerseyClient
        .resource(config.getCgpServiceUrl())
        .queryParam(ENTITY_TYPE_PARAM_NAME, ENTITY_TYPE_PARAM_VALUE);
  }

  @Override
  public List<CancerGenomeProject> getCancerGenomeProjects() {
    val clientResponse = resourse.get(ClientResponse.class);

    return convertProjects(clientResponse.getEntity(new GenericType<List<CGPResponseMini>>() {}));
  }

  /**
   * Converts CGPResponseMini objects to CGPResponse ones.
   */
  private static List<CancerGenomeProject> convertProjects(List<CGPResponseMini> source) {
    val result = new ImmutableList.Builder<CancerGenomeProject>();
    for (val cgpResponseMini : source) {
      result.add(cgpMini2CancerGenomeProject(cgpResponseMini));
    }

    return result.build();
  }

  @Override
  public CancerGenomeProject getCancerGenomeProject(String id) {
    checkStringArguments(id);
    val query = addParams(resourse.queryParam(ENTITY_ID_PARAM_NAME, id));

    return cgp2CancerGenomeProject(query.get(ClientResponse.class).getEntity(CGPResponse.class));
  }

  /**
   * Adds details and memberships {@code request} parameters request if they are set
   * @param request to be modified
   * @return modified request
   */
  private WebResource addParams(WebResource request) {
    WebResource result = request;
    boolean paramAdded = false;
    String param = "";

    if (details) {
      param = DETAILS_PARAM_VALUE;
      paramAdded = true;
    }

    if (memberships) {
      // query has format "expand=details,memberships"
      if (paramAdded) param = param.concat(",");
      param = param.concat(MEMBERSHIPS_PARAM_VALUE);
    }

    if (details || memberships) {
      result = result.queryParam(EXPAND_PARAM_NAME, param);
    }

    return result;
  }

  @Override
  public CGPClient details() {
    details = true;

    return this;
  }

  @Override
  public CGPClient memberships() {
    memberships = true;

    return this;
  }

  @Override
  public DataLevelProject getDataLevelProject(String id) {
    checkStringArguments(id);
    val query = addParams(resourse.queryParam(ENTITY_ID_PARAM_NAME, id));

    return dlp2DataLevelProject(query.get(ClientResponse.class).getEntity(DLPResponse.class));
  }

  /**
   * Converts a response got from the REST API to the client return format.
   */
  private static CancerGenomeProject cgp2CancerGenomeProject(CGPResponse project) {
    log.debug("Original {}", project);
    return CancerGenomeProject.builder()
        .nid(project.getCGPId())
        .name(project.getCGPName())
        .organSystem(project.getCGPOrganSystem())
        .country(project.getCGPCountry())
        .details(project.getCgpDetails())
        .members(getCGPUsers(project, UserType.MEMBERS))
        .leaders(getCGPUsers(project, UserType.LEADERS))
        .dataSubmitters(getCGPUsers(project, UserType.DATA_SUBMITTERS))
        .dlps(getDlpsFromCGPResponse(project))
        .build();
  }

  /**
   * Creates a list of DataLevelProject extracted from the <code>project</code>
   */
  private static List<DataLevelProject> getDlpsFromCGPResponse(CGPResponse project) {
    val result = new ImmutableList.Builder<DataLevelProject>();
    val dlps = project.getDlps();

    if (dlps != null) {
      for (val dlp : dlps) {
        result.add(getDLPFromCGPResponse(project, dlps.indexOf(dlp)));
      }
    }

    return result.build();
  }

  /**
   * Creates a DataLevelProject object extracted from <code>project</code>
   * @param project - CGPResponse used as a source
   * @param index in dlps array in CGPResponse to be extracted
   */
  private static DataLevelProject getDLPFromCGPResponse(CGPResponse project, int index) {
    val result = DataLevelProject.builder()
        .nid(project.getDlps().get(index).getDlp().getNid())
        .name(project.getDlps().get(index).getDlp().getName())
        .leaders(getDLPUsers(project, index, UserType.LEADERS))
        .members(getDLPUsers(project, index, UserType.MEMBERS))
        .dataSubmitters(getDLPUsers(project, index, UserType.DATA_SUBMITTERS));

    if (project.getDlpDetails().isEmpty()) {
      result.details(createEmptyDetails());
    } else {
      result.details(project.getDlpDetails().get(index));
    }

    return result.build();
  }

  /**
   * Extracts members/leaders/data_submitters Cancer Genome Project details from <code>project</code>
   * @param project used as source
   * @param type to be extracted
   */
  private static List<User> getCGPUsers(CGPResponse project, UserType type) {
    if (project.getMemberships() == null) return createEmptyUsers();
    List<UserContainer> users = Collections.emptyList();

    switch (type) {
    case LEADERS:
      users = project.getMemberships().getCgp().getLeaders();
      break;
    case MEMBERS:
      users = project.getMemberships().getCgp().getMembers();
      break;
    case DATA_SUBMITTERS:
      users = project.getMemberships().getCgp().getDataSubmitters();
      break;
    }

    return addUsers(users);
  }

  /**
   * Extracts members/leaders/data_submitters Data Level Project details from <code>project</code>
   * @param project used as source
   * @param index in dlps array in CGPResponse to be extracted
   * @param type to be extracted
   */
  private static List<User> getDLPUsers(CGPResponse project, int index, UserType type) {
    if (project.getMemberships() == null) return createEmptyUsers();
    List<UserContainer> users = Collections.emptyList();

    switch (type) {
    case LEADERS:
      users = project.getMemberships().getDlps().get(index).getDlp().getMemberships().getLeaders();
      break;
    case MEMBERS:
      users = project.getMemberships().getDlps().get(index).getDlp().getMemberships().getMembers();
      break;
    case DATA_SUBMITTERS:
      users = project.getMemberships().getDlps().get(index).getDlp().getMemberships().getDataSubmitters();
      break;
    }

    return addUsers(users);
  }

  /**
   * Extracts members/leaders/data_submitters Data Level Project details from <code>project</code>
   * @param project used as source
   * @param type to be extracted
   */
  private static List<User> getDLPUsers(DLPResponse project, UserType type) {
    if (project.getMemberships() == null) return createEmptyUsers();
    List<UserContainer> users = Collections.emptyList();

    switch (type) {
    case LEADERS:
      users = project.getMemberships().getLeaders();
      break;
    case MEMBERS:
      users = project.getMemberships().getMembers();
      break;
    case DATA_SUBMITTERS:
      users = project.getMemberships().getDataSubmitters();
      break;
    }

    return addUsers(users);
  }

  /**
   * Converts a CGPResponseMini object to CancerGenomeProject
   */
  private static CancerGenomeProject cgpMini2CancerGenomeProject(CGPResponseMini project) {
    log.debug("Original {}", project);
    return CancerGenomeProject.builder()
        .nid(project.getCgpId())
        .details(createEmptyDetails())
        .dlps(getDlpsFromCGPResponseMini(project))
        .dataSubmitters(createEmptyUsers())
        .leaders(createEmptyUsers())
        .members(createEmptyUsers())
        .build();
  }

  private static List<DataLevelProject> getDlpsFromCGPResponseMini(CGPResponseMini project) {
    val result = new ImmutableList.Builder<DataLevelProject>();
    for (val dlp : project.getDlpIds()) {
      result.add(DataLevelProject.builder()
          .nid(dlp)
          .details(createEmptyDetails())
          .dataSubmitters(createEmptyUsers())
          .leaders(createEmptyUsers())
          .members(createEmptyUsers())
          .build());
    }

    return result.build();
  }

  private static DataLevelProject dlp2DataLevelProject(DLPResponse project) {
    return DataLevelProject.builder()
        .nid(project.getNid())
        .name(project.getName())
        .details(project.getDlpDetails())
        .dataSubmitters(getDLPUsers(project, UserType.DATA_SUBMITTERS))
        .leaders(getDLPUsers(project, UserType.LEADERS))
        .members(getDLPUsers(project, UserType.MEMBERS))
        .build();
  }

  private static List<User> createEmptyUsers() {
    return new ImmutableList.Builder<User>().build();
  }

  private static Map<String, String> createEmptyDetails() {
    return new ImmutableMap.Builder<String, String>().build();
  }

  private static List<User> addUsers(List<UserContainer> users) {
    val result = new ImmutableList.Builder<User>();

    for (val user : users) {
      result.add(user.getUser());
    }

    return result.build();
  }

}
