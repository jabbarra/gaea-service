package com.obarra.gaea_service

import io.vertx.core.Vertx

fun main() {
  // val vertx = MainVerticle().start()
  val vertx = Vertx.vertx()
  vertx.deployVerticle("com.obarra.gaea_service.MainVerticle")
}
