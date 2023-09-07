package llesha.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.staticFiles
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.appendIfNameAbsent
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import llesha.*
import java.io.File
import java.time.LocalDateTime
import kotlin.random.Random

val monitoring = mutableMapOf<Route, Int>()

fun Application.configureRouting() {
    val routing = routing {
        openAPI(path = "api", swaggerFile = "openapi/documentation.yaml") {
            codegen = StaticHtmlCodegen()
        }

        intercept(ApplicationCallPipeline.Monitoring) {
            monitoring.merge((call as RoutingApplicationCall).route.parent!!, 1) { a, b -> a + b }
        }

        get("/") {
            call.respondText("Hello World!")
        }
        get("/echo/{text}") {
            call.respondText(call.parameters["text"]!!)
        }

        route("/time") {
            get {
                val t = LocalDateTime.now()
                val mnemonics = "ymdhms"
                val current = "${t.year}:${t.month.value}:${t.dayOfMonth}:${t.hour}:${t.minute}:${t.second}"
                val sizes = current.split(":").map { it.length }
                call.respond(current + "\n" + mnemonics
                    .toList()
                    .withIndex()
                    .joinToString(separator = ":") { it.value.toString().repeat(sizes[it.index]) })
            }

            staticFiles("/stopwatch/default", File("pages")) {
                default("time/stopwatch.html")
            }

            get("stopwatch") {
                accessParameters(call)
                call.redirectInternally("/time/stopwatch/default", makeTimeParams(false, call.parameters))
            }

            get("timer") {
                accessParameters(call)
                call.redirectInternally("/time/stopwatch/default", makeTimeParams(true, call.parameters))
            }
        }

        route("/rnd") {
            get("/str") {
                val symbols = call.parameters["symbols"]?.toList()
                    ?: "a-zA-Z0-9!@#$%^&*\"':;.,()<>_+=-*".toList()
                val length = call.parameters["length"]?.let { getRange(it).random() }
                    ?: Random.nextInt(2, 11)

                val res = StringBuilder()
                for (i in 0 until length) {
                    res.append(symbols.random())
                }
                call.respondText(res.toString())
            }
            get("/{int?}") {
                val range = call.parameters["int"]?.let { getRange(it) } ?: Int.MIN_VALUE..Int.MAX_VALUE
                call.respondText(range.random().toString())
            }
        }

        get("/monitoring") {
            call.respond(monitoring)
        }

        get("/html-template") {
            call.respond(
                """
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Title</title>
    <link rel="stylesheet" href="style.css">
  </head>
  <body>
    <div id="main"></div>
    <script src="index.js"></script>
  </body>
</html>
            """.trimIndent()
            )
        }
    }
}

/**
 * Makes range from string that looks like A..B
 * If only one number N is specified, get range of type N..N
 */
private fun getRange(range: String): IntRange {
    if (!range.contains("..")) {
        return range.toInt()..range.toInt()
    }
    val elements = range.split("..")
    if (elements.size != 2)
        throw IllegalArgumentException("range should be split by `..`")
    val numbers = elements.map { it.toInt() }
    return numbers.first()..numbers.last()
}

private fun makeTimeParams(isTimer: Boolean, parameters: Parameters): Parameters {
    val builder = ParametersBuilder()
    builder.appendAll(
        parameters.toStringValuesBuilderImpl()
            .appendIfNameAbsent("type", if (isTimer) "timer" else "stopwatch")
            .appendIfNameAbsent("d", "0")
            .appendIfNameAbsent("h", "0")
            .appendIfNameAbsent("s", if (isTimer) "1" else "0")
            .appendIfNameAbsent("m", "0").build()
    )
    return builder.build()
}

/**
 * For OpenApi documentation
 */
private fun accessParameters(call: ApplicationCall) {
    call.parameters["d"]?.toInt()
    call.parameters["h"]?.toInt()
    call.parameters["m"]?.toInt()
    call.parameters["s"]?.toInt()
}