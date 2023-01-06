package com.andymcg.octopusgowatcher.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.cglib.core.Local
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*

@ConfigurationProperties(prefix = "go-watcher")
data class GoWatcherConfig
@ConstructorBinding
constructor(
    val offPeakStartTime: LocalTime = LocalTime.of(0,30,0),
    val offPeakEndTime: LocalTime = LocalTime.of(4,30,0),
    val meterMpan: String,
    val meterSerialNumber: String,
    val apiKey: String,
    val apiEndpoint: String
) {
    fun getEncodedApiKey(): String =
        Base64.getEncoder().encodeToString(apiKey.toByteArray())
}