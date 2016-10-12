/* Licensed to Jenkins CI under one or more contributor license
agreements.  See the NOTICE file distributed with this work
for additional information regarding copyright ownership. 
Jenkins CI licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except
in compliance with the License.  You may obtain a copy of the
License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License. */

package org.jenkinsci.plugins.saml;

import org.kohsuke.stapler.Stapler;

import org.acegisecurity.providers.AbstractAuthenticationToken;
import org.acegisecurity.context.SecurityContextHolder;

import javax.servlet.http.HttpSession;

import jenkins.model.Jenkins;
import jenkins.security.SecurityListener;

public class SamlAuthenticationToken extends AbstractAuthenticationToken {

  private static final long serialVersionUID = 2L;

  private final SamlUserDetails userDetails;
  private final HttpSession session;

  public SamlAuthenticationToken(SamlUserDetails userDetails, HttpSession session) {
    super(userDetails.getAuthorities());
    this.userDetails = userDetails;
    this.setDetails(userDetails);
    this.setAuthenticated(true);
    this.session = session;
  }

  public SamlUserDetails getPrincipal() {
    // check if session should have expired
    if (session.getAttribute(SamlSecurityRealm.EXPIRATION_ATTRIBUTE) != null &&
        System.currentTimeMillis() > (long)session.getAttribute(SamlSecurityRealm.EXPIRATION_ATTRIBUTE)) {

      // log the current user out and invalidate this session
      this.setAuthenticated(false);
      session.invalidate();
      SecurityContextHolder.clearContext();
      SecurityListener.fireLoggedOut(userDetails.getUsername());
    }

    return userDetails;
  }

  public String getCredentials() {
    return "SAML does not use passwords";
  }

}
