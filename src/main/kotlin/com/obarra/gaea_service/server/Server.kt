package com.obarra.gaea_service.server

import com.obarra.gaea_service.controller.HealthCheckController
import com.obarra.gaea_service.controller.LocationController
import io.vertx.ext.web.Router

class Server(
) : AbstractServer() {

  private val version = "/v1"
  override fun routes(router: Router) {
    val healthCheckController = HealthCheckController()
    val locationController = LocationController()

    router.get("/").handlerAwait(healthCheckController::health)

    router.get("$version/locations")
      .handlerAwait(locationController::getLocations)
  }
}
