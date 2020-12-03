package com.obarra.gaea_service.controller


import com.obarra.gaea_service.server.internalServerError
import com.obarra.gaea_service.server.success
import io.vertx.ext.web.RoutingContext

class LocationController() {
  suspend fun getLocations(context: RoutingContext) {
    val response = Location(2.3, 4.4)

    if (response == null) {
      context.internalServerError("Error ")
    } else {
      context.success(response)
    }
  }
}

data class Location(
  val latitude: Double,
  val longitude: Double,
)
