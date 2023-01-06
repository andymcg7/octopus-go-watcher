package com.andymcg.octopusgowatcher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableCaching
@ConfigurationPropertiesScan(value = ["com.andymcg.octopusgowatcher.config"])
class OctopusGoWatcherApplication

fun main(args: Array<String>) {
	runApplication<OctopusGoWatcherApplication>(*args)
}

object Constants {
	const val RESULTS_CACHE_KEY = "consumption-data"
}