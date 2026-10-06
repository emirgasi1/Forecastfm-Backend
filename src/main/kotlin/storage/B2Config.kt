package storage

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

/**
 * Cloud-agnostic S3-compatible storage config, pointed at Backblaze B2.
 *
 * Reads six environment variables:
 *   B2_ENDPOINT                e.g. https://s3.us-east-005.backblazeb2.com
 *   B2_REGION                  e.g. us-east-005
 *   B2_ACCESS_KEY              Application Key ID
 *   B2_SECRET_KEY              Application Key
 *   B2_BUCKET                  e.g. forecastfm-uploads
 *   B2_PRESIGN_EXPIRY_SECONDS  e.g. 3600
 *
 * The S3Client and S3Presigner are lazy singletons. First call constructs
 * them; subsequent calls reuse. Safe to warm up on startup from a
 * background thread if desired.
 */
object B2Config {

    private val endpoint: String = requireEnv("B2_ENDPOINT")
    private val region: String = requireEnv("B2_REGION")
    private val accessKey: String = requireEnv("B2_ACCESS_KEY")
    private val secretKey: String = requireEnv("B2_SECRET_KEY")

    val bucket: String = requireEnv("B2_BUCKET")

    val presignExpirySeconds: Long =
        System.getenv("B2_PRESIGN_EXPIRY_SECONDS")?.toLongOrNull() ?: 3600L

    private val credentials = StaticCredentialsProvider.create(
        AwsBasicCredentials.create(accessKey, secretKey)
    )

    /**
     * R2/B2/GCS all require path-style addressing (bucket in the URL path,
     * not as a subdomain). Without this, the SDK will try
     * forecastfm-uploads.s3.us-east-005.backblazeb2.com and B2 will 404.
     */
    private val s3Config = S3Configuration.builder()
        .pathStyleAccessEnabled(true)
        .chunkedEncodingEnabled(false)   // see note below
        .build()

    val client: S3Client by lazy {
        S3Client.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .credentialsProvider(credentials)
            .serviceConfiguration(s3Config)
            .build()
    }

    val presigner: S3Presigner by lazy {
        S3Presigner.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .credentialsProvider(credentials)
            .build()
    }

    private fun requireEnv(key: String): String =
        System.getenv(key) ?: error("Missing required env var: $key")
}