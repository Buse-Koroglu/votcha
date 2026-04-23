package com.example.votcha.votcha_search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    @Value("${spring.elasticsearch.uris}") private String uris;
    @Value("${spring.elasticsearch.username}") private String username;
    @Value("${spring.elasticsearch.password}") private String password;
    @Value("${spring.elasticsearch.crt.path}") private String crtPath;

    /**
     * Configures the connection settings for Elasticsearch.
     * We specify the host, port, security (SSL), and credentials here.
     */
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(uris) // The address where Elasticsearch is running (Docker mapping)
                .usingSsl(createSslContext())// Explicitly use our custom SSL context to trust the self-signed certificate
                .withBasicAuth(username, password)// username and password for internal authentication
                .build();
    }

    /**
     * Elasticsearch 9.x uses HTTPS by default and generates a self-signed CA certificate.
     * Java does not trust this certificate by default, which causes "PKIX path building failed" errors.
     * This method manually loads the 'ca.crt' and adds it to a temporary TrustStore so the app can communicate securely.
     */
//    private SSLContext createSslContext() {
//        try{
//            // Standard factory to handle X.509 type certificates
//            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            Certificate ca;
//
//            // Load the certificate file from the 'src/main/resources' folder
//            try(InputStream is = ElasticsearchConfig.class.getClassLoader().getResourceAsStream("ca.crt")){
//                if(is == null){
//                    throw new RuntimeException("ca.crt not found");
//                }
//                ca = cf.generateCertificate(is);
//            }
//
//            // Create an empty in-memory KeyStore (TrustStore)
//            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            trustStore.load(null, null);
//            // Add the Elasticsearch CA certificate to our custom TrustStore
//            trustStore.setCertificateEntry("ca", ca);
//
//            // Initialize a TrustManagerFactory with our custom TrustStore
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            tmf.init(trustStore);
//
//            // Initialize SSL context using the TLS protocol and our custom TrustManagers
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, tmf.getTrustManagers(), null);
//            return  sslContext;
//        } catch (CertificateException | IOException  | KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
//            throw new RuntimeException("Elasticsearch ssl context initialization failed", e);
//        }
//    }
    private SSLContext createSslContext() {
        try {
            File file = new File(crtPath);
            if(!file.exists()){
                throw new RuntimeException("Sertifika dosyası bulunamadı: " + file.getAbsolutePath());
            }
            try(InputStream is = new FileInputStream(file)){
                // Standard factory to handle X.509 type certificates
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate  ca = cf.generateCertificate(is);


                // Create an empty in-memory KeyStore (TrustStore)
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                // Add the Elasticsearch CA certificate to our custom TrustStore
                trustStore.setCertificateEntry("ca", ca);

                // Initialize a TrustManagerFactory with our custom TrustStore
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustStore);

                // Initialize SSL context using the TLS protocol and our custom TrustManagers
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);
                return  sslContext;
            }

        } catch (Exception e){
            throw new RuntimeException("Elasticsearch SSL context cannot be loaded: "+ crtPath,e);
        }
    }
}
