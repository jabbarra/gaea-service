package com.obarra.gaea_service

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx

fun main() {
  try {
    val preVertx = Vertx.vertx()

    preVertx.close {
      val vertx = Vertx.vertx()
      vertx.deployVerticle(AppVerticle::class.java.name, DeploymentOptions())
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }
}
