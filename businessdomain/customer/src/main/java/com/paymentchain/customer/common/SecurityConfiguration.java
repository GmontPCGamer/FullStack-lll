/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.common;
/*
import java.time.Duration;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
*/
/**
 *
 * @author Duoc
 */

/*@Configuration
@EnableWebSecurity*/
public class SecurityConfiguration {
    
    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        /*return http.authorizeHttpRequests(request-> request.anyRequest()
                .authenticated())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .build();
        
              return http
        .csrf(csrf -> csrf.disable()) // Deshabilitado para facilitar pruebas en Swagger/Postman
        .authorizeHttpRequests(auth -> auth
            // 1. Rutas de Swagger (Siempre primero y públicas)
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            
            // 2. Reglas específicas por método y patrón
            // Nota: Usa "**" para capturar sub-rutas, ej: /customers/1/orders
          // .requestMatchers(HttpMethod.GET, "/*customer/**").hasRole("ADMIN")
           // .requestMatchers(HttpMethod.POST, "/*customer/**").authenticated()
            
            // 3. Regla global (Siempre al final)
           // .anyRequest().authenticated()
       // )
       // .formLogin(Customizer.withDefaults())
        //.httpBasic(Customizer.withDefaults())
        //.build();
    //}
    /*
     @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cc = new CorsConfiguration();
       
        cc.setAllowedHeaders(Arrays.asList("Origin,Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Authorization"));
        cc.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
       
        cc.setAllowedOrigins(Arrays.asList("/*"));
        
        cc.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "PUT", "PATCH"));
        
        cc.addAllowedOriginPattern("*");       

        
        cc.setMaxAge(Duration.ZERO);
        cc.setAllowCredentials(Boolean.TRUE);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cc);
        return source;
    }*/
}
