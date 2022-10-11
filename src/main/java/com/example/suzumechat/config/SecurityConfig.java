package com.example.suzumechat.config;

import org.springframework.web.filter.CorsFilter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    Environment env;

    // originally `Password` object
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Spring Security does not apply
        // TODO: envによって、本番環境と切り替える方法は?
        // 本番では h2-consoleはoff
        return (web) -> web.ignoring().antMatchers("/h2-console/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.antMatchers("/csrfToken").permitAll()
                .antMatchers("/createChannel").permitAll()
                .antMatchers("/healthcheck").permitAll()
        // .anyRequest().authenticated()
        );

        // http.csrf().disable(); // commented code for quick debugging
        val webSocketChatEndPoint = env.getProperty("ws.endpoint");
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
        config.addAllowedOrigin(env.getProperty("front.url"));
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
