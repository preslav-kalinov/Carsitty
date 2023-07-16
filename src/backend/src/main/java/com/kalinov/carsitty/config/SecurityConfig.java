package com.kalinov.carsitty.config;

import com.kalinov.carsitty.RoleEnum;
import com.kalinov.carsitty.component.CustomBasicAuthenticationEntryPoint;
import com.kalinov.carsitty.dto.ExceptionDto;
import com.kalinov.carsitty.util.HttpResponseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

import javax.servlet.http.HttpServletResponse;
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

                .antMatchers( "/users").hasAuthority(RoleEnum.Administrator.toString())
                .antMatchers("/users/*").hasAuthority(RoleEnum.Administrator.toString())

                .antMatchers(HttpMethod.POST, "/backup").hasAuthority(RoleEnum.Administrator.toString())

                .antMatchers(HttpMethod.GET, "/parts").hasAnyAuthority(RoleEnum.Manager.toString(), RoleEnum.Administrator.toString(), RoleEnum.Employee.toString())
                .antMatchers(HttpMethod.GET, "/parts/*").hasAnyAuthority(RoleEnum.Manager.toString(), RoleEnum.Administrator.toString(), RoleEnum.Employee.toString())
                .antMatchers(HttpMethod.GET, "/parts/categories").hasAnyAuthority(RoleEnum.Manager.toString(), RoleEnum.Administrator.toString(), RoleEnum.Employee.toString())
                .antMatchers(HttpMethod.GET, "/parts/cars").hasAnyAuthority(RoleEnum.Manager.toString(), RoleEnum.Administrator.toString(), RoleEnum.Employee.toString())

                .antMatchers(HttpMethod.POST, "/parts/logs").hasAuthority(RoleEnum.Administrator.toString())

                .antMatchers(HttpMethod.POST, "/parts/*/sale").hasAuthority(RoleEnum.Employee.toString())
                .antMatchers(HttpMethod.POST, "/parts/*/share").hasAuthority(RoleEnum.Employee.toString())

                .antMatchers(HttpMethod.POST, "/parts").hasAuthority(RoleEnum.Manager.toString())
                .antMatchers(HttpMethod.PUT, "/parts/*").hasAuthority(RoleEnum.Manager.toString())
                .antMatchers(HttpMethod.DELETE, "/parts/*").hasAuthority(RoleEnum.Manager.toString())

                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    HttpResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_FORBIDDEN,
                            new ExceptionDto(HttpStatus.FORBIDDEN.getReasonPhrase(),
                                    HttpResponseUtil.getErrorContent("Authorization not enough")));
                })
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
        configuration.setAllowCredentials(true);
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