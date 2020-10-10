package com.obarra.gaea_service

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.core.deployVerticleAwait

class MainVerticle : AbstractVerticle() {
  private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    logSplash()

    vertx
      .createHttpServer()
      .requestHandler { req ->
        req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x!")
      }
      .listen(8888) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          println("HTTP server started on port 8888")
        } else {
          println("dfsdfs")

          startPromise.fail(http.cause());
        }
      }
  }

  private fun logSplash() {
    logger.info(javaClass.getResource("/splash.txt").readText())
  }
}
