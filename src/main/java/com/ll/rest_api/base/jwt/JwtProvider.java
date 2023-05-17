package com.ll.rest_api.base.jwt;


import com.ll.rest_api.base.util.Ut;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider { // 토큰을 생성해주는 클래스
    private SecretKey cachedSecretKey;

    @Value("${custom.jwt.secretKey}")
    private String secretKeyPlain; // 시크릿 키 원문을 가져다 쓴다

    private SecretKey _getSecretKey() { // 시크릿 키를 가져온다
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyPlain.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    public SecretKey getSecretKey() { // 시크릿키를 가져오는 메서드
        if (cachedSecretKey == null) cachedSecretKey = _getSecretKey(); // 시크릿 키가 존재하지 않으면 가지고 있는 시크릿 키를 base64인코딩 후 암호화

        return cachedSecretKey;
    }

    public String genToken(Map<String, Object> claims, int seconds) {
        long now = new Date().getTime(); // 현재 시간
        Date accessTokenExpiresIn = new Date(now + 1000L * seconds); // 매개 변수로 들어온 시간을 밀리로 바꿔서 현재 시간에 더해줌

        return Jwts.builder() // jwts 객체를 생성해서 반환 (실제 토큰을 생성하는 부분)
                .claim("body", Ut.json.toStr(claims)) // jwt 의 body 부분에 저장할 claim 을 json
                .setExpiration(accessTokenExpiresIn)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean verify(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey()) // 생성된 시크릿 키를 가져옴
                    .build()
                    .parseClaimsJws(token); // 현재
        } catch (ExpiredJwtException e) {
            System.out.println("유효기간 만료되서 예외 발생"); // 테스트
            System.out.println(e.getMessage());
            return false;
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Map<String, Object> getClaims(String token) {
        String body = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("body", String.class);

        return Ut.json.toMap(body);
    }
}
