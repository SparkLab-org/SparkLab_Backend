package com.sparkLab.study.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sparkLab.study.security.auth.constant.AccountRole;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class TokenService {

    private final Es256KeyProvider keyProvider;

    public TokenService(Es256KeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    public String issueToken(String accountId, AccountRole role) {
        ECKey key = keyProvider.getKey();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(accountId)
                .issuer("https://auth.example.com")
                .claim("roles", List.of(role))
                .issueTime(new Date())
                .expirationTime(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 15)
                )
                .build();

        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.ES256)
                        .keyID(key.getKeyID()) // kid
                        .build(),
                claims
        );

        try {
            jwt.sign(new ECDSASigner(key.toECPrivateKey()));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("JWT_SIGN_FAILED", e);
        }
    }
}
