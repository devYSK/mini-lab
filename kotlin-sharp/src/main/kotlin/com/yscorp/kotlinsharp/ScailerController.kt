package com.yscorp.kotlinsharp

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import kotlin.random.Random

@RestController
@RequestMapping("/3")
class ScailerController(
    private val scailerImageProcessService: ScailerImageProcessService,
    private val imageProcessService: ImageProcessService,
) {

    @PostMapping("/url")
    suspend fun processImageFromUrl(url: String) {
        val processImagesFromUrls = TimeLogging.runWithTimingLogging {
            scailerImageProcessService.processImagesFromUrls(url)
        }

        println("original : ${processImagesFromUrls.originalWidth}")
        println("original : ${processImagesFromUrls.originalHeight}")
        println("original sizde : ${ByteUtils.bytesToKilobytes(processImagesFromUrls.originalSize)}")
        println(processImagesFromUrls.s3FileName)
        println(processImagesFromUrls.width)
        println(processImagesFromUrls.height)
        println(ByteUtils.getFileSizeInKB(processImagesFromUrls.buffer))
    }


    @PostMapping("/file")
    suspend fun processImageFromFile(@RequestParam("file") file: MultipartFile) {
        val processImagesFromMultipartFiles = TimeLogging.runWithTimingLogging {
            scailerImageProcessService.processImagesFromMultipartFiles(file)
        }

        println("original : ${processImagesFromMultipartFiles.originalWidth}")
        println("original : ${processImagesFromMultipartFiles.originalHeight}")
        println("original sizde : ${ByteUtils.bytesToKilobytes(processImagesFromMultipartFiles.originalSize)}")
        println(processImagesFromMultipartFiles.s3FileName)
        println(processImagesFromMultipartFiles.width)
        println(processImagesFromMultipartFiles.height)
        println(ByteUtils.getFileSizeInKB(processImagesFromMultipartFiles.buffer))
        val outputPath = "/Users/ysk/test/" + processImagesFromMultipartFiles.s3FileName
        println("outputPath: $outputPath")

        imageProcessService.saveProcessedFile(processImagesFromMultipartFiles.buffer, outputPath)
    }

    @PostMapping("/file2")
    suspend fun processImageFromFile2(@RequestParam("file") file: MultipartFile) {
        val processImagesFromMultipartFiles = TimeLogging.runWithTimingLogging {
            scailerImageProcessService.processImagesFromMultipartFile(file)
        }

        println("original : ${processImagesFromMultipartFiles.originalWidth}")
        println("original : ${processImagesFromMultipartFiles.originalHeight}")
        println("original sizde : ${ByteUtils.bytesToKilobytes(processImagesFromMultipartFiles.originalSize)}")
        println(processImagesFromMultipartFiles.s3FileName)
        println(processImagesFromMultipartFiles.width)
        println(processImagesFromMultipartFiles.height)
        println(ByteUtils.getFileSizeInKB(processImagesFromMultipartFiles.file.bytes))
        val outputPath = "/Users/ysk/test/${Random.nextInt()}" + processImagesFromMultipartFiles.s3FileName
        println("outputPath: $outputPath")

        imageProcessService.saveProcessedFile(processImagesFromMultipartFiles.file.bytes, outputPath)
    }


}

//fun generateRandomEmoji(): String {
//    val emojiRanges = listOf(
//        0x1F600..0x1F64F, // Emoticons
//        0x1F300..0x1F5FF, // Miscellaneous Symbols and Pictographs
//        0x1F680..0x1F6FF, // Transport and Map Symbols
//        0x1F700..0x1F77F, // Alchemical Symbols
//        0x1F780..0x1F7FF, // Geometric Shapes Extended
//        0x1F800..0x1F8FF, // Supplemental Arrows-C
//        0x1F900..0x1F9FF, // Supplemental Symbols and Pictographs
//        0x1FA00..0x1FA6F, // Chess Symbols
//        0x1FA70..0x1FAFF, // Symbols and Pictographs Extended-A
//        0x2600..0x26FF,   // Miscellaneous Symbols
//        0x2700..0x27BF    // Dingbats
//    ).flatten()
//
//    val randomEmojiCodePoint = emojiRanges.random(Random(System.currentTimeMillis() + Random.nextLong() ))
//    return String(Character.toChars(randomEmojiCodePoint))
//}

fun generateRandomEmoji(): String {
    val emojiRanges = listOf(
        0x1F600..0x1F64F, // Emoticons
        0x1F300..0x1F5FF, // Miscellaneous Symbols and Pictographs
        0x1F680..0x1F6FF, // Transport and Map Symbols
        0x1F700..0x1F77F, // Alchemical Symbols
        0x1F900..0x1F9FF, // Supplemental Symbols and Pictographs
        0x1F1E6..0x1F1FF  // Flags
    ).flatten()

    val validEmoji = emojiRanges.filter { Character.isDefined(it) && Character.UnicodeBlock.of(it) != null }
    val randomEmojiCodePoint = validEmoji.random(Random)
    return String(Character.toChars(randomEmojiCodePoint))
}
fun main() {
    repeat(10000) {
        if (it % 100 == 0) println()
        val randomEmoji = generateRandomEmoji()
        print("$randomEmoji,  ")
    }
}