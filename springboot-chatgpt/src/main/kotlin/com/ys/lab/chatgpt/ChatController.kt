package com.ys.lab.chatgpt

import com.ys.lab.chatgpt.config.ChatGptProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux


@RestController
class ChatController(
    @Qualifier("openaiWebClient") val webClient: WebClient,
    private val chatGptClient: ChatGptClient,
    private val chatGptProperties: ChatGptProperties,
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/chat/gpt3.5")
    suspend fun chat(@RequestParam prompt: String): ChatResponse {

        log.info("key : ${chatGptProperties.firstKey()}, $chatGptProperties")

        return chatGptClient.query(prompt, chatGptProperties.firstKey(), GptModel.GPT_3_5_TURBO)
    }

    @GetMapping("/chat/gpt4")
    suspend fun chatPostGpt4(@RequestParam prompt: String): ChatResponse? {
        return chatGptClient.query(prompt, chatGptProperties.firstKey(), GptModel.GPT_4)
    }

    @GetMapping("/chat/stream")
    fun chatPostGptStream(@RequestParam prompt: String): Flux<ChatStreamResponse> {
        return chatGptClient.queryStream(prompt, chatGptProperties.firstKey())
    }

    @GetMapping("/chat/stream/coroutine")
    suspend fun chatPostGptStreamCoroutine(@RequestParam prompt: String): List<ChatStreamResponse> {
        return chatGptClient.queryStreamCoroutine(prompt, chatGptProperties.firstKey())
    }


//    @PostMapping("/chat/trim")
//    suspend fun chatPostGpt4Trim(@RequestBody prompt: String): String {
//        val lines = prompt.trim().split(".")
//        val groupedLines = lines.chunked(5) { it.joinToString(".") + "." }
//        logger.info("그룹 갯수 : ${groupedLines.size}" )
//        return supervisorScope {
//            val deferredResponses: List<Deferred<String>> = groupedLines.map { group ->
//                async {
//                    call(group)
//                }
//            }
//
//            val responses: List<String> = deferredResponses.map { it.await() }
//            responses.joinToString(".")
//        }
//    }
//
//    suspend fun call(str: String): String {
//        val prompt = """
//                """
//        val request = ChatRequest("gpt-3.5-turbo",
//            prompt
//        )
//
//        logger.info("Calling API with string: $prompt")
//
//        val chatResponse = webClient.post()
//            .uri("/v1/chat/completions")
//            .bodyValue(request)
//            .retrieve()
//            .bodyToMono<ChatResponse>()
//            .timeout(Duration.ofSeconds(300))
//            .awaitSingleOrNull()
//
//        return chatResponse?.choices?.get(0)?.message?.content ?: "No response"
//    }

}