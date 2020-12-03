package com.obarra.gaea_service.handler

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import java.util.*

object TraceHandler : Handler<RoutingContext> {

  const val TRACE_ID = "trace-id"

  override fun handle(context: RoutingContext) {
    val headers = context.request().headers()
    if (headers.get(TRACE_ID).isNullOrEmpty()) {
      UUID.randomUUID().toString().also { context.request().headers().add(TRACE_ID, it) }
    }

    context.next()
  }
}
