package com.obarra.gaea_service

import com.obarra.gaea_service.server.Server
import io.vertx.core.DeploymentOptions
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle

class AppVerticle : CoroutineVerticle() {
  private val logger: Logger = LoggerFactory.getLogger(AppVerticle::class.java)

  override suspend fun start() {
    try {
      deployVerticles()
      logger.info("STARTED. ")
    } catch (e: Exception) {
      logger.error("An error occurred trying to start application", e)
    }
  }

  private suspend fun deployVerticles() {

    val verticles = mapOf(
      "orderEvents" to Server::class.java
    )
    verticles.forEach { (key, value) ->
      vertx.deployVerticleAwait(
        value.name,
        DeploymentOptions()
      )
    }

  }
}
