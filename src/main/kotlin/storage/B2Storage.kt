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

        B2Config.presigner.presignGetObject(presignRequest).url().toString()
    }
}