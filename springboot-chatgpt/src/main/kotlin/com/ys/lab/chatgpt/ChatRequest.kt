package com.ys.lab.chatgpt

data class ChatRequest(
    val model: String,
    val messages: MutableList<Message> = mutableListOf(),
    val temperature: Double = 0.5,
    val stream: Boolean = false
) {
    private constructor(model: String, prompt: String, temperature: Double = 0.0, stream: Boolean = false) : this(
        model,
        mutableListOf(Message("user", prompt)),
        temperature,
        stream
    )

    data class Message(
        val role: String,
        val content: String,
    )

    companion object {
        fun stream(model: GptModel, prompt: String): ChatRequest {
            return ChatRequest(model = model.modelName, prompt = prompt, stream = true)
        }

        fun of(model: GptModel, prompt: String): ChatRequest {
            return ChatRequest(model = model.modelName, prompt = prompt, stream = false)
        }
    }

}