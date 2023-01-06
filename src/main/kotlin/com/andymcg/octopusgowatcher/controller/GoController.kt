package com.andymcg.octopusgowatcher.controller

import com.andymcg.octopusgowatcher.model.ConsumptionResult
import com.andymcg.octopusgowatcher.service.GoService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class GoController(
    private val service: GoService
) {

    @RequestMapping("/")
    @ResponseBody
    fun getConsumptionData(): List<ConsumptionResult> = service.getConsumptionData()
}