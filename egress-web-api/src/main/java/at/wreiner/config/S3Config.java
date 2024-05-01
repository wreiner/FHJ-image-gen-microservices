package at.wreiner.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
public class S3Config {

    @Value("${s3.aws_access_key}")
    private String accessKey;

    @Value("${s3.aws_secret_key}")
    private String secretKey;

    @Value("${s3.aws_region}")
    private String region;

    @Value("${s3.s3_endpoint}")
    private String s3Endpoint;

    @Value("${s3.use_https}")
    private boolean useHttps;

    @Value("${s3.verify_ssl}")
    private boolean verifySsl;

    @Bean
    public AmazonS3 s3client() throws Exception {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(useHttps ? Protocol.HTTPS : Protocol.HTTP);

        if (!verifySsl) {
            // Add custom SSL context that does not validate certificates
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { new NoopX509TrustManager() }, new SecureRandom());
            SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            clientConfig.getApacheHttpClientConfig().setSslSocketFactory(sslSocketFactory);
        }

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withClientConfiguration(clientConfig)
                .withEndpointConfiguration(new AmazonS3ClientBuilder.EndpointConfiguration(s3Endpoint, region))
                .withPathStyleAccessEnabled(true);  // Necessary for some S3-compatible services

        return builder.build();
    }

    private static class NoopX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] xcs, String string) {}
        public void checkServerTrusted(X509Certificate[] xcs, String string) {}
        public X509Certificate[] getAcceptedIssuers() { return null; }
    }
}
