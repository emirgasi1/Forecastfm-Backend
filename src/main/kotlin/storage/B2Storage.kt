package storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration


object B2Storage {

    suspend fun upload(
        key: String,
        bytes: ByteArray,
        contentType: String
    ): Unit = withContext(Dispatchers.IO) {
        B2Config.client.putObject(
            PutObjectRequest.builder()
                .bucket(B2Config.bucket)
                .key(key)
                .contentType(contentType)
                .build(),
            RequestBody.fromBytes(bytes)
        )
        Unit
    }

    suspend fun presign(key: String): String = withContext(Dispatchers.IO) {
        val getRequest = GetObjectRequest.builder()
            .bucket(B2Config.bucket)
            .key(key)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofSeconds(B2Config.presignExpirySeconds))
            .getObjectRequest(getRequest)
            .build()

        val rawUrl = B2Config.presigner.presignGetObject(presignRequest).url().toString()

        rawUrl.replace("\\u0026", "&")
    }

    suspend fun getSignedImageUrlOrOriginal(rawUrl: String?): String? {
        if (rawUrl.isNullOrBlank()) return null

        if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
            return rawUrl
        }

        return runCatching { B2Storage.presign(rawUrl) }.getOrNull()
    }
}