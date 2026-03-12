package com.creditwise.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebhookSecurityService {

    @Value("${paystack.secret.key}")
    private String paystackSecret;

    public boolean isValidSignature(String payload, String signature) {

        try {

            Mac sha512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec key = new SecretKeySpec(
                    paystackSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512");

            sha512.init(key);

            byte[] hash = sha512.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            String computedHash = bytesToHex(hash);

            return MessageDigest.isEqual(
                    computedHash.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {

        StringBuilder hex = new StringBuilder(bytes.length * 2);

        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }
}