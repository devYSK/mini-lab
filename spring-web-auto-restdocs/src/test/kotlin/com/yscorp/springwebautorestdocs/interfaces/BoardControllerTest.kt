package com.yscorp.springwebautorestdocs

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@DisplayName("게시판 api")
class BoardControllerTest : TestAbstractController() {

    @Test
    @DisplayName("게시글 전체 조회 테스트")
    fun `findAllPost`() { // 반드시 영어로 작성해야 한다.
        // Given: 게시글 추가
        val post = Post(1, "제목", "내용")
        mockMvc.perform(
            post("/api/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(post))
        ).andExpect(status().isCreated)

        // When: 게시글 조회
        mockMvc.perform(get("/api/board"))
            .andExpect(status().isOk)
            .andDo(
                document(
                    responseFields(
                        fieldWithPath("[].id").description("게시글 ID"),
                        fieldWithPath("[].title").description("게시글 제목"),
                        fieldWithPath("[].content").description("게시글 내용")
                    )
                )
            )
    }

    @Test
    @DisplayName("게시글 생성 테스트")
    fun createPost() {
        val post = Post(2, "새 게시글", "새 내용")

        mockMvc.perform(
            post("/api/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(post))
        )
            .andExpect(status().isCreated)
            .andDo(
                document(
                    requestFields(
                        fieldWithPath("id").description("게시글 ID"),
                        fieldWithPath("title").description("게시글 제목"),
                        fieldWithPath("content").description("게시글 내용")
                    ),
                    responseFields(
                        fieldWithPath("id").description("게시글 ID"),
                        fieldWithPath("title").description("게시글 제목"),
                        fieldWithPath("content").description("게시글 내용")
                    )
                )
            )
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    fun `updatePost`() {
        val post = Post(3, "수정 전 제목", "수정 전 내용")

        // Given: 게시글 추가
        mockMvc.perform(
            post("/api/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(post))
        ).andExpect(status().isCreated)

        // When: 게시글 수정
        val updatedPost = Post(3, "수정 후 제목", "수정 후 내용")
        mockMvc.perform(
            put("/api/board/{id}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(updatedPost))
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    requestFields(
                        fieldWithPath("id").description("게시글 ID"),
                        fieldWithPath("title").description("수정된 게시글 제목"),
                        fieldWithPath("content").description("수정된 게시글 내용")
                    ),
                    responseFields(
                        fieldWithPath("id").description("게시글 ID"),
                        fieldWithPath("title").description("수정된 게시글 제목"),
                        fieldWithPath("content").description("수정된 게시글 내용")
                    )
                )
            )
    }
}
