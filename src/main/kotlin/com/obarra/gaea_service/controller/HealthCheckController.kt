package com.obarra.gaea_service.controller

import com.obarra.gaea_service.server.success
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

class HealthCheckController {

  suspend fun health(context: RoutingContext) {
    context.success(response)
  }

  companion object {
    private val response = JsonObject().put("status", "ok")
  }
}
