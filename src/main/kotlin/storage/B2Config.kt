package storage

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

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
            .serviceConfiguration(s3Config) // 👈 Reusing the same config here!
            .build()
    }

    private fun requireEnv(key: String): String =
        System.getenv(key) ?: error("Missing required env var: $key")
}