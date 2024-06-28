package com.yscorp.simple.restdocs

import org.springframework.restdocs.payload.JsonFieldType

sealed class DocsFieldType(
	val type: JsonFieldType
)

data object ARRAY : DocsFieldType(JsonFieldType.ARRAY)
data object BOOLEAN : DocsFieldType(JsonFieldType.BOOLEAN)
data object OBJECT : DocsFieldType(JsonFieldType.OBJECT)
object NUMBER : DocsFieldType(JsonFieldType.NUMBER)
object NULL : DocsFieldType(JsonFieldType.NULL)
object STRING : DocsFieldType(JsonFieldType.STRING)
object ANY : DocsFieldType(JsonFieldType.VARIES)
object DATE : DocsFieldType(JsonFieldType.STRING)
object DATETIME : DocsFieldType(JsonFieldType.STRING)
