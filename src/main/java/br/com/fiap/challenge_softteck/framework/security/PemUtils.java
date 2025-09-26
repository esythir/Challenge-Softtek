package br.com.fiap.challenge_softteck.framework.security;

import java.security.KeyFactory;
import java.security.interfaces.*;
import java.security.spec.*;
import java.util.Base64;

public final class PemUtils {

    public static RSAPublicKey parsePublicKey(String pem) {
        try {
            String content = pem.lines()
                    .filter(l -> !l.startsWith("-----"))
                    .reduce("", String::concat);
            byte[] decoded = Base64.getDecoder().decode(content);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse public key", e);
        }
    }

    public static RSAPrivateKey parsePrivateKey(String pem) {
        try {
            String content = pem.lines()
                    .filter(l -> !l.startsWith("-----"))
                    .reduce("", String::concat);
            byte[] decoded = Base64.getDecoder().decode(content);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse private key", e);
        }
    }
}
