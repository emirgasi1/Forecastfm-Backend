package com.example.routes

import com.example.comment.CommentRepository
import com.example.comment.CreateCommentRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlin.uuid.Uuid

fun Route.commentRoutes() {
    val commentRepository = CommentRepository()

    post("/api/comments") {
        val request = call.receive<CreateCommentRequest>()

        val comment = commentRepository.createComment(
            userId = request.userId,
            postId = request.postId,
            text = request.text
        )

        call.respond(HttpStatusCode.Created, comment)
    }

    get("/api/comments/{id}") {
        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing comment ID")

        val comment = commentRepository.getCommentById(id)

        if (comment == null) {
            call.respond(HttpStatusCode.NotFound, "Comment not found")
        } else {
            call.respond(comment)
        }
    }
}