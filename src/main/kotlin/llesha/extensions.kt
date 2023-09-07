package llesha

import io.ktor.http.Parameters
import io.ktor.http.RequestConnectionPoint
import io.ktor.http.formUrlEncode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.response.respondRedirect
import io.ktor.util.StringValuesBuilderImpl
import io.ktor.util.flattenEntries

fun Parameters.toStringValuesBuilderImpl(): StringValuesBuilderImpl {
    val res = StringValuesBuilderImpl()
    flattenEntries().forEach { res.append(it.first, it.second) }
    return res
}

suspend fun ApplicationCall.redirectInternally(path: String, parameters: Parameters) {
    val cp = object : RequestConnectionPoint by this.request.local {
        override val uri: String = path
    }
    val req = object : ApplicationRequest by this.request {
        override val local: RequestConnectionPoint = cp
        override val queryParameters: Parameters = parameters
        override val rawQueryParameters: Parameters = parameters
    }
    val call = object : ApplicationCall by this {
        override val request: ApplicationRequest = req
        override val parameters: Parameters = parameters
    }

    val query = call.parameters.formUrlEncode()
    call.respondRedirect("$path?$query")
}