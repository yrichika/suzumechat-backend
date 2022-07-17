package com.example.suzumechat.config;

import org.springframework.web.filter.CorsFilter;
import org.apache.http.client.methods.HttpOptions;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    
    // originally `Password` object
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .antMatchers("/h2-console/**")
            .antMatchers("/healthcheck");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .antMatchers("/").permitAll()
        );
        // http.csrf().disable();
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {

        CorsConfiguration config = new CorsConfiguration();
        // config.setAllowCredentials(true);

        // Access-Control-Allow-Origin
        // config.addAllowedOrigin("http://localhost:3000");
        // config.addAllowedOrigin(CorsConfiguration.ALL);
        // Access-Control-Allow-Methods
        // config.addAllowedMethod(CorsConfiguration.ALL);
        // Access-Control-Allow-Headers
        // config.addAllowedHeader(CorsConfiguration.ALL);
        // Allow all
        config.applyPermitDefaultValues();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
