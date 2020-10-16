package uk.nhs.digital.nhsconnect.nhais.configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GetObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DocumentDBTrustStore {

    private final AmazonS3 s3Client;

    @SneakyThrows
    public void addToDefault(String trustStorePath, String trustStorePassword) {
        final X509TrustManager defaultTrustManager = getDefaultTrustManager();
        final X509TrustManager documentDbTrustManager = getDocumentDbTrustManager(new AmazonS3URI(trustStorePath), trustStorePassword);
        X509TrustManager combinedTrustManager = new CombinedTrustManager(documentDbTrustManager, defaultTrustManager);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]
            {
                combinedTrustManager
            }, null);
        SSLContext.setDefault(sslContext);
    }

    @SneakyThrows
    private X509TrustManager getDefaultTrustManager() {
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
    private X509TrustManager getDocumentDbTrustManager(AmazonS3URI s3URI, String trustStorePassword) {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null); // Using null here initialises the TMF with the default trust store.

        try (var s3Object = s3Client.getObject(new GetObjectRequest(s3URI.getBucket(), s3URI.getKey()));
             var content = s3Object.getObjectContent()) {
            KeyStore documentDbKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            documentDbKeyStore.load(content, trustStorePassword.toCharArray());
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(documentDbKeyStore);
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
