/**

© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.notification.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PlainTextAuthProvider;
import com.infosys.lex.notification.properties.ApplicationServerProperties;
import com.infosys.lex.notification.properties.DatabaseProperties;

@Configuration
@ConfigurationProperties("spring.data.cassandra.bodhi")
@EnableCassandraRepositories(basePackages = "com.infosys.lex.notification.bodhi.repository", cassandraTemplateRef = "bodhiTemplate")
public class BodhiConfig extends AbstractCassandraConfiguration  {

	@Autowired
	DatabaseProperties dbProperties;
	
	@Autowired
	ApplicationServerProperties appServerProp;
	
	protected String contactPoints;
	protected Integer port;
	protected String keyspaceName;

	@Override
	protected boolean getMetricsEnabled() { return false; }
	
	
	@Override
	public String getContactPoints() {
		return contactPoints;
	}

	/**
	 * @param contactPoints
	 */
	public void setContactPoints(String contactPoints) {
		this.contactPoints = contactPoints;
	}

	/**
	 * @param keyspaceName
	 */
	public void setKeyspaceName(String keyspaceName) {
		this.keyspaceName = keyspaceName;
	}

	@Override
	public String getKeyspaceName() {
		return keyspaceName;
	}

	@Override
	public int getPort() {
		return port;
	}

	/**
	 * @param contactPoints
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.cassandra.config.AbstractCassandraConfiguration#
	 * cassandraTemplate()
	 */
	@Override
	@Primary
	@Bean(name = "bodhiTemplate")
	public CassandraAdminOperations cassandraTemplate() throws Exception {
		return new CassandraAdminTemplate(session().getObject(), cassandraConverter());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.cassandra.config.AbstractCassandraConfiguration#
	 * session()
	 */
	@Override
	@Bean(name = "bodhiSession")
	public CassandraSessionFactoryBean session() {

		AuthProvider authProvider = new PlainTextAuthProvider(appServerProp.getCassandraUserName(), appServerProp.getCassandraPassword());
		CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
		session.setCluster(Cluster.builder().addContactPoint(getContactPoints()).withAuthProvider(authProvider)
				.withPort(getPort()).withAuthProvider(new PlainTextAuthProvider(dbProperties.getCassandraUserName(),
						dbProperties.getCassandraPassword()))
				.build());
		session.setConverter(cassandraConverter());
		session.setKeyspaceName(getKeyspaceName());
		session.setSchemaAction(getSchemaAction());
		session.setStartupScripts(getStartupScripts());
		session.setShutdownScripts(getShutdownScripts());
		return session;
	}

	/**
	 * @return
	 * @throws ClassNotFoundException
	 */
	/**
	 * @return
	 * @throws ClassNotFoundException
	 */
	@Bean("bodhiOperations")
	public CassandraOperations cassandraOperationsB() throws ClassNotFoundException {
		return new CassandraTemplate(session().getObject(), cassandraConverter());
	}

	
}