package com.yscorp.lgtm.common

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

object TimeUtil {

    private val SEOUL_ZONE_ID: ZoneId = ZoneId.of("Asia/Seoul")

    fun toLong(localDateTime: LocalDateTime) : Long {
        val seoulZoneId: ZoneId = SEOUL_ZONE_ID
        val seoulOffset: ZoneOffset = seoulZoneId.rules.getOffset(localDateTime)

        return localDateTime.toInstant(seoulOffset).toEpochMilli()
    }

    fun toDate(localDateTime: LocalDateTime) : Date {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault())
            .toInstant()
        )
    }

}