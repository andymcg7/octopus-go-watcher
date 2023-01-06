package com.andymcg.octopusgowatcher.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.Clock
import java.time.Instant
import java.time.ZoneId


@Configuration
class TestConfiguration {

    @Primary
    @Bean
    fun fixedClock(): Clock {
        return Clock.fixed(
            Instant.parse("2023-01-05T10:05:23.653Z"),
            ZoneId.of("Europe/London")
        )
    }
}