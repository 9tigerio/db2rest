package com.homihq.db2rest.jdbc.config.driver;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Slf4j
public class EnvVarSSLSocketFactory extends javax.net.ssl.SSLSocketFactory {
    private final SSLSocketFactory factory;

    public EnvVarSSLSocketFactory() throws Exception {

        log.info("Loading certificate from environment variables");

        // Get certificate content from environment variable
        String certContent = System.getenv("PG_CERT_CONTENT");

        log.info("cert content: {}", certContent);

        if (certContent == null || certContent.trim().isEmpty()) {
            throw new IllegalStateException("PG_CERT_CONTENT environment variable is not set");
        }

        // Decode base64 if the cert is base64 encoded
        byte[] certBytes;
        try {
            certBytes = Base64.getDecoder().decode(certContent);
        } catch (IllegalArgumentException e) {
            // If not base64, use raw content
            certBytes = certContent.getBytes(StandardCharsets.UTF_8);
        }

        // Create KeyStore and load the certificate
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);

        // Create certificate from the content
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(
            new ByteArrayInputStream(certBytes)
        );

        // Add certificate to keystore
        keyStore.setCertificateEntry("postgresql", cert);

        // Create TrustManagerFactory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // Create SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        // Get the factory
        this.factory = sslContext.getSocketFactory();
    }

    // Delegate all SSLSocketFactory methods to the internal factory
    @Override
    public String[] getDefaultCipherSuites() {
        return factory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

    @Override
    public java.net.Socket createSocket(java.net.Socket socket, String host, int port, boolean autoClose)
            throws java.io.IOException {
        return factory.createSocket(socket, host, port, autoClose);
    }

    @Override
    public java.net.Socket createSocket(String host, int port) throws java.io.IOException {
        return factory.createSocket(host, port);
    }

    @Override
    public java.net.Socket createSocket(String host, int port, java.net.InetAddress localHost, int localPort)
            throws java.io.IOException {
        return factory.createSocket(host, port, localHost, localPort);
    }

    @Override
    public java.net.Socket createSocket(java.net.InetAddress host, int port) throws java.io.IOException {
        return factory.createSocket(host, port);
    }

    @Override
    public java.net.Socket createSocket(java.net.InetAddress address, int port, java.net.InetAddress localAddress,
            int localPort) throws java.io.IOException {
        return factory.createSocket(address, port, localAddress, localPort);
    }
}
