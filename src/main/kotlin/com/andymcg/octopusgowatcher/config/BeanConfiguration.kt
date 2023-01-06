package com.andymcg.octopusgowatcher.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class BeanConfiguration {

    @Bean
    fun clock(): Clock = Clock.systemUTC()
}