package com.obarra.gaea_service.server

import io.vertx.ext.web.Router

class RouterLocation () {
  fun createRouter(router: Router) {
    router.get("/policies/:id").handlerAwait
  }

}
