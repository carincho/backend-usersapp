package com.carincho.backend.usersapp.backendusersapp.auth.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;
import static com.carincho.backend.usersapp.backendusersapp.auth.TokenJwtConfig.*;

//IMPLEMENTANDO FILTRO PARA VALIDAR TOKEN JWTVALIDATIONFILTER
public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(HEADER_AUTHORIZATION);
        if (header == null || !header.startsWith(PREFIX_TOKEN)) {

            chain.doFilter(request, response);
            return;
        }

        // Esto se hizo de forma artesanal

        // byte [] tokenDecodeBytes = Base64.getDecoder().decode(token);
        // String tokenDecodeString = new String(tokenDecodeBytes);
        // String [] tokenArray = tokenDecodeString.split("\\.");

        // String secret = tokenArray[0];
        // String username = tokenArray[1];

        // if( SECRET_KEY.equals(secret) ) {

        String token = header.replace(PREFIX_TOKEN, "");

        try {

            Claims claims =  Jwts
            .parser()
            .verifyWith(SECRET_KEY)
            .build()
            .parseSignedClaims(token)
            .getPayload();

            Object authoritiesClaims = claims.get("authorities");

            String username = claims.getSubject();
            Object username2 = claims.get("username");//para esto se agrega el claims en JwtAuthenticationFilter linea 96

            System.out.println("username: " + username);
            System.out.println("username2: " + username2);

            Collection<? extends GrantedAuthority> authorities = Arrays
            .asList(new ObjectMapper()
            .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                    null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            chain.doFilter(request, response);
        } catch (JwtException e) {

            Map<String, Object> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "Token JWT no válido");
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");

        }

    }

}
