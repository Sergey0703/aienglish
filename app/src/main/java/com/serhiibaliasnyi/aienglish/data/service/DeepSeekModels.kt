package com.serhiibaliasnyi.aienglish.data.service

data class Message(
    val role: String,
    val content: String
)

data class DeepSeekRequest(
    val model: String = "deepseek-chat",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class Choice(
    val message: Message,
    val index: Int
)

data class DeepSeekResponse(
    val choices: List<Choice>
) 