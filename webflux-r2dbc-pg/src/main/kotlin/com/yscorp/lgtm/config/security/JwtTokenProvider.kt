package com.yscorp.webflux.config.security

import com.yscorp.lgtm.common.TimeUtil
import com.yscorp.lgtm.domain.UserRole
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.Key
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class JwtTokenProvider(
    jwtProperties: JwtProperties,
) {

    private var key: Key? = null

    private val secretKey: String = jwtProperties.secretKey

    private var accessTokenExpirationSecond: Long = jwtProperties.accessTokenExpirationSecond

    val refreshTokenExpirationSecond: Long = jwtProperties.refreshTokenExpirationSecond

    private val refreshSecretKey: String = jwtProperties.secretKey

    init {
        val keyBytes = secretKey.toByteArray(StandardCharsets.UTF_8)
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun createAccessToken(userId: Long, username: String, role: UserRole): Pair<String, LocalDateTime> {
        val claims = Jwts.claims().subject(userId.toString())
        val issuedAt = LocalDateTime.now()

        claims.add("id", userId)
        claims.add("role", role)
        claims.add("username", username)
        claims.add("createdAt", TimeUtil.toLong(issuedAt))

        val claimBuild = claims.build()

        val expiredDate = accessTokenExpirationTime()

        return Jwts.builder()
            .claims(claimBuild)
            .issuedAt(TimeUtil.toDate(issuedAt))
            .expiration(TimeUtil.toDate(expiredDate))
            .signWith(key)
            .compact() to expiredDate
    }

    fun reIssueAccessToken(refreshToken: String): Pair<String, LocalDateTime> {
        val tokenInfo = parse(refreshToken)

        return createAccessToken(tokenInfo.userId, tokenInfo.username, tokenInfo.role)
    }

    fun createRefreshToken(userId: Long, username: String, role: UserRole): Pair<String, LocalDateTime> {
        val claims = Jwts.claims().subject(userId.toString())
        val issuedAt = LocalDateTime.now()

        claims.add("id", userId)
        claims.add("role", role)
        claims.add("username", username)
        claims.add("createdAt", TimeUtil.toLong(issuedAt))

        val claimBuild = claims.build()

        val expiredDate = refreshTokenExpirationTime()

        val first = Jwts.builder()
            .claims(claimBuild)
            .issuedAt(TimeUtil.toDate(issuedAt))
            .expiration(TimeUtil.toDate(expiredDate))
            .signWith(key)
            .compact()

        return Pair(
            first, expiredDate
        )
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun parse(accessToken: String): TokenInfo {
        val claims = getClaims(accessToken)

        val id = claims["id"].toString().toLong()
        val username = claims["username"].toString()
        val role = UserRole.valueOf(claims["role"].toString())

        return TokenInfo(
            accessToken,
            id,
            username,
            role
        )
    }


    fun getExpireTime(token: String): LocalDateTime {
        val time = getClaims(token).expiration
        return time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun getCreatedAt(token: String): LocalDateTime {
        val dateLong = getClaims(token)["createdAt"]
        val date = Date(dateLong as Long)  // Type casting
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun validateJwtToken(token: String) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token)
        } catch (e: ExpiredJwtException) {
            throw RuntimeException("토큰이 유효하지 않습니다. 만료된 JWT 토큰입니다.", e);
        } catch (e: SecurityException) {
            throw RuntimeException(e)
        } catch (e: MalformedJwtException) {
            throw RuntimeException(e)
        } catch (e: UnsupportedJwtException) {
            throw RuntimeException(e)
        } catch (e: IllegalArgumentException) {
            throw RuntimeException(e)
        }
    }

    fun accessTokenExpirationTime(): LocalDateTime {
        return LocalDateTime.now().plusSeconds(accessTokenExpirationSecond)
    }

    fun refreshTokenExpirationTime(): LocalDateTime {
        return LocalDateTime.now().plusSeconds(refreshTokenExpirationSecond)
    }

}
