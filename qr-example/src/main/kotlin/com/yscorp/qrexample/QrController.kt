package com.yscorp.qrexample

import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * /qr: 이 URL을 브라우저에서 열면 QR 코드가 브라우저 내에서 표시
 * /qr/download: 이 URL을 브라우저에서 열면 QR 코드 이미지가 자동으로 다운로드
 */
@RestController
class QrController(
    private val qrCodeService: QrGenerateService,
) {

    @GetMapping("/qr", produces = [MediaType.IMAGE_PNG_VALUE])
    suspend fun getQRCode(): ResponseEntity<ByteArray> {
        val byteArray =
            qrCodeService.generateQRCodeAsByteArray("https://www.naver.com", 300, 300)

        val dataBuffer = DefaultDataBufferFactory.sharedInstance.wrap(byteArray)
        DataBufferUtils.release(dataBuffer)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qrcode.png\"")
            .contentType(MediaType.IMAGE_PNG)
            .body(byteArray)
    }

    @GetMapping("/qr/download", produces = [MediaType.IMAGE_PNG_VALUE])
    suspend fun downloadQRCode(): ResponseEntity<ByteArray> {
        val byteArray = qrCodeService.generateQRCodeAsByteArray("https://www.example.com", 300, 300)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qrcode.png\"")
            .contentType(MediaType.IMAGE_PNG)
            .body(byteArray)
    }
}