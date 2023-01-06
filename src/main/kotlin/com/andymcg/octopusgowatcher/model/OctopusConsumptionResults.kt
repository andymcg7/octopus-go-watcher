package com.andymcg.octopusgowatcher.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal
import java.time.ZonedDateTime

data class OctopusConsumptionResults(
    val count: Long,
    val next: String?,
    val previous: String?,
    val results: List<OctopusConsumptionResult>
)

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OctopusConsumptionResult(
    val consumption: BigDecimal,
    val intervalStart: ZonedDateTime,
    val intervalEnd: ZonedDateTime
)
