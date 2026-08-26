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

    val commentRepository= CommentRepository()

    post("/api/comments") {

        val request = call.receive<CreateCommentRequest>()

        val comment = commentRepository.createComment(
            userId = Uuid.parse(request.userId),
            postId = Uuid.parse(request.postId),
            text = request.text
        )

        call.respond(
            HttpStatusCode.Created,
            comment
        )
    }

    get("/api/comments/{id}") {

        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        val commentId = try {
            Uuid.parse(id)
        } catch (e: IllegalArgumentException) {
            return@get call.respond(HttpStatusCode.BadRequest)
        }

        val comment = commentRepository.getCommentById(commentId)

        if (comment == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(comment)
        }
    }


}