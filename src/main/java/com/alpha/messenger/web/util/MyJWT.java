package com.alpha.messenger.web.util;

import com.alpha.messenger.persistance.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.data.annotation.Id;

import java.util.Calendar;
import java.util.Date;

public class MyJWT {

    private static final int EXPIRATION = 1000 * 60 * 60 * 24;
    private String token;

    public MyJWT() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MyJWT(String token) {
        this.token = token;
    }

    public String generate(User user) {
        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + EXPIRATION);
        this.token = JWT.create()
                .withSubject(user.getUsername())
                .withIssuer("pmr")
                .withClaim("email", user.getMail())
                .withExpiresAt(tomorrow)
                .sign(Algorithm.HMAC256("alpha"));
        return token;
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public boolean isExpirated() {
        try {
            DecodedJWT decode = JWT.decode(this.token);
            Date today = new Date();
            return !decode.getExpiresAt().after(today);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}