package com.yscorp.slackfile

import okhttp3.MultipartBody
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.nio.charset.Charset

@SpringBootTest
class SlackFileApplicationTests {

    @Autowired
    lateinit var slackService: SlackService

    @Test
    fun contextLoads() {
        slackService.sendFile("test 텍스텍스트")
    }

    @Test
    fun test() {
//        // 파일 경로로 업로드
//        slackService.sendFile(filePath = "/Users/ysk/study/temp/abb33c1e-6e4a-4975-ae43-82f041836dc8.jpeg", channel = "C07GN2ZG482")

        // URL로 업로드
        slackService.sendFile(fileUrl = "https://dimg.donga.com/wps/NEWS/IMAGE/2022/01/28/111500268.2.jpg", channel = "C07GN2ZG482")

    // 멀티파트 파일로 업로드
//        val multipartFile = MultipartBody.Part.createFormData("file", "filename.jpg", RequestBody.create(null, File("/path/to/file.jpg")))
//        slackService.sendFile(multipartFile = multipartFile, channel = "C07GN2ZG482")

        // 바이트 배열로 업로드
//        val byteArray = "abc!!11".toByteArray(Charset.forName("UTF-8")) // 예시 바이트 배열
//        slackService.sendFile(byteArray = byteArray, filename = "myfile.txt", channel = "C07GN2ZG482")
    }

}
