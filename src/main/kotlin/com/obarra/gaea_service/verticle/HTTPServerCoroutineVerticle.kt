package com.obarra.gaea_service.verticle

import io.vertx.core.Handler
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch

class HTTPServerCoroutineVerticle : CoroutineVerticle() {

  override suspend fun start() {
    vertx.createHttpServer()
      .requestHandler(createRouter())
      .listenAwait(config.getInteger("http.port", 8080))
  }


  private fun createRouter() = Router.router(vertx).apply {
    // this.get("/").coroutineHandler{ ctx -> getPolicies(ctx)}
    this.get("/policies").coroutineHandler { ctx -> getPolicies(ctx) }
    this.get("/policies/:id").coroutineHandler(::getPolicyById)
  }

  suspend fun getPolicies(ctx: RoutingContext) {
    ctx.response().end(json {
      obj("id" to 12, "name" to "policy").encode()
    })
  }

  suspend fun getPolicyById(context: RoutingContext) {
    val policyId = context.request().getParam("id").toLong()
    context.response().end(json {
      obj("id" to policyId, "name" to "policy").encode()
    })
  }


  /**
   * An extension method for simplifying coroutines usage with Vert.x Web routers
   */
  private fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit) {
    handler { ctx ->
      launch(ctx.vertx().dispatcher()) {
        try {
          fn(ctx)
        } catch (e: Exception) {
          ctx.fail(e)
        }
      }
    }
  }

}
