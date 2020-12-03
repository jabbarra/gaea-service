@file:JvmName("RestConfigurationKt")

package com.obarra.gaea_service.server

import com.obarra.gaea_service.handler.TraceHandler
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.validation.ValidationException
import io.vertx.kotlin.core.http.endAwait

private val logger = LoggerFactory.getLogger(RoutingContext::class.java)

suspend fun RoutingContext.success(data: Any) {
  respond(this, HttpCode.OK, data)
}

suspend fun RoutingContext.created(data: Any) {
  respond(this, HttpCode.CREATED, data)
}

suspend fun RoutingContext.badRequest(
  data: Any = "Bad request",
  customError: Boolean = false,
  errorCode: String? = null
) {
  error(this, HttpCode.BAD_REQUEST, data, customError, errorCode)
}

suspend fun RoutingContext.forbidden(data: Any = "Invalid access token") {
  error(this, HttpCode.FORBIDDEN, data)
}

suspend fun RoutingContext.notFound(data: Any = "Not found") {
  error(this, HttpCode.NOT_FOUND, data)
}

suspend fun RoutingContext.conflict(data: Any = "Conflict", customError: Boolean = false) {
  error(this, HttpCode.CONFLICT, data, customError)
}

suspend fun RoutingContext.internalServerError(data: Any = "Internal server error") {
  error(this, HttpCode.INTERNAL_SERVER_ERROR, data)
}

private suspend fun error(
  context: RoutingContext,
  statusCode: Int,
  data: Any,
  customError: Boolean = false,
  errorCode: String? = null
) {
  val message = if (!customError) {
    context.failure()?.message ?: data.toString()
  } else {
    data.toString()
  }
  val response = JsonObject()
    .put("status", statusCode)
    .put("message", message)

  if (errorCode != null) {
    response.put("code", errorCode)
  } else {
    val failure = context.failure()
    if (failure is ValidationException && failure.value() != null) {
      response.put("code", failure.value())
    }
  }

  val traceId = context.request().getHeader(TraceHandler.TRACE_ID)
  val parsedResponse = response.toString()
  logger.error("TraceId=$traceId Error: $parsedResponse", context.failure())

  respond(context, statusCode, response)
}

private suspend fun respond(context: RoutingContext, statusCode: Int, data: Any) {
  val buffer = Json.encodeToBuffer(data)
  context.response()
    .putHeader("Content-Type", "application-json")
    .setStatusCode(statusCode)
    .endAwait(buffer)
}


