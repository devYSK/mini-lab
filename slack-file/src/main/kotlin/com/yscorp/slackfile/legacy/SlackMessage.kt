package com.yscorp.slackfile.legacy

import com.slack.api.model.block.*
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.composition.TextObject
import com.slack.api.model.block.element.ImageElement
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


open class SlackMessage private constructor() {

    private var blocks: MutableList<LayoutBlock> = mutableListOf()

    init {
        blocks = mutableListOf()
    }

    fun blocks(): List<LayoutBlock> {
        return blocks
    }

    class MessageBuilder {
        private val slackMessage: SlackMessage = SlackMessage()

        fun titleBlock(headerTitle: String): MessageBuilder {
            slackMessage.blocks.add(headerBlock(headerTitle))
            return this
        }

        fun sectionBlock(fields: List<TextObject>): MessageBuilder {
            val section = section(fields)
            slackMessage.blocks.add(section)
            return this
        }

        fun sectionBlock(field: TextObject): MessageBuilder {
            val section = section(field)
            slackMessage.blocks.add(section)
            return this
        }

        fun sectionBlock(key: String, value: String): MessageBuilder {
            val section = section(markdownFieldV2(key, value))
            slackMessage.blocks.add(section)
            return this
        }

        fun dividerBlock(): MessageBuilder {
            slackMessage.blocks.add(divider())
            return this
        }

        fun imageBlock(sectionText: String?, imageUrl: String?, imageText: String?): MessageBuilder {
            val layoutBlock = sectionWithImage(markdownField(sectionText), imageUrl, imageText)
            slackMessage.blocks.add(layoutBlock)
            return this
        }

        fun image(imageUrl: String?): MessageBuilder {
            val image = Companion.image(imageUrl)
            slackMessage.blocks.add(image)
            return this
        }

        fun image(imageUrl: String?, imageText: String?): MessageBuilder {
            val image = Companion.image(imageUrl, imageText)
            slackMessage.blocks.add(image)
            return this
        }

        fun build(): SlackMessage {
            require(slackMessage.blocks().isNotEmpty()) { "메시지가 비어있습니다." }
            return slackMessage
        }
    }

    enum class LogLevel(
        val text: String,
        val logTitle: LogTitle,
    ) {
        ERROR("error", LogTitle.ERROR),
        WARN("warn", LogTitle.WARN),
        INFO("info", LogTitle.INFO),
        DEBUG("debug", LogTitle.DEBUG),
        TRACE("trace", LogTitle.TRACE);

        companion object {
            fun get(loglevel: LogLevel): LogLevel {
                return values().first { it == loglevel }
            }
        }
    }

    enum class LogTitle(
        // 연초록
        val text: String,
    ) {
        ERROR("\uD83D\uDD34 error Level \uD83D\uDD34"),  // 빨강
        WARN("\uD83D\uDFE0 warn Level \uD83D\uDFE0"),  // 주황
        INFO("\uD83D\uDFE2 info Level \uD83D\uDFE2"),  // 초록
        DEBUG("\uD83D\uDFE3 debug Level \uD83D\uDFE3"),  // 보라
        TRACE("\uD83D\uDD35 trace Level \uD83D\uDD35");

        companion object {
            fun of(logStr: String): LogTitle {
                return Arrays.stream(LogTitle.values())
                    .filter { logTitle: LogTitle ->
                        logTitle.name == logStr.uppercase(Locale.getDefault())
                    }
                    .findFirst()
                    .orElseThrow { RuntimeException(logStr) }
            }
        }
    }

    companion object {
        fun builder(): MessageBuilder {
            return MessageBuilder()
        }

        fun headerBlock(text: String?): LayoutBlock {
            return HeaderBlock.builder()
                .text(PlainTextObject(text, true))
                .build()
        }

        fun section(fields: List<TextObject>): LayoutBlock {
            return SectionBlock.builder()
                .fields(fields)
                .build()
        }

        fun section(field: TextObject): LayoutBlock {
            return SectionBlock.builder()
                .text(field)
                .build()
        }

        fun sectionWithImage(field: TextObject, imageUrl: String?, imageText: String?): LayoutBlock {
            val imageElement = ImageElement.builder()
                .imageUrl(imageUrl)
                .altText(imageText)
                .build()
            return SectionBlock.builder()
                .text(field)
                .accessory(imageElement)
                .build()
        }

        fun divider(): LayoutBlock {
            return DividerBlock()
        }

        fun markdownField(fieldTitle: String, text: String?): TextObject {
            return MarkdownTextObject.builder()
                .text("*`$fieldTitle`:*\n\n```$text```")
                .verbatim(true)
                .build()
        }

        fun markdownFieldV2(fieldTitle: String, text: String): TextObject {
            return MarkdownTextObject.builder()
                .text("*$fieldTitle*: $text \n")
                .verbatim(true)
                .build()
        }

        fun plainField(fieldTitle: String, text: String): TextObject {
            return PlainTextObject.builder()
                .text("$fieldTitle : $text \n")
                .emoji(true).build()
        }

        fun markdownField(text: String?): TextObject {
            return MarkdownTextObject.builder()
                .text(text)
                .verbatim(true)
                .build()
        }

        fun plainField(text: String?): TextObject {
            return PlainTextObject.builder()
                .text(text)
                .emoji(true).build()
        }

        fun image(imageUrl: String?): ImageBlock {
            return ImageBlock.builder()
                .imageUrl(imageUrl)
                .altText("image")
                .build()
        }

        fun image(imageUrl: String?, imageText: String?): ImageBlock {
            return ImageBlock.builder()
                .imageUrl(imageUrl)
                .altText(imageText)
                .build()
        }

        fun error(
            e: Throwable,
            requestUri: String? = null,
            method: String? = null,
            message: String? = null,
        ): SlackMessage {
            val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초 - ss"))

            val builder = builder()
                .dividerBlock()
                .titleBlock("❗ " + e.javaClass.simpleName)
                .sectionBlock(markdownFieldV2("time ", time))

            requestUri?.let {
                builder.sectionBlock(markdownFieldV2("request_path", it))
            }

            method?.let {
                builder.sectionBlock(markdownFieldV2("request_method", it))
            }

            builder.dividerBlock()
                .sectionBlock(markdownFieldV2("message", e.message ?: "에러"))
                .dividerBlock()

            message?.let {
                builder.sectionBlock(markdownFieldV2("추가메시지", it))
            }

            builder.sectionBlock(markdownFieldV2("cause", e.toStackTrace()))

            return builder.build()
        }

        fun message(
            title: String = "메시지",
            message: String
        ): SlackMessage {
            val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초 - ss"))
            return builder()
                .titleBlock("❗ $title")
                .dividerBlock()
                .sectionBlock(markdownFieldV2("time ", time))
                .dividerBlock()
                .sectionBlock(markdownFieldV2("message", message))
                .build()
        }

    }

}

fun Throwable.toStackTrace(length: Int = 1000): String {
    val stringWriter = StringWriter()
    val printWriter = PrintWriter(stringWriter)
    this.printStackTrace(printWriter)

    val fullStackTrace = stringWriter.toString()

    return fullStackTrace.take(fullStackTrace.length.coerceAtMost(length))
}

