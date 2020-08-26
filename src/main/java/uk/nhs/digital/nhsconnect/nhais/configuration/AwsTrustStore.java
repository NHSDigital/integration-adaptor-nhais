package uk.nhs.digital.nhsconnect.nhais.configuration;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class AwsTrustStore {

    @SneakyThrows
    public static void addToDefault(String trustStorePath, String trustStorePassword) {
        final X509TrustManager defaultTrustManager = getDefaultTrustManager();
        final X509TrustManager awsTrustManager = getAwsTrustManager(trustStorePath, trustStorePassword);
        X509TrustManager combinedTrustManager = new CombinedTrustManager(awsTrustManager, defaultTrustManager);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]
            {
                combinedTrustManager
            },null);
        SSLContext.setDefault(sslContext);
    }

    @SneakyThrows
    private static X509TrustManager getDefaultTrustManager() {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null); // Using null here initialises the TMF with the default trust store.

        for (TrustManager tm : trustManagerFactory.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                return (X509TrustManager) tm;
            }
        }
        throw new IllegalStateException("Cannot find trust manager");
    }

    @SneakyThrows
    private static X509TrustManager getAwsTrustManager(String trustStorePath, String trustStorePassword) {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null); // Using null here initialises the TMF with the default trust store.

        try (FileInputStream myKeys = new FileInputStream(trustStorePath)) {
            KeyStore awsKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            awsKeyStore.load(myKeys, trustStorePassword.toCharArray());
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(awsKeyStore);
        }

        for (TrustManager tm : trustManagerFactory.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                return (X509TrustManager) tm;
            }
        }
        throw new IllegalStateException("Cannot find trust manager");
    }

    @RequiredArgsConstructor
    private static class CombinedTrustManager implements X509TrustManager {
        private final X509TrustManager primaryTrustManager;
        private final X509TrustManager secondaryTrustManager;

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return secondaryTrustManager.getAcceptedIssuers();
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                primaryTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException e) {
                // This will throw another CertificateException if this fails too.
                secondaryTrustManager.checkServerTrusted(chain, authType);
            }
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            secondaryTrustManager.checkClientTrusted(chain, authType);
        }
    }
}
