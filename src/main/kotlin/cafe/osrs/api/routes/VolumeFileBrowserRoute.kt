package cafe.osrs.api.routes

import cafe.osrs.api.APIConfig
import cafe.osrs.api.utils.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import java.io.File
import java.net.URI

fun Route.VolumeFileBrowserRoute() {
    val logger = KtorSimpleLogger("VFB")
    val password = String.random(length = 50)
    logger.info("VFB: $password")

    get("/vfb/{path...}") {
        if(!call.queryParameters.contains("p", password)) throw NotAuthorizedException()

        val path =  call.pathParameters.getAll("path")!!
        val file = File(APIConfig.volumePath, path.joinToString("/"))
        if(file.isDirectory) {
            val uri = URI(call.request.uri)
            val links = file.listFiles()?.sortedBy { !it.isDirectory }?.map {
                val basePath = uri.path.removeSuffix("/") + "/"
                val newUri = uri.withPath(basePath + it.name)
                val name = if(it.isDirectory) "${it.name}/" else it.name
                val size = if(it.isFile) " - ${it.length().formatFileSize()}" else ""
                "<p><a href='$newUri'>/$name$size</a></p>"
            }
            val parentPath = uri.path.split("/").dropLast(1).joinToString("/")
            val parentURI = uri.withPath(parentPath)

            val body = """
                <html>
                    <style>
                        body { background-color: #393939; color: lightgray; }
                        a { color: lightgray; }
                    </style>
                    <h2>${uri.path}</h2>
                    <p><a href='$parentURI'>/..</a></p>
                    ${links?.joinToString("")}
                </html>
            """.trimIndent()
            call.respondText(text = body, contentType = ContentType.Text.Html)
        } else if(file.exists()) {
            call.respondBytes(file.readBytes())
        } else {
            call.respondText(text = "File not found", status = HttpStatusCode.NotFound)
        }
    }
}