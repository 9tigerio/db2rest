package com.homihq.db2rest.auth.jwt;

import com.auth0.jwt.algorithms.Algorithm;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class AlgorithmFactory {

    public static Algorithm getAlgorithm(JwtProperties jwtProperties) throws Exception{
        switch (jwtProperties.getAlgorithm()) {
            case "HMAC256" -> {
                return Algorithm.HMAC256(jwtProperties.getSecret());
            }
            case "HMAC384" -> {
                return Algorithm.HMAC384(jwtProperties.getSecret());
            }
            case "HMAC512" -> {
                return Algorithm.HMAC512(jwtProperties.getSecret());
            }
            case "RSA256" -> {
                if(jwtProperties.isEnvironmentBasedProperty()) {
                    return Algorithm.RSA256(
                            (RSAPublicKey) PemUtils.readPublicKeyFromFile(jwtProperties.publicKeyFile,
                                    jwtProperties.algorithm),
                            (RSAPrivateKey) PemUtils.readPrivateKeyFromFile(jwtProperties.privateKeyFile,
                                    jwtProperties.algorithm));
                }
                else if(jwtProperties.isEnvironmentBasedProperty()) {
                    return Algorithm.RSA256(
                            (RSAPublicKey) PemUtils.readPublicKeyFromEnvironment(jwtProperties.envPublicKey,
                                    jwtProperties.algorithm),
                            (RSAPrivateKey) PemUtils.readPublicKeyFromEnvironment(jwtProperties.envPrivateKey,
                                    jwtProperties.algorithm));
                }
                else {
                    throw new RuntimeException(jwtProperties.getAlgorithm() + " unable to load Public/Private Key.");
                }
            }
            case "RSA384" -> {
                if(jwtProperties.isEnvironmentBasedProperty()) {
                    return Algorithm.RSA384(
                            (RSAPublicKey) PemUtils.readPublicKeyFromFile(jwtProperties.publicKeyFile,
                                    jwtProperties.algorithm),
                            (RSAPrivateKey) PemUtils.readPrivateKeyFromFile(jwtProperties.privateKeyFile,
                                    jwtProperties.algorithm));
                }
                else if(jwtProperties.isEnvironmentBasedProperty()) {
                    return Algorithm.RSA384(
                            (RSAPublicKey) PemUtils.readPublicKeyFromEnvironment(jwtProperties.envPublicKey,
                                    jwtProperties.algorithm),
                            (RSAPrivateKey) PemUtils.readPublicKeyFromEnvironment(jwtProperties.envPrivateKey,
                                    jwtProperties.algorithm));
                }
                else {
                    throw new RuntimeException(jwtProperties.getAlgorithm() + " unable to load Public/Private Key.");
                }
            }
            case "RSA512" -> {
                if(jwtProperties.isEnvironmentBasedProperty()) {
                    return Algorithm.RSA512(
                            (RSAPublicKey) PemUtils.readPublicKeyFromFile(jwtProperties.publicKeyFile,
                                    jwtProperties.algorithm),
                            (RSAPrivateKey) PemUtils.readPrivateKeyFromFile(jwtProperties.privateKeyFile,
                                    jwtProperties.algorithm));
                }
                else if(jwtProperties.isEnvironmentBasedProperty()) {
                    return Algorithm.RSA512(
                            (RSAPublicKey) PemUtils.readPublicKeyFromEnvironment(jwtProperties.envPublicKey,
                                    jwtProperties.algorithm),
                            (RSAPrivateKey) PemUtils.readPublicKeyFromEnvironment(jwtProperties.envPrivateKey,
                                    jwtProperties.algorithm));
                }
                else {
                    throw new RuntimeException(jwtProperties.getAlgorithm() + " unable to load Public/Private Key.");
                }
            }

            default -> throw new RuntimeException(jwtProperties.getAlgorithm() + " is not supported.");
        }
    }
}
