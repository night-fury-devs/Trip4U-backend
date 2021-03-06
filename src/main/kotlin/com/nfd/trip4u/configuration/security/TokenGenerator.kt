package com.nfd.trip4u.configuration.security

import com.nfd.trip4u.configuration.SERVER_URL
import com.nfd.trip4u.entity.domain.User
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.commons.logging.LogFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component
import java.nio.charset.Charset
import java.util.*

/**
 * Author: Alexey Kleschikov
 * Date: 16 Jun 2016
 * Time: 22:17
 */
@Component
open class TokenGenerator {

    private val logger = LogFactory.getLog(this.javaClass)

    private val EXPIRED_TROUGH = 24 * 60 * 60 * 1000
    private val EMAIL_CLAIM = "email"
    private val ROLES = "roles"
    private val UTF8 = "UTF-8"
    private val KEY = "5E3b86m26SKXhet8d9Y1UVl2p62AUoYRhLmXd6S6mh7dM0AMd6LXEC22VHSVb7hk"
    private val BAD_TOKEN = "Invalid token provided."
    private val TOKEN_EXPIRED = "Token expired."

    fun generateForAuthentication(authentication: Authentication): String {
        return Jwts.builder()
                .setIssuer(SERVER_URL)
                .setSubject(authentication.principal.toString())
                //TODO: Add roles to token
                .setExpiration(Date(System.currentTimeMillis() + EXPIRED_TROUGH))
                .signWith(SignatureAlgorithm.HS512, KEY)
                .compact()
    }

    fun generateForConfirmation(user: User): String {
        val token = Jwts.builder()
                .setIssuer(SERVER_URL)
                .setExpiration(Date(System.currentTimeMillis() + EXPIRED_TROUGH))
                .setSubject(user.username)
                .setIssuedAt(Date())
                .claim(EMAIL_CLAIM, user.email)
                .signWith(SignatureAlgorithm.HS512, KEY)
                .compact()

        return Base64.getEncoder().encodeToString(token.toByteArray(charset = Charset.forName(UTF8)))
    }

    fun parseAuthenticationToken(token: String): Authentication {
        try {
            val claims = Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).body

            if (claims.expiration.before(Date())) {
                throw BadCredentialsException(TOKEN_EXPIRED)
            }

            //TODO: Add retrieving roles from token

            val resultAuth = PreAuthenticatedAuthenticationToken(claims.subject, null)
            resultAuth.isAuthenticated = true

            return resultAuth
        } catch (ex: JwtException) {
            throw BadCredentialsException(BAD_TOKEN, ex)
        }
    }

    fun parseConfirmationToken(encodedToken: String): String {
        val token = String(Base64.getDecoder().decode(encodedToken), Charset.forName(UTF8))

        try {
            val claims = Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).body

            if (claims.expiration.before(Date())) {
                throw BadCredentialsException(TOKEN_EXPIRED)
            }

            return claims.subject
        } catch (ex: JwtException) {
            throw BadCredentialsException(BAD_TOKEN, ex)
        }
    }
}