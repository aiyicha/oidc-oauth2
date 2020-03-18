package com.auth2.server.repository;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DatabaseConfiguration {

	@Autowired
	private DataSource dataSource;

	@Bean(name = {"defaultPersistenceUnit", "entityManagerFactory"})
	public FactoryBean<EntityManagerFactory> entityManagerFactory(JpaVendorAdapter jpaAdapter) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setPackagesToScan("org.mitre", "com.auth2");
//		factory.setPackagesToScan("org.mitre", "org.mitre");
		factory.setPersistenceProviderClass(org.eclipse.persistence.jpa.PersistenceProvider.class);
		factory.setPersistenceUnitName("defaultPersistenceUnit");
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(jpaAdapter);
		Map<String, Object> jpaProperties = new HashMap<String, Object>();
		jpaProperties.put("eclipselink.weaving", "false");
		jpaProperties.put("eclipselink.logging.level", "INFO");
		jpaProperties.put("eclipselink.logging.level.sql", "INFO");
		jpaProperties.put("eclipselink.cache.shared.default", "false");
		factory.setJpaPropertyMap(jpaProperties);

		return factory;
	}

//	@Bean
//	public DataSource dataSource() {
//		return new EmbeddedDatabaseBuilder(new DefaultResourceLoader() {
//			@Override
//			public Resource getResource(String location) {
//				String sql;
//				try {
//					sql = new String(Files.readAllBytes(Paths.get("..", "openid-connect-server-webapp", "src", "main",
//							"resources", "db", "mysql", location)), UTF_8);
//				} catch (IOException e) {
//					throw new RuntimeException("Failed to read sql-script " + location, e);
//				}
//
//				return new ByteArrayResource(sql.getBytes(UTF_8));
//			}
//		}).generateUniqueName(true).setScriptEncoding(UTF_8.name()).setType(EmbeddedDatabaseType.H2)
//				.addScripts("mysql_database_tables.sql").build();
//	}

	@Bean
	public JpaVendorAdapter jpaAdapter() {
		EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
		adapter.setDatabase(Database.MYSQL);
		adapter.setShowSql(true);
		return adapter;
	}

	@Bean("defaultTransactionManager")
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager platformTransactionManager = new JpaTransactionManager();
		platformTransactionManager.setEntityManagerFactory(entityManagerFactory);
		return platformTransactionManager;
	}
}
