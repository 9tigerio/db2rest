package com.homihq.db2rest.jdbc.config.driver;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

@Slf4j
public class EnvVarSSLSocketFactory extends javax.net.ssl.SSLSocketFactory {

    private static final String HTTPS_ALGORITHM = "HTTPS";

    private final SSLSocketFactory factory;
    private final X509Certificate cert;

    public EnvVarSSLSocketFactory() throws Exception {
        // Get certificate content from environment variable
        String certContent = System.getenv("PG_CERT_CONTENT");
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

        // Create certificate from the content
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        cert = (X509Certificate) cf.generateCertificate(
                new ByteArrayInputStream(certBytes)
        );

        // Validate certificate immediately
        validateCertificate(cert);

        // Create KeyStore and load the certificate
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("postgresql", cert);

        // Create TrustManagerFactory with custom TrustManager
        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // Create SSLContext with custom TrustManager that includes hostname verification
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers =
                createTrustManagersWithHostnameVerification(tmf.getTrustManagers());
        sslContext.init(null, trustManagers, null);

        // Get the factory
        this.factory = sslContext.getSocketFactory();
    }

    private void validateCertificate(X509Certificate cert) throws CertificateException {
        // Check certificate validity period
        try {
            cert.checkValidity(new Date());
        } catch (CertificateExpiredException e) {
            throw new CertificateException("Certificate has expired", e);
        } catch (CertificateNotYetValidException e) {
            throw new CertificateException("Certificate is not yet valid", e);
        }

        // Additional basic validation
        if (cert.getSubjectX500Principal() == null ||
                cert.getSubjectX500Principal().getName().isEmpty()) {
            throw new CertificateException("Certificate has invalid subject");
        }

        // Check for basic constraints if it's a CA certificate
        int basicConstraints = cert.getBasicConstraints();
        boolean isNotCA = basicConstraints != -1;
        boolean isInvalid = basicConstraints < 0;
        if (isNotCA && isInvalid) {
            throw new CertificateException("Certificate has invalid subject");
        }

        // Log certificate information for debugging
        log.debug("Certificate validated successfully:");
        log.debug("Subject: {}", cert.getSubjectX500Principal().getName());
        log.debug("Issuer: {}", cert.getIssuerX500Principal().getName());
        log.debug("Valid from: {}", cert.getNotBefore());
        log.debug("Valid until: {}", cert.getNotAfter());
    }

    private TrustManager[] createTrustManagersWithHostnameVerification(TrustManager[] trustManagers) {
        TrustManager[] wrappedTrustManagers = new TrustManager[trustManagers.length];

        for (int i = 0; i < trustManagers.length; i++) {
            if (trustManagers[i] instanceof X509TrustManager) {
                int finalI = i;
                wrappedTrustManagers[i] = new X509TrustManager() {
                    private final X509TrustManager delegate =
                            (X509TrustManager) trustManagers[finalI];

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        delegate.checkClientTrusted(chain, authType);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        delegate.checkServerTrusted(chain, authType);

                        // Additional custom validation
                        for (X509Certificate certificate : chain) {
                            validateCertificate(certificate);
                        }
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return delegate.getAcceptedIssuers();
                    }
                };
            } else {
                wrappedTrustManagers[i] = trustManagers[i];
            }
        }

        return wrappedTrustManagers;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws java.io.IOException {
        SSLSocket sslSocket = (SSLSocket) factory.createSocket(socket, host, port, autoClose);

        // Enable hostname verification
        SSLParameters sslParams = sslSocket.getSSLParameters();
        sslParams.setEndpointIdentificationAlgorithm(HTTPS_ALGORITHM);
        sslSocket.setSSLParameters(sslParams);

        return sslSocket;
    }

    // Other createSocket methods with hostname verification
    @Override
    public Socket createSocket(String host, int port) throws java.io.IOException {
        SSLSocket sslSocket = (SSLSocket) factory.createSocket(host, port);
        SSLParameters sslParams = sslSocket.getSSLParameters();
        sslParams.setEndpointIdentificationAlgorithm(HTTPS_ALGORITHM);
        sslSocket.setSSLParameters(sslParams);
        return sslSocket;
    }

    @Override
    public Socket createSocket(String host, int port, java.net.InetAddress localHost, int localPort)
            throws java.io.IOException {
        SSLSocket sslSocket = (SSLSocket) factory.createSocket(host, port, localHost, localPort);
        SSLParameters sslParams = sslSocket.getSSLParameters();
        sslParams.setEndpointIdentificationAlgorithm(HTTPS_ALGORITHM);
        sslSocket.setSSLParameters(sslParams);
        return sslSocket;
    }

    @Override
    public Socket createSocket(java.net.InetAddress host, int port) throws java.io.IOException {
        SSLSocket sslSocket = (SSLSocket) factory.createSocket(host, port);
        SSLParameters sslParams = sslSocket.getSSLParameters();
        sslParams.setEndpointIdentificationAlgorithm(HTTPS_ALGORITHM);
        sslSocket.setSSLParameters(sslParams);
        return sslSocket;
    }

    @Override
    public Socket createSocket(java.net.InetAddress address, int port, java.net.InetAddress localAddress,
                               int localPort) throws java.io.IOException {
        SSLSocket sslSocket =
                (SSLSocket) factory.createSocket(address, port, localAddress, localPort);
        SSLParameters sslParams = sslSocket.getSSLParameters();
        sslParams.setEndpointIdentificationAlgorithm(HTTPS_ALGORITHM);
        sslSocket.setSSLParameters(sslParams);
        return sslSocket;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return factory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }
}
