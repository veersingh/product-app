package com.example.productapp.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
	 @Autowired
	   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	      KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
	      keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
	      auth.authenticationProvider(keycloakAuthenticationProvider);
	   }
	 
	 @Bean
	   public org.keycloak.adapters.KeycloakConfigResolver KeycloakConfigResolver() {
	      return new KeycloakSpringBootConfigResolver();
	   }

	@Autowired
	public KeycloakClientRequestFactory keycloakClientRequestFactory;

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public KeycloakRestTemplate keycloakRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
	            .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
	            .build();
	        CloseableHttpClient httpClient = HttpClients.custom()
	            .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
	            .build();
			keycloakClientRequestFactory.setHttpClient(httpClient);
		return new KeycloakRestTemplate(keycloakClientRequestFactory);
	}

	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	@Override
	   protected void configure(HttpSecurity http) throws Exception
	   {
	      super.configure(http);
	      http
	            .authorizeRequests()
	            .antMatchers("/products*").hasRole("user")
	            .anyRequest().permitAll();
	   }

}
