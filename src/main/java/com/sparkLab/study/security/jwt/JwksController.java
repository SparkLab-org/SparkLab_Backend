package com.sparkLab.study.security.jwt;

import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JwksController {

    private final Es256KeyProvider keyProvider;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {

        return new JWKSet(List.of(keyProvider.getKey().toPublicJWK()))
                .toJSONObject();
    }

}

