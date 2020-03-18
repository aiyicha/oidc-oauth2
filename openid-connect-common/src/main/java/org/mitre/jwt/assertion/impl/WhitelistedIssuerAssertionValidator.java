/*******************************************************************************
 * Copyright 2018 The MIT Internet Trust Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.mitre.jwt.assertion.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.mitre.jwt.assertion.AssertionValidator;
import org.mitre.jwt.signer.service.JWTSigningAndValidationService;
import org.mitre.jwt.signer.service.impl.JWKSetCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Checks to see if the assertion was signed by a particular authority available from a whitelist
 * @author jricher
 *
 */
@Component
@ConfigurationProperties("spring.oidc")
public class WhitelistedIssuerAssertionValidator implements AssertionValidator {

	private static Logger logger = LoggerFactory.getLogger(WhitelistedIssuerAssertionValidator.class);

	/**
	 * Map of issuer -> JWKSetUri
	 */
	private Map<String, String> whitelist = new HashMap<>();

	public void setWhitelist(Map<String, String> whitelist) {
		this.whitelist.putAll(whitelist);
	}

	/**
	 * @return the whitelist
	 */
	public Map<String, String> getWhitelist() {
		return whitelist;
	}

	@Autowired
	private JWKSetCacheService jwkCache;

	@Override
	public boolean isValid(JWT assertion) {

		if (!(assertion instanceof SignedJWT)) {
			// unsigned assertion
			return false;
		}

		JWTClaimsSet claims;
		try {
			claims = assertion.getJWTClaimsSet();
		} catch (ParseException e) {
			logger.debug("Invalid assertion claims");
			return false;
		}

		if (Strings.isNullOrEmpty(claims.getIssuer())) {
			logger.debug("No issuer for assertion, rejecting");
			return false;
		}

		if (!whitelist.containsKey(claims.getIssuer())) {
			logger.debug("Issuer is not in whitelist, rejecting");
			return false;
		}

		String jwksUri = whitelist.get(claims.getIssuer());

		JWTSigningAndValidationService validator = jwkCache.getValidator(jwksUri);

		if (validator.validateSignature((SignedJWT) assertion)) {
			return true;
		} else {
			return false;
		}

	}

}
