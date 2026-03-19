package com.carincho.backend.usersapp.backendusersapp.auth.filters;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.carincho.backend.usersapp.backendusersapp.models.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import static com.carincho.backend.usersapp.backendusersapp.auth.TokenJwtConfig.*;

//Para agregar el filtro de autenticación JWT, debemos extender la clase UsernamePasswordAuthenticationFilter e 
// implementar los métodos necesarios para manejar la autenticación y generación del token JWT. en contexto REST API,
//  no se utiliza el formulario de login tradicional, por lo que el método attemptAuthentication se puede dejar vacío o implementar 
// según las necesidades específicas de la aplicación. El método successfulAuthentication se encargará de
//  generar el token JWT y enviarlo en la respuesta, mientras que el método unsuccessfulAuthentication manejará los casos de autenticación fallida.
//Por debajo maneja una ruta login, es como un controlador que intercepta las peticiones a esa ruta, y en el método attemptAuthentication se procesa la autenticación, y si es exitosa, se llama al método successfulAuthentication para generar el token JWT y enviarlo en la respuesta. Si la autenticación falla, se llama al método unsuccessfulAuthentication para manejar el error de autenticación.
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        User user = null;
        String username = null;
        String password = null;

        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();

        } catch (JacksonException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);

        return authenticationManager.authenticate(authenticationToken);// Por debajo se ejecuta el jpaUserDetailsService
                                                                       // para validar el usuario y contraseña, si es
                                                                       // correcto, se devuelve un objeto Authentication
                                                                       // con los detalles del usuario autenticado, que
                                                                       // luego se utiliza en el método
                                                                       // successfulAuthentication para generar el token
                                                                       // JWT. Si la autenticación falla, se lanza una
                                                                       // excepción que es manejada en el método
                                                                       // unsuccessfulAuthentication.
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal())
                .getUsername();

               Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();

               boolean isAdmin = roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        // String originalInput = SECRET_KEY + "." + username; esto es de forma artesanal spring security
        // String token = Base64.getEncoder().encodeToString(originalInput.getBytes());

        // Se crea el token con jwt

        Claims claims = Jwts.claims()
        .add("authorities", new ObjectMapper().writeValueAsString(roles))
        .add("isAdmin", isAdmin)
        .add("username", username)
        .build();
        

        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .signWith(SECRET_KEY)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .compact();

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("username", username);
        body.put("message", String.format("Hola %s, Authentication successful", username));

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Authentication failed: men ");
        body.put("error", failed.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType("application/json");
    }

}
