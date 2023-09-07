package llesha

import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.staticFiles
import io.ktor.server.netty.*
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import llesha.plugins.*
import java.io.File

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    handleExceptions()
}
