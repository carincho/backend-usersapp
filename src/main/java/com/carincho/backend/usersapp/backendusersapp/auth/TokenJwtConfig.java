package com.carincho.backend.usersapp.backendusersapp.auth;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;

public class TokenJwtConfig {

    // public final static String SECRET_KEY =  "algun_token_con_alguna_frase_secreta";
    public final static SecretKey SECRET_KEY =  Jwts.SIG.HS256.key().build();//LLAVE SECRETA PARA FIRMAR EL TOKEN, SE GENERA DE MANERA AUTOMATICA CON LA CLASE Keys DE LA LIBRERIA JJWT, UTILIZANDO EL ALGORITMO HS256 PARA LA FIRMA DEL TOKEN. ESTA CLAVE SE UTILIZA PARA VERIFICAR LA AUTENTICIDAD DEL TOKEN CUANDO SE RECIBE EN LAS SOLICITUDES POSTERIORES.
    public final static String PREFIX_TOKEN =  "Bearer ";
    public final static String HEADER_AUTHORIZATION =  "Authorization";
    

    
    
}
