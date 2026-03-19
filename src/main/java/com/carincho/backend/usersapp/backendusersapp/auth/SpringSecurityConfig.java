package com.carincho.backend.usersapp.backendusersapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.carincho.backend.usersapp.backendusersapp.auth.filters.JwtAuthenticationFilter;
import com.carincho.backend.usersapp.backendusersapp.auth.filters.JwtValidationFilter;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    PasswordEncoder passwordEncoder() {
        // return  NoOpPasswordEncoder.getInstance();//En este caso se utiliza un encoder de contraseñas que no realiza ninguna codificación, lo cual es inseguro para aplicaciones en producción. En un entorno real, se recomienda utilizar un encoder más seguro, como BCryptPasswordEncoder, para proteger las contraseñas de los usuarios.
        return new BCryptPasswordEncoder();//GENERA VALORES ENCRIPTADOS, PARA PROTEGER LAS CONTRASEÑAS DE LOS USUARIOS. EN ESTE CASO SE UTILIZA EL ALGORITMO DE ENCRIPTACIÓN BCRYPT, QUE ES UN ALGORITMO DE HASHING FUERTE Y SEGURO PARA PROTEGER LAS CONTRASEÑAS DE LOS USUARIOS. CUANDO SE UTILIZA ESTE ENCODER, LAS CONTRASEÑAS SE GUARDAN EN LA BASE DE DATOS DE FORMA ENCRIPTADA, LO QUE AUMENTA LA SEGURIDAD DE LA APLICACIÓN.
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(authorize -> 
            authorize.requestMatchers(HttpMethod.GET, "/users").permitAll()
            .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyRole("USER", "ADMIN") //AQUI SE OMITE LA PALABRA ROLE ANY ROLE ES VARIOS ROLES
            .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")//HASROLE SOLO ES UN ROLE
            .requestMatchers ("/users/**").hasRole("ADMIN")//TAMBIEN SE PUEDE USAR ASI 
            // .requestMatchers(HttpMethod.PUT, "/users/{id}").hasRole("ADMIN")
            // .requestMatchers(HttpMethod.DELETE, "/users/{id}").hasRole("ADMIN")
                .anyRequest()
                .authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()))//Se agrega el filtro a la configuracion de spring security, este filtro se encargará de interceptar las peticiones de autenticación y generar el token JWT.
                .addFilter(new JwtValidationFilter(authenticationConfiguration.getAuthenticationManager()))
                .csrf(config -> config.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

}
