package com.example.routes

import com.example.outfit.CreateOutfitRequest
import com.example.outfit.OutfitRepository
import com.example.outfit.SavedOutfitRepository
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.coroutines.runBlocking
import storage.B2Storage
import java.util.UUID

fun Route.outfitRoutes() {
    val outfitRepository = OutfitRepository()
    val savedOutfitRepository = SavedOutfitRepository()

    post("/api/outfits") {
        val request = call.receive<CreateOutfitRequest>()
        val outfit = outfitRepository.createOutfit(
            userId = request.userId,
            imageUrl = request.imageUrl,
            title = request.title,
            weatherCondition = request.weatherCondition,
            season = request.season,
            storeName = request.storeName,
            storeAddress = request.storeAddress,
            price = request.price,
            storePhone = request.storePhone,
            productUrl = request.productUrl
        )
        call.respond(HttpStatusCode.Created, outfit)
    }

    get("/api/outfits/trending") {
        val outfits = outfitRepository.getTrendingOutfits(10)
        val signed = outfits.map { outfit ->
            val presigned = outfit.imageUrl.let { key ->
                runCatching { B2Storage.presign(key) }.getOrNull()
            }
            outfit.copy(imageUrl = presigned ?: outfit.imageUrl)
        }
        call.respond(signed)
    }

    get("/api/outfits/weather/{weather}") {
        val weather = call.parameters["weather"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing weather")
        val outfits = outfitRepository.getOutfitsByWeather(weather)
        val signed = outfits.map { outfit ->
            val presigned = outfit.imageUrl.let { key ->
                runCatching { B2Storage.presign(key) }.getOrNull()
            }
            outfit.copy(imageUrl = presigned ?: outfit.imageUrl)
        }
        call.respond(signed)
    }

    post("/api/outfits/{id}/like") {
        val id = call.parameters["id"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing outfit ID")
        outfitRepository.likeOutfit(id)
        call.respond(HttpStatusCode.OK)
    }

    post("/api/outfits/{outfitId}/save") {
        val outfitId = call.parameters["outfitId"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing outfit ID")

        val userId = call.request.headers["User-Id"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        savedOutfitRepository.saveOutfit(
            userId = userId,
            outfitId = outfitId
        )
        call.respond(HttpStatusCode.Created)
    }

    delete("/api/outfits/{outfitId}/save") {
        val outfitId = call.parameters["outfitId"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing outfit ID")

        val userId = call.request.headers["User-Id"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        savedOutfitRepository.unsaveOutfit(
            userId = userId,
            outfitId = outfitId
        )
        call.respond(HttpStatusCode.OK)
    }

    get("/api/outfits/{outfitId}/save") {
        val outfitId = call.parameters["outfitId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing outfit ID")

        val userId = call.request.headers["User-Id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        val isSaved = savedOutfitRepository.isOutfitSaved(
            userId = userId,
            outfitId = outfitId
        )
        call.respond(mapOf("saved" to isSaved))
    }

    get("/api/outfits/saved") {
        val userId = call.request.headers["User-Id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        val savedOutfits = savedOutfitRepository.getSavedOutfits(userId)
        val signed = savedOutfits.map { outfit ->
            val presigned = outfit.imageUrl.let { key ->
                runCatching { B2Storage.presign(key) }.getOrNull()
            }
            outfit.copy(imageUrl = presigned ?: outfit.imageUrl)
        }
        call.respond(signed)
    }

    get("/api/outfits/{id}") {
        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing outfit ID")

        val outfit = outfitRepository.getOutfitById(id)
        if (outfit == null) {
            call.respond(HttpStatusCode.NotFound, "Outfit not found")
        } else {
            val presigned = runCatching { B2Storage.presign(outfit.imageUrl) }.getOrNull()
            call.respond(outfit.copy(imageUrl = presigned ?: outfit.imageUrl))
        }
    }

    post("/api/uploads/image") {
        val multipart = call.receiveMultipart()
        var uploadedKey: String? = null

        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                val originalName = part.originalFileName ?: "image.jpg"
                val ext = originalName.substringAfterLast('.', "jpg")
                val key = "outfits/${UUID.randomUUID()}.$ext"

                val bytes = part.streamProvider().use { it.readBytes() }

                runBlocking {
                    B2Storage.upload(
                        key = key,
                        bytes = bytes,
                        contentType = "image/$ext"
                    )
                }

                uploadedKey = key
            }
            part.dispose()
        }

        val uploaded = uploadedKey
            ?: return@post call.respond(HttpStatusCode.BadRequest, "No file uploaded")

        // Sign the key so the client gets back a usable URL immediately.
        val presigned = runCatching { B2Storage.presign(uploaded) }.getOrNull()
            ?: return@post call.respond(
                HttpStatusCode.InternalServerError,
                "Failed to sign uploaded image"
            )

        call.respond(HttpStatusCode.OK, mapOf("url" to presigned))
    }
}