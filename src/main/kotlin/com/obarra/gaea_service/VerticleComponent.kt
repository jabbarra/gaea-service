package com.obarra.gaea_service

import io.vertx.core.Verticle
import io.vertx.core.spi.VerticleFactory

object VerticleComponent : VerticleFactory {

  override fun prefix(): String = "componentXXX"

  override fun createVerticle(verticleName: String, classLoader: ClassLoader): Verticle {
    println("xxxxxxxxxxxxxxxxxxxxxx")
    val clazz = Class.forName(verticleName.substringAfter("${prefix()}:")).kotlin
    return MainVerticle()
  }

}
