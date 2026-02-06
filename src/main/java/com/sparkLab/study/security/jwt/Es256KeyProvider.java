package com.sparkLab.study.security.jwt;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import org.springframework.stereotype.Component;
import java.security.interfaces.ECPrivateKey;
import jakarta.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPublicKey;
import java.util.UUID;

@Component
public class Es256KeyProvider {

    private ECKey ecKey;

    @PostConstruct
    public void init() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(Curve.P_256.toECParameterSpec());
            KeyPair keyPair = generator.generateKeyPair();

            this.ecKey = new ECKey.Builder(Curve.P_256, (ECPublicKey) keyPair.getPublic())
                    .privateKey((ECPrivateKey) keyPair.getPrivate())
                    .keyID(UUID.randomUUID().toString())
                    .build();

        } catch (Exception e) {
            throw new IllegalStateException("EC_KEY_INIT_FAILED", e);
        }
    }

    public ECKey getKey() {
        return ecKey;
    }

    public ECKey getPublicKey() {
        return ecKey.toPublicJWK();
    }
}
