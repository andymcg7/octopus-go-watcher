package com.andymcg.octopusgowatcher.model

import java.math.BigDecimal

data class ProcessedResults(
    val results: List<ConsumptionResult>
)

enum class TimePeriod {
    LAST_YEAR,
    LAST_30_DAYS,
    YESTERDAY
}

data class ConsumptionResult(
    val type: TimePeriod,
    val totalConsumption: BigDecimal,
    val goPercentage: Int
)
