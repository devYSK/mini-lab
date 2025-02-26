package com.yscorp.qrexample

import com.google.zxing.*
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

@Service
class QrGenerateService {

    suspend fun generateQRCode(content: String, width: Int, height: Int): BufferedImage {
        val hints = Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
        val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
        return MatrixToImageWriter.toBufferedImage(bitMatrix)
    }

    suspend fun generateQRCodeAsByteArray(content: String, width: Int, height: Int): ByteArray {
        // QR 코드 힌트 설정
        val hints = Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.MARGIN] = 1

        // QR 코드 생성
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
        val bufferedImage: BufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)

        // BufferedImage를 ByteArrayOutputStream으로 변환
        val baos = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", baos)

        // ByteArrayOutputStream을 바이트 배열로 변환
        return baos.toByteArray()
    }


    fun generateQRCodeAsRawByteArray(content: String, width: Int, height: Int): ByteArray {
        // QR 코드 힌트 설정
        val hints = Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.MARGIN] = 1

        val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)

        return bitMatrixToByteArray(bitMatrix)
    }

    fun buildTelQr(phonNumber: String) {
        buildQR("tel:+${phonNumber}")
    }

    fun buildQR(
        url: String,
        pathString: String = "/Users/ysk/study/temp",
        fileName: String = UUID.randomUUID().toString()
    ) {
        val hints = Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H // 높은 오류 수정 수준
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8" // 문자셋 설정
        hints[EncodeHintType.MARGIN] = 2 // 좁은 여백 설정
        hints[EncodeHintType.QR_VERSION] = 5 // QR 코드 버전 5
        hints[EncodeHintType.QR_MASK_PATTERN] = 2 // 마스크 패턴 2 사용
        hints[EncodeHintType.QR_COMPACT] = true // QR 코드 컴팩트 모드 사용

        val writer = MultiFormatWriter()

        val width = 400
        val height = 400
        // 200, 200은 가로, 세로 크기
        val matrix = writer.encode(url, BarcodeFormat.QR_CODE, width, height, hints)

        val path = Paths.get("$pathString/$fileName.jpeg")

        MatrixToImageWriter.writeToPath(matrix, "jpeg", path)
    }

    fun decodeBarcode(filePath: String): Result {
        // 이미지 파일 로드
        val file = File(filePath)
        val bufferedImage = ImageIO.read(file)

        // 이미지에서 바코드 데이터를 디코딩하기 위한 BinaryBitmap 생성
        val luminanceSource = BufferedImageLuminanceSource(bufferedImage)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(luminanceSource))

        //바코드/QR 코드를 디코딩
        val reader = MultiFormatReader()

        return reader.decode(binaryBitmap)
    }

    private fun bitMatrixToByteArray(bitMatrix: BitMatrix): ByteArray {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val byteArray = ByteArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                byteArray[index] = if (bitMatrix.get(x, y)) 1 else 0
            }
        }

        return byteArray
    }

}