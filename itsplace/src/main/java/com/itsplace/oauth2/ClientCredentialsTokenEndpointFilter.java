/*
 * Copyright 2006-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.itsplace.oauth2;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * A filter and authentication endpoint for the OAuth2 Token Endpoint. Allows clients to authenticate using request
 * parameters if included as a security filter, as permitted by the specification (but not recommended). It is
 * recommended by the specification that you permit HTTP basic authentication for clients, and not use this filter at
 * all.
 * 
 * @author Dave Syer
 * 
 */
public class ClientCredentialsTokenEndpointFilter extends AbstractAuthenticationProcessingFilter {

	protected ClientCredentialsTokenEndpointFilter() {
	
		super("/oauth/token");
		System.out.println("ClientCredentialsTokenEndpointFilter");
		System.out.println("ClientCredentialsTokenEndpointFilter");
		System.out.println("ClientCredentialsTokenEndpointFilter");
		System.out.println("ClientCredentialsTokenEndpointFilter");
		System.out.println("ClientCredentialsTokenEndpointFilter");
		System.out.println("ClientCredentialsTokenEndpointFilter");
		setAuthenticationFailureHandler(new OAuth2AuthenticationFailureHandler());
	}

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				// no-op - just allow filter chain to continue to token endpoint
				System.out.println("onAuthenticationSuccess");
				System.out.println("onAuthenticationSuccess");
				System.out.println("onAuthenticationSuccess");
				System.out.println("onAuthenticationSuccess");
				System.out.println("onAuthenticationSuccess");
				System.out.println("onAuthenticationSuccess");
				
			}
		});
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		String clientId = request.getParameter("client_id");
		String clientSecret = request.getParameter("client_secret");

		if (clientId == null) {
			return null;
		}

		if (clientSecret == null) {
			clientSecret = "";
		}

		clientId = clientId.trim();
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(clientId,
				clientSecret);

		return this.getAuthenticationManager().authenticate(authRequest);

	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain, Authentication authResult) throws IOException, ServletException {
		System.out.println("ssssssssssssssssss");
		super.successfulAuthentication(request, response, chain, authResult);
		chain.doFilter(request, response);
	}

	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("rrrrrrrrrrrrrrrrrr");
		String uri = request.getRequestURI();
		int pathParamIndex = uri.indexOf(';');

		if (pathParamIndex > 0) {
			// strip everything after the first semi-colon
			uri = uri.substring(0, pathParamIndex);
		}

		String clientId = request.getParameter("client_id");

		if (clientId == null) {
			// Give basic auth a chance to work instead (it's preferred anyway)
			return false;
		}

		return super.requiresAuthentication(request, response);
	}

}
