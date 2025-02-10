package com.yscorp.slackfile

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.slack.api.Slack
import com.slack.api.methods.request.files.FilesUploadV2Request
import okhttp3.MultipartBody
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLConnection
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

@Service
class SlackService(
    private val environment: Environment,
) {
    val token: String = environment["SLACK_TOKEN"] ?: throw IllegalArgumentException("슬랙 토큰 환경변수가 존재하지 않습니다 ")

    val slack = Slack.getInstance()

    init {
        System.getenv().forEach { (t, u) ->
            println("t $t , $u")
        }
    }

    val slackChannel = "C07GN2ZG482"

    fun sendFile(text: String) {
        val byteArray = text.toByteArray()

        // 파일 업로드 요청 생성
        val request = FilesUploadV2Request.builder()
            .token(token)
            .fileData(byteArray)  // 파일 데이터를 바이트 배열로 전달
            .filename("textfile.txt")
            .initialComment(
                """
                Here is the text file **안녕하세요**
                - 반
                - 갑
                - 습
                """.trimIndent()
            )
            .channel("C07GN2ZG482") // ㅇㅅ쪽 테스트 채널 아이디
            .build()

        val response = slack.methods().filesUploadV2(request)

        // 결과 출력
        if (response.isOk) {
            println("File uploaded successfully: ${jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response)}")
        } else {
            println("Error uploading file: ${response.error}")
        }
    }


    fun sendFile(
        filePath: String? = null,
        fileUrl: String? = null,
        multipartFile: MultipartBody.Part? = null,
        byteArray: ByteArray? = null,
        filename: String? = null,
        channel: String,
        comment: String = ""
    ) {
        val fileData: ByteArray
        val finalFilename: String
        val fileType: String?

        when {
            filePath != null -> {
                // 파일 경로로부터 파일 읽기
                val path = Paths.get(filePath)
                fileData = Files.readAllBytes(path)
                finalFilename = filename ?: path.fileName.toString()
                fileType = Files.probeContentType(path)
            }
            fileUrl != null -> {
//                // URL로부터 파일 다운로드
//                val url = URI.create(fileUrl).toURL()
//                fileData = url.readBytes()
//
//                // URL 경로에서 파일명 추출
//                val urlPath = url.path
//                finalFilename = filename ?: urlPath.substringAfterLast('/', "downloaded_file")
//
//                // 파일 타입 추출
//                val connection = url.openConnection()
//                fileType = URLConnection.guessContentTypeFromStream(connection.getInputStream())
//                connection.headerFields.forEach { t, u ->
//                    println("$t - $u")
//                }
                val (data, name, type) = downloadAndExtractFileInfo(fileUrl)
                fileData = data
                finalFilename = name
                fileType = type
            }
            multipartFile != null -> {
                // 멀티파트 파일로부터 파일 데이터 추출
                val requestBody = multipartFile.body
                val buffer = okio.Buffer()
                requestBody.writeTo(buffer)
                fileData = buffer.readByteArray()
                finalFilename = filename ?: multipartFile.headers?.get("Content-Disposition")
                    ?.split("filename=")?.get(1)?.replace("\"", "") ?: "uploaded_file"
                fileType = URLConnection.guessContentTypeFromName(finalFilename)
            }
            byteArray != null -> {
                // 바이트 배열로부터 파일 처리
                fileData = byteArray
                finalFilename = filename ?: "uploaded_file"
                fileType = URLConnection.guessContentTypeFromName(finalFilename)
            }
            else -> {
                throw IllegalArgumentException("One of filePath, fileUrl, multipartFile, or byteArray must be provided.")
            }
        }

        // Slack 파일 업로드 요청 생성
        val request = FilesUploadV2Request.builder()
            .token(token)
            .fileData(fileData)
            .filename(finalFilename)
            .initialComment(comment.ifEmpty { "Here is the $fileType - $filename file" })
            .channel(channel)
            .build()

        val response = slack.methods().filesUploadV2(request)

        // 결과 출력
        if (response.isOk) {
            println("File uploaded successfully: ${jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response)}")
        } else {
            println("Error uploading file: ${response.error}")
        }
    }

    fun downloadAndExtractFileInfo(fileUrl: String): Triple<ByteArray, String, String> {
        val url = URI.create(fileUrl).toURL()
        var connection: HttpURLConnection? = null
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("HTTP error code: ${connection.responseCode}")
            }

            // 파일 데이터 다운로드
            val fileData = connection.inputStream.use { it.readBytes() }

            // 파일명 추출
            var filename = ""
            val contentDisposition = connection.getHeaderField("Content-Disposition")
            if (contentDisposition != null && contentDisposition.contains("filename=")) {
                filename = contentDisposition.split("filename=")[1].trim('"')
                filename = URLDecoder.decode(filename, StandardCharsets.UTF_8.toString())
            }
            if (filename.isBlank()) {
                filename = url.path.substringAfterLast('/')
            }
            if (filename.isBlank()) {
                filename = "downloaded_file"
            }

            // 파일 타입 추출
            var fileType = connection.contentType ?: ""
            if (fileType.isBlank()) {
                fileType = when {
                    filename.contains('.') -> filename.substringAfterLast('.').toLowerCase()
                    else -> "application/octet-stream"
                }
            }

            return Triple(fileData, filename, fileType)
        } finally {
            connection?.disconnect()
        }
    }
}