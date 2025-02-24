package com.hackathon.blockchain.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

public class SignatureUtil {
    public static String signature(String dataToSign, PrivateKey issuerPrivateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(issuerPrivateKey);
        signature.update(dataToSign.getBytes());
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    public static Boolean verifySignature(String dataToSign, String digitalSignature, PublicKey issuerPublicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(issuerPublicKey);
        signature.update(dataToSign.getBytes());
        byte[] signatureBytes = Base64.getDecoder().decode(digitalSignature);
        return signature.verify(signatureBytes);
    }
}
