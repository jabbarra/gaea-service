package com.obarra.gaea_service.verticle

import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class HTTPServerAbstractVerticle : AbstractVerticle() {
  private val logger = LoggerFactory.getLogger(HTTPServerAbstractVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    logSplash()

    vertx
      .createHttpServer()
      .requestHandler(createRouter())
      .listen(config().getInteger("http.port", 8888)) { resultHttp ->
        if (resultHttp.succeeded()) {
          logger.info("HTTPServerVerticle is UP")
          startPromise.complete()
        } else {
          logger.info("HTTPServerVerticle fail")
          startPromise.fail(resultHttp.cause());
        }
      }
  }

  private fun createRouter() = Router.router(vertx).apply {
    this.get("/").handler(handlerRoot)
    this.get("/policies").handler(handlerPolicies)
    this.get("/policies/:id").handler(handlerPolicyById)
  }

  private val handlerRoot = Handler<RoutingContext> { ctx ->
    ctx.response()
      .putHeader("content-type", "text/plain")
      .end("Hello from Vert.x!")
  }

  private val handlerPolicies = Handler<RoutingContext> { ctx ->
    ctx.response()
      .putHeader("Content-Type", "application/json; charset=utf-8")
      .end(Json.encodePrettily(policies))
  }

  private val handlerPolicyById = Handler<RoutingContext> { ctx ->
    val policyId = ctx.request().getParam("id").toLong()
    ctx.response()
      .endWithJson(policies.find { it.id == policyId }!!)
  }

  /**
   * Extension to the HTTP response to output JSON objects.
   */
  private fun HttpServerResponse.endWithJson(obj: Any) {
    this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
  }

  private val policies = listOf(Policy(1, "policy1"), Policy(2, "policy2"))

  data class Policy(val id: Long, val name: String)

  private fun logSplash() {
    logger.info(javaClass.getResource("/splash.txt").readText())
  }
}
