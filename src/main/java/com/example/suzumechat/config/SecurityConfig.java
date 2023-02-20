package com.example.suzumechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${ws.endpoint}")
    private String webSocketChatEndPoint;

    @Value("${front.url}")
    private String frontUrl;

    // originally `Password` object
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // @Bean
    // public WebSecurityCustomizer webSecurityCustomizer() {
    // Spring Security does not apply
    // return (web) -> web.ignoring().antMatchers("/h2-console/**");
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.antMatchers("/csrfToken").permitAll()
            .antMatchers("/createChannel").permitAll()
            .antMatchers("/healthcheck").permitAll()
        // .anyRequest().authenticated()
        );

        // http.csrf().disable(); // commented code for quick debugging
        http.csrf()
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringAntMatchers(webSocketChatEndPoint + "/**");
        http.headers().frameOptions().sameOrigin();

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {

        CorsConfiguration config = new CorsConfiguration();


        // Access-Control-Allow-Origin
        config.addAllowedOrigin(frontUrl);
        // config.addAllowedOrigin(CorsConfiguration.ALL);
        // Access-Control-Allow-Methods
        config.addAllowedMethod(CorsConfiguration.ALL);
        // Access-Control-Allow-Headers
        config.addAllowedHeader(CorsConfiguration.ALL);
        // cookieをcross domainで共有するため?
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean =
            new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
