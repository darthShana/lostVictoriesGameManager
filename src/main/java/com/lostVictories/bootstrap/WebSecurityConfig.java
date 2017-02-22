package com.lostVictories.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.lostVictories.service.GameAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private GameAuthenticationProvider authenticationProvider;

	@Autowired
	public WebSecurityConfig(GameAuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		 http
         .authorizeRequests()
             .antMatchers("/", "/landing", "/createUser", "/createFormUser", "/userLogin").permitAll()
             .antMatchers("/assets/**", "/js/**").permitAll()
             .anyRequest().authenticated()
             .and()
         .formLogin()
             .loginPage("/login")
             .defaultSuccessUrl("/home", true)
             .permitAll()
             .and()
         .logout()
             .permitAll();
//		http.authorizeRequests()
//				.antMatchers("/userLogin", "/createUser", "/helloWorld").permitAll()
//				.anyRequest().authenticated();
		http.csrf().disable();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider);

	}
}
