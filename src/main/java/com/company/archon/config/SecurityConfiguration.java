package com.company.archon.config;

import com.company.archon.exception.ApplicationExceptionHandler;
import com.company.archon.services.AuthorizationService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {
    @Configuration
    @RequiredArgsConstructor
    public static class FormLoginAuthentication extends WebSecurityConfigurerAdapter {
        private final ApplicationExceptionHandler exceptionHandler;
        private final AuthorizationService userService;

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        @Override
        protected UserDetailsService userDetailsService() {
            return new CustomUserDetailsService(userService);
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/").permitAll()
                    .anyRequest().authenticated();
            http
                    .rememberMe()
                    .userDetailsService(userDetailsService())
                    .rememberMeParameter("rememberMe")
                    .rememberMeCookieName("JSESSION_REMEMBER_ME");
            http
                    .logout()
                    .logoutUrl("/logout")
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
                        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                    })
                    .invalidateHttpSession(true);
            http
                    .csrf().disable();
            http
                    .exceptionHandling()
                    .defaultAuthenticationEntryPointFor(
                            getRestAuthenticationEntryPoint(),
                            new AntPathRequestMatcher("/**"));
        }

        private AuthenticationEntryPoint getRestAuthenticationEntryPoint() {
            return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
        }
    }

    @Order(1)
    @Configuration
    public static class HttpBasicAuthentication extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers()
                    .antMatchers(HttpMethod.GET,
                            "/js/**",
                            "/css/**"
                    )
                    .and().httpBasic()
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().csrf().disable();
        }
    }
}
