package com.studyolle.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.studyolle.account.validator.AccountService;

import lombok.RequiredArgsConstructor;


@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final AccountService accountService;
	private final DataSource dataSource;
	
	@Override
	public void configure(HttpSecurity http) throws Exception{
		http.authorizeRequests()
		    .mvcMatchers("/","/login","/sign-up","/check-email-token","/email-login","/login-by-email",
		    		     "/check-email-login","/login-link").permitAll()
		    .mvcMatchers(HttpMethod.GET,"/profile/*").permitAll()
		    .anyRequest().authenticated();
		
		
		http.formLogin()
		    .loginPage("/login").permitAll();
		
		http.logout()
		    .logoutSuccessUrl("/");
		
		http.rememberMe()
		   .userDetailsService(accountService)
		    .tokenRepository(tokenRepository());
	}
	
	@Bean
	public PersistentTokenRepository tokenRepository() {
		JdbcTokenRepositoryImpl jdbcTokenRepository=new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource);
		return jdbcTokenRepository;
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception{
		web.ignoring()
		   .mvcMatchers("account/email-login")
		   .mvcMatchers("/node_modules/**")
		   .antMatchers("/favicon.ico", "/resources/**", "/error")
		   .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
	

}
