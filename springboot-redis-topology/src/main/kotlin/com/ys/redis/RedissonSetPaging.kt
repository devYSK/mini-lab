package com.ys.redis

@Component
class RedissonSetPaging(
    private val redissonClient: RedissonClient,
) {

    val key = "keyword:set"

    fun getReplaceKeywordsSet() = redissonClient.getSet<String>(replaceKeywordSetKey)


    fun findAllReplaceKeywords(pageable: Pageable): ReplaceKeywordsResponse? {
        val replaceKeywordSet = getReplaceKeywordsSet()
        val replaceKeywords = replaceKeywordSet
            .readSortAlpha(SortOrder.ASC, pageable.offset.toInt(), pageable.pageSize)

        val pageContent = replaceKeywords.map {
            ReplaceKeyword(it)
        }.toMutableList()

        return ReplaceKeywordsResponse.from(PageImpl(pageContent, pageable, replaceKeywordSet.size.toLong()))
    }

}