package com.hackathon.blockchain.utils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class PemUtil {
    public static String toPEMFormat(byte[] encodedKey, String keyType) {
        String base64EncodedKey = Base64.getEncoder().encodeToString(encodedKey);
        StringBuilder pemStringBuilder = new StringBuilder();
        pemStringBuilder.append(String.format("-----BEGIN %s KEY-----\n", keyType));

        int offset = 0;
        while (offset < base64EncodedKey.length()) {
            int end = Math.min(offset + 64, base64EncodedKey.length());
            pemStringBuilder.append(base64EncodedKey.substring(offset, end) + "\n");
            offset = end;
        }

        pemStringBuilder.append(String.format("-----END %s KEY-----\n", keyType));
        return pemStringBuilder.toString();
    }

    public static String toPEMFormat(PublicKey publicKey, String keyType) {
        return toPEMFormat(publicKey.getEncoded(), keyType.toUpperCase());
    }

    public static String toPEMFormat(PrivateKey privateKey, String keyType) {
        return toPEMFormat(privateKey.getEncoded(), keyType.toUpperCase());
    }
}
