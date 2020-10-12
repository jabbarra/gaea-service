package com.obarra.gaea_service

import io.vertx.core.Vertx

fun main() {
  val vertx = Vertx.vertx()
  try {
    vertx.deployVerticle("com.obarra.gaea_service.verticle.HTTPServerCoroutineVerticle")
  } catch (e: Throwable) {
    e.printStackTrace()
  }
}
