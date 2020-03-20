/*******************************************************************************
 * Copyright 2018 The MIT Internet Trust Consortium
 *
 * Portions copyright 2011-2013 The MITRE Corporation
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
package com.auth2.server.openid.connect.repository.impl;

import org.mitre.openid.connect.model.Authorities;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.AuthoritiesRepository;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.mitre.util.jpa.JpaUtil.getSingleResult;

/**
 * JPA UserInfo repository implementation
 *
 * @author Michael Joseph Walsh
 *
 */
@Repository("jpaAuthoritiesRepository")
public class JpaAuthoritiesRepository implements AuthoritiesRepository {

	@PersistenceContext(unitName="defaultPersistenceUnit")
	private EntityManager manager;

	/**
	 * Get authorities by user
	 */
	@Override
	public List<Authorities> getByUserId(Long userId) {
		TypedQuery<Authorities> query = manager.createNamedQuery(Authorities.QUERY_BY_USERID, Authorities.class);
		query.setParameter(Authorities.PARAM_USER_ID, userId);

		return query.getResultList();

	}

}
