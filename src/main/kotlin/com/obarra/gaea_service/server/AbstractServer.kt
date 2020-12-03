package com.obarra.gaea_service.server

import com.obarra.gaea_service.handler.TraceHandler
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.ResponseContentTypeHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch

abstract class AbstractServer : CoroutineVerticle() {

  private lateinit var httpServer: HttpServer
  private lateinit var router: Router

  override fun init(vertx: Vertx, context: Context) {
    super.init(vertx, context)
    this.httpServer = vertx.createHttpServer(getHttpServerOptions())
    this.router = Router.router(this.vertx)
  }

  override suspend fun start() {
    addRoutes()
    listen()
  }

  private fun getHttpServerOptions(): HttpServerOptions {
    val options = HttpServerOptions()
    if (vertx.isNativeTransportEnabled) {
      options.setTcpFastOpen(true)
        .setTcpNoDelay(true)
        .setTcpQuickAck(true)
    }
    return options
  }

  private suspend fun listen() {
    val port =  8080 //this.config.getJsonObject("server").getInteger("port")
    this.httpServer.requestHandler(this.router)
    this.httpServer.listenAwait(port)
  }

  private fun addRoutes() {
    handleGlobalDefaults()
    handleErrors()
    routes(this.router)
  }

  private fun handleGlobalDefaults() {
    val catchAllRoute = "/*"
    val jsonMimeType = "application/json"
    val bodyHandler = BodyHandler.create()
    this.router.post(catchAllRoute).handler(bodyHandler).handler(TraceHandler)
    this.router.patch(catchAllRoute).handler(bodyHandler).handler(TraceHandler)
    this.router.put(catchAllRoute).handler(bodyHandler).handler(TraceHandler)
    this.router.get(catchAllRoute).handler(TraceHandler)
    this.router.delete(catchAllRoute).handler(TraceHandler)
    this.router.route(catchAllRoute)
      .consumes(jsonMimeType)
      .produces(jsonMimeType)
      .handler(ResponseContentTypeHandler.create())
      .handler(TraceHandler)
  }

  private fun handleErrors() {
    this.router.errorHandler(HttpCode.BAD_REQUEST) { ctx ->
      launch(ctx.vertx().dispatcher()) {
        ctx.badRequest()
      }
    }
    this.router.errorHandler(HttpCode.NOT_FOUND) { ctx ->
      launch(ctx.vertx().dispatcher()) {
        ctx.notFound()
      }
    }
    this.router.errorHandler(HttpCode.INTERNAL_SERVER_ERROR) { ctx ->
      launch(ctx.vertx().dispatcher()) {
        ctx.internalServerError()
      }
    }
  }

  fun Route.handlerAwait(fn: suspend (RoutingContext) -> Any): Route {
    return handler { ctx ->
      launch(ctx.vertx().dispatcher()) {
        try {
          fn(ctx)
        } catch (e: Exception) {
          ctx.fail(e)
        }
      }
    }
  }

  abstract fun routes(router: Router)

  companion object {
    val logger: Logger = LoggerFactory.getLogger(AbstractServer::class.java)
  }
}

class HttpCode private constructor() {
  companion object {
    const val OK = 200
    const val CREATED = 201
    const val BAD_REQUEST = 400
    const val FORBIDDEN = 403
    const val NOT_FOUND = 404
    const val CONFLICT = 409
    const val INTERNAL_SERVER_ERROR = 500
  }
}
