package com.homihq.db2rest.jdbc.config.driver;

import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.PGProperty;
import org.postgresql.jdbc.SslMode;
import org.postgresql.ssl.LazyKeyManager;
import org.postgresql.ssl.NonValidatingFactory.NonValidatingTM;
import org.postgresql.ssl.PKCS12KeyManager;
import org.postgresql.ssl.WrappedFactory;
import org.postgresql.util.GT;
import org.postgresql.util.ObjectFactory;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.postgresql.util.internal.Nullness;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.Locale;
import java.util.Properties;



/**
 * Provide an SSLSocketFactory that is compatible with the libpq behaviour.
 */
public class PgSingleCertValidatingFactory extends WrappedFactory {

    private static final String ENV_PREFIX = "env:";
    private static final String SYS_PROP_PREFIX = "sys:";

    @Nullable
    KeyManager km;
    boolean defaultfile;

    /**
     * @param info the connection parameters The following parameters are used:
     *             sslmode,sslcert,sslkey,sslrootcert,sslhostnameverifier,sslpasswordcallback,sslpassword
     * @throws PSQLException if security error appears when initializing factory
     */
    public PgSingleCertValidatingFactory(Properties info) throws Exception {

        try {
            // Determining the default file location
            String pathsep = System.getProperty("file.separator");
            String defaultdir;

            if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")) { // It is Windows
                defaultdir = System.getenv("APPDATA") + pathsep + "postgresql" + pathsep;
            } else {
                defaultdir = System.getProperty("user.home") + pathsep + ".postgresql" + pathsep;
            }

            String sslkeyfile = PGProperty.SSL_KEY.getOrDefault(info);
            if (sslkeyfile == null) { // Fall back to default
                defaultfile = true;
                sslkeyfile = defaultdir + "postgresql.pk8";
            }

            if (sslkeyfile.endsWith(".p12") || sslkeyfile.endsWith(".pfx")) {
                initP12(sslkeyfile, info);
            } else {
                initPk8(sslkeyfile, defaultdir, info);
            }

            TrustManager[] tm;
            SslMode sslMode = SslMode.of(info);
            if (!sslMode.verifyCertificate()) {
                // server validation is not required
                tm = new TrustManager[] {new NonValidatingTM()};
            } else {
                InputStream fis;
                String sslFactoryArg = info.getProperty("sslfactoryarg");
                String sslrootcertfile = PGProperty.SSL_ROOT_CERT.getOrDefault(info);

                //before default try system or environment
                if (sslFactoryArg.startsWith(ENV_PREFIX)) {
                    String name = sslFactoryArg.substring(ENV_PREFIX.length());
                    String cert = base64Encode(System.getenv(name));
                    if (cert == null || "".equals(cert)) {
                        throw new RuntimeException(GT.tr(
                                "The environment variable containing the server's SSL certificate must not be empty."));
                    }

                    fis = new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8));
                } else if (sslFactoryArg.startsWith(SYS_PROP_PREFIX)) {
                    String name = sslFactoryArg.substring(SYS_PROP_PREFIX.length());
                    String cert = base64Encode(System.getProperty(name));
                    if (cert == null || "".equals(cert)) {
                        throw new RuntimeException(GT.tr(
                                "The system property containing the server's SSL certificate must not be empty."));
                    }

                    fis = new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8));
                } else {
                    if (sslrootcertfile == null) { // Fall back to default
                        sslrootcertfile = defaultdir + "root.crt";
                    }

                    fis = getCertFis(sslrootcertfile);
                }
                tm = initTrustManagerFactory(fis, sslrootcertfile).getTrustManagers();
            }

            SSLContext ctx = initContext(tm);

            factory = ctx.getSocketFactory();
        } catch (NoSuchAlgorithmException ex) {
            throw new PSQLException(GT.tr("Could not find a java cryptographic algorithm: {0}.",
                    ex.getMessage()), PSQLState.CONNECTION_FAILURE, ex);
        }
    }

    private CallbackHandler getCallbackHandler(
            @UnderInitialization(WrappedFactory.class)PgSingleCertValidatingFactory this,
            Properties info) throws PSQLException {
        // Determine the callback handler
        CallbackHandler cbh;
        String sslpasswordcallback = PGProperty.SSL_PASSWORD_CALLBACK.getOrDefault(info);
        if (sslpasswordcallback != null) {
            try {
                cbh =
                        ObjectFactory.instantiate(CallbackHandler.class, sslpasswordcallback, info, false, null);
            } catch (Exception e) {
                throw new PSQLException(
                        GT.tr("The password callback class provided {0} could not be instantiated.",
                                sslpasswordcallback),
                        PSQLState.CONNECTION_FAILURE, e);
            }
        } else {
            cbh = new ConsoleCallbackHandler(PGProperty.SSL_PASSWORD.getOrDefault(info));
        }
        return cbh;
    }

    private void initPk8(
            @UnderInitialization(WrappedFactory.class)PgSingleCertValidatingFactory this,
            String sslkeyfile, String defaultdir, Properties info) throws PSQLException {

        // Load the client's certificate and key
        String sslcertfile = PGProperty.SSL_CERT.getOrDefault(info);
        if (sslcertfile == null) { // Fall back to default
            defaultfile = true;
            sslcertfile = defaultdir + "postgresql.crt";
        }

        // If the properties are empty, give null to prevent client key selection
        km = new LazyKeyManager(("".equals(sslcertfile) ? null : sslcertfile),
                ("".equals(sslkeyfile) ? null : sslkeyfile), getCallbackHandler(info), defaultfile);
    }

    private void initP12(
            @UnderInitialization(WrappedFactory.class)PgSingleCertValidatingFactory this,
            String sslkeyfile, Properties info) throws PSQLException {
        km = new PKCS12KeyManager(sslkeyfile, getCallbackHandler(info));
    }

    private KeyStore getKeyStoreInstance() throws Exception {
        try {
            return KeyStore.getInstance("jks");
        } catch (KeyStoreException e) {
            // this should never happen
            throw new NoSuchAlgorithmException("jks KeyStore not available");
        }
    }

    private FileInputStream getCertFis(String sslrootcertfile) throws Exception {
        try {
            return new FileInputStream(sslrootcertfile); // NOSONAR
        } catch (FileNotFoundException ex) {
            throw new PSQLException(
                    GT.tr("Could not open SSL root certificate file {0}.", sslrootcertfile),
                    PSQLState.CONNECTION_FAILURE, ex);
        }
    }

    private TrustManagerFactory initTrustManagerFactory(InputStream fis, String sslrootcertfile) throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
        KeyStore ks = getKeyStoreInstance();

        try (fis) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // Certificate[] certs = cf.generateCertificates(fis).toArray(new Certificate[]{}); //Does
            // not work in java 1.4
            Object[] certs = cf.generateCertificates(fis).toArray(new Certificate[] {});
            ks.load(null, null);
            for (int i = 0; i < certs.length; i++) {
                ks.setCertificateEntry("cert" + i, (Certificate) certs[i]);
            }
            tmf.init(ks);
            return tmf;
        } catch (IOException ioex) {
            throw new PSQLException(
                    GT.tr("Could not read SSL root certificate file {0}.", sslrootcertfile),
                    PSQLState.CONNECTION_FAILURE, ioex);
        } catch (GeneralSecurityException gsex) {
            throw new PSQLException(
                    GT.tr("Loading the SSL root certificate {0} into a TrustManager failed.",
                            sslrootcertfile),
                    PSQLState.CONNECTION_FAILURE, gsex);
        }
    }

    private SSLContext initContext(TrustManager[] tm) throws Exception {
        SSLContext ctx = SSLContext.getInstance("TLS"); // or "SSL" ?
        // finally we can initialize the context
        try {
            ctx.init(this.km == null ? null : new KeyManager[] {this.km}, tm, null);
            return ctx;
        } catch (KeyManagementException ex) {
            throw new PSQLException(GT.tr("Could not initialize SSL context."),
                    PSQLState.CONNECTION_FAILURE, ex);
        }
    }

    public String base64Encode(String p10) {
        return new String(Base64.getDecoder().decode(p10));
    }

    /**
     * Propagates any exception from {@link LazyKeyManager}.
     *
     * @throws PSQLException if there is an exception to propagate
     */
    public void throwKeyManagerException() throws PSQLException {
        if (km != null) {
            if (km instanceof LazyKeyManager lazyKeyManager) {
                lazyKeyManager.throwKeyManagerException();
            }
            if (km instanceof PKCS12KeyManager pkcs12KeyManager) {
                pkcs12KeyManager.throwKeyManagerException();
            }
        }
    }

    /**
     * A CallbackHandler that reads the password from the console or returns the password given to its
     * constructor.
     */
    public static class ConsoleCallbackHandler implements CallbackHandler {

        private char @Nullable [] password;

        ConsoleCallbackHandler(@Nullable String password) {
            if (password != null) {
                this.password = password.toCharArray();
            }
        }

        /**
         * Handles the callbacks.
         *
         * @param callbacks The callbacks to handle
         * @throws UnsupportedCallbackException If the console is not available or other than
         *                                      PasswordCallback is supplied
         */
        @Override
        public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
            Console cons = System.console();
            if (cons == null && this.password == null) {
                throw new UnsupportedCallbackException(callbacks[0], "Console is not available");
            }
            for (Callback callback : callbacks) {
                if (!(callback instanceof PasswordCallback pwdCallback)) {
                    throw new UnsupportedCallbackException(callback);
                }
                if (this.password != null) {
                    pwdCallback.setPassword(this.password);
                    continue;
                }
                // It is used instead of cons.readPassword(prompt), because the prompt may contain '%'
                // characters
                pwdCallback.setPassword(
                        Nullness.castNonNull(cons, "System.console()")
                                .readPassword("%s", pwdCallback.getPrompt())
                );
            }
        }
    }
}



