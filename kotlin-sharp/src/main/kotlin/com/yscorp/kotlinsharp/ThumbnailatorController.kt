package com.yscorp.kotlinsharp

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/4")
class ThumbnailatorController(
    private val thumbnailatorService: ThumbnailatorService,
) {

    //    @PostMapping("/url")
//    suspend fun processImageFromUrl(url: String) {
//        val processImagesFromUrls = TimeLogging.runWithTimingLogging {
//            thumbnailatorService.processImagesFromUrls(url)
//        }
//        println("original : ${processImagesFromUrls.originalWidth}")
//        println("original : ${processImagesFromUrls.originalHeight}")
//        println("original sizde : ${ByteUtils.bytesToKilobytes(processImagesFromUrls.originalSize)}")
//        println(processImagesFromUrls.s3FileName)
//        println(processImagesFromUrls.width)
//        println(processImagesFromUrls.height)
//        println(ByteUtils.getFileSizeInKB(processImagesFromUrls.buffer))
//    }


    @PostMapping("/file")
    suspend fun processImageFromFile(file: MultipartFile) {
        val processImagesFromMultipartFiles = TimeLogging.runWithTimingLogging {
            thumbnailatorService.resizeImage(file, 200)
        }

        println("original : ${processImagesFromMultipartFiles.originalWidth}")
        println("original : ${processImagesFromMultipartFiles.originalHeight}")
        println("original sizde : ${ByteUtils.bytesToKilobytes(processImagesFromMultipartFiles.originalSize)}")
        println(processImagesFromMultipartFiles.s3FileName)
        println(processImagesFromMultipartFiles.width)
        println(processImagesFromMultipartFiles.height)
        println(ByteUtils.getFileSizeInKB(processImagesFromMultipartFiles.buffer))
    }

}
