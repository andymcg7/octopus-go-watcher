package com.andymcg.octopusgowatcher.service

import com.andymcg.octopusgowatcher.Constants
import com.andymcg.octopusgowatcher.LoggerDelegate
import com.andymcg.octopusgowatcher.config.GoWatcherConfig
import com.andymcg.octopusgowatcher.model.ConsumptionResult
import com.andymcg.octopusgowatcher.model.OctopusConsumptionResult
import com.andymcg.octopusgowatcher.model.OctopusConsumptionResults
import com.andymcg.octopusgowatcher.model.TimePeriod
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class GoService(
    restTemplateBuilder: RestTemplateBuilder,
    private val config: GoWatcherConfig,
    private val clock: Clock
) {
    companion object {
        val logger by LoggerDelegate()
    }

    val restTemplate: RestTemplate =
        restTemplateBuilder
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic ${config.getEncodedApiKey()}")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

    @Cacheable(Constants.RESULTS_CACHE_KEY)
    fun getConsumptionData(): List<ConsumptionResult> {
        val uriComponents: UriComponents = UriComponentsBuilder.fromHttpUrl(
            "${config.apiEndpoint}/electricity-meter-points/${config.meterMpan}/meters/${config.meterSerialNumber}/consumption")
            .queryParam("page_size", "25000")
            .queryParam("period_from", LocalDate.now(clock).minusYears(1).atStartOfDay())
            .encode()
            .build()

        logger.info("Fetching from Octopus")

        val octResults: OctopusConsumptionResults? = restTemplate.getForObject(uriComponents.toUriString())
        return octResults?.let { it ->

            val yearlyResults = calculateResultsOverItems(it.results, TimePeriod.LAST_YEAR)

            val thirtyDaysAgo = LocalDate.now(clock).minusDays(30).atStartOfDay()
            val endOfYesterday = LocalDate.now(clock).atStartOfDay()
            val thirtyDayRange = thirtyDaysAgo..endOfYesterday
            val last30DaysItems = it.results.filter { it.intervalStart.toLocalDateTime() in thirtyDayRange }

            val last30DaysResults = calculateResultsOverItems(last30DaysItems, TimePeriod.LAST_30_DAYS)

            val startOfYesterday = LocalDate.now(clock).minusDays(1).atStartOfDay()
            val yesterdayRange = startOfYesterday..endOfYesterday
            val yesterdaysItems = last30DaysItems.filter { it.intervalStart.toLocalDateTime() in yesterdayRange }

            val yesterdaysResults = calculateResultsOverItems(yesterdaysItems, TimePeriod.YESTERDAY)

            return listOf(yearlyResults, last30DaysResults, yesterdaysResults)
        } ?: emptyList()
    }

    private fun calculateResultsOverItems(results: List<OctopusConsumptionResult>, timePeriod: TimePeriod): ConsumptionResult {
        val totalConsumption = results.sumOf { entry -> entry.consumption }
        val offPeakConsumptionItems: List<BigDecimal> = results.mapNotNull { octopusConsumptionResult ->
            val startOfDay: LocalDateTime = octopusConsumptionResult.intervalStart.toLocalDate().atStartOfDay()
            val offPeakStartTime = config.offPeakStartTime.atDate(startOfDay.toLocalDate())
            val offPeakEndTime = config.offPeakEndTime.atDate(startOfDay.toLocalDate())
            val offPeakRange = offPeakStartTime..offPeakEndTime
            if (octopusConsumptionResult.intervalStart.toLocalDateTime() in offPeakRange &&
                octopusConsumptionResult.intervalEnd.toLocalDateTime() in offPeakRange
            ) {
                octopusConsumptionResult.consumption
            } else null
        }
        val total = offPeakConsumptionItems.sumOf { it }
        val goPercentage = ((total.setScale(2).divide(totalConsumption, MathContext.DECIMAL128)).multiply(100.00.toBigDecimal())).toInt()
        return ConsumptionResult(
            type = timePeriod,
            totalConsumption = totalConsumption,
            goPercentage = goPercentage
        )
    }

    @Scheduled(fixedRateString = "30000")
    @CacheEvict(value = [Constants.RESULTS_CACHE_KEY], allEntries = true)
    fun evictResultsCache() {
        logger.info("results cache evicted")
    }


}