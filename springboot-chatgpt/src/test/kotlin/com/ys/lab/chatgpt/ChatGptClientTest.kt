package com.ys.lab.chatgpt

import com.ys.lab.chatgpt.config.ChatGptProperties
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ChatGptClientTest(
) {

    @Autowired
    private lateinit var chatGptClient: ChatGptClient

    @Autowired
    private lateinit var gptProperties: ChatGptProperties

    @Test
    fun toFlow(): Unit = runBlocking {
        // given
        val prompt = """
                #명령문
                - 너는 20대 여성의 친근한 말투를 사용하는 누구누구야
                - 아래의 제약 조건을 지켜서 ~~를 작성해줘 
                
                #제약 조건
                - 수치와 구체적인 정보를 사용해서 신빙성을 높여줘
                - 150자 이내로
                """

        val apiKey = gptProperties.firstKey()

        // when
        chatGptClient.queryStream(prompt, apiKey)
            .doOnNext { response ->
                response.choices.forEach { choice ->
                    print(choice.delta.content)
                    System.out.flush()
                }
            }
            .doOnError { error ->
                // 에러 로깅
                error.printStackTrace()
            }
            .blockLast() // 스트림의 마지막 요소가 처리될 때까지 기다림

    }

}
