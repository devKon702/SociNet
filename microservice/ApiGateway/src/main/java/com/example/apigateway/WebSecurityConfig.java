//package com.example.apigateway;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.cors.reactive.CorsConfigurationSource;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//
//import java.util.List;
//
//@Configuration
//public class WebSecurityConfig {
//    @Bean
//    CorsWebFilter corsWebFilter(){
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedOrigins(List.of("*"));
//        corsConfiguration.setAllowedHeaders(List.of("*"));
//        corsConfiguration.setAllowedMethods(List.of("*"));
//
//        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
//        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
//
//        return new CorsWebFilter((CorsConfigurationSource) urlBasedCorsConfigurationSource);
//    }
//}
