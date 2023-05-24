package com.kalinov.carsitty.config;

import com.kalinov.carsitty.RoleEnum;
import com.kalinov.carsitty.component.CustomBasicAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    UserDetailsManager userDetailsManager (DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username = ? AND enabled IS TRUE");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("SELECT u.username, r.role FROM users u " +
                "JOIN roles r ON u.roleId = r.id WHERE u.username = ?");

        return jdbcUserDetailsManager;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint) throws Exception {
        httpSecurity.cors()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .authorizeRequests()

                .antMatchers(HttpMethod.GET, "/users").hasAnyAuthority(RoleEnum.Manager.toString(), RoleEnum.Administrator.toString())
                .antMatchers(HttpMethod.GET, "/users/*").hasAnyAuthority(RoleEnum.Manager.toString(), RoleEnum.Administrator.toString())

                .antMatchers(HttpMethod.POST, "/users/employees").hasAuthority(RoleEnum.Manager.toString())
                .antMatchers(HttpMethod.PATCH, "/users/employees/*").hasAuthority(RoleEnum.Manager.toString())
                .antMatchers(HttpMethod.DELETE, "/users/employees/*").hasAuthority(RoleEnum.Manager.toString())

                .antMatchers(HttpMethod.POST, "/users/managers").hasAuthority(RoleEnum.Administrator.toString())
                .antMatchers(HttpMethod.PATCH, "/users/managers/*").hasAuthority(RoleEnum.Administrator.toString())
                .antMatchers(HttpMethod.DELETE, "/users/managers/*").hasAuthority(RoleEnum.Administrator.toString())

                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(customBasicAuthenticationEntryPoint)
                .and()
                .logout()
                .logoutUrl("/logout");

        return httpSecurity.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${http.allowed-origins}") String httpAllowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(httpAllowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
    }
}