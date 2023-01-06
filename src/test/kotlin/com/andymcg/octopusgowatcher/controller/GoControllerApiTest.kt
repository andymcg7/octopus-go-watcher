package com.andymcg.octopusgowatcher.controller

import com.andymcg.octopusgowatcher.config.GoWatcherConfig
import com.andymcg.octopusgowatcher.service.GoService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GoControllerApiTest {

    @Autowired
    lateinit var goService: GoService
    @Autowired
    lateinit var config: GoWatcherConfig
    @Autowired
    lateinit var mockMvc: MockMvc

    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun beforeEach() {
        mockServer = MockRestServiceServer.createServer(goService.restTemplate)
    }

    @Test
    fun `go watcher should return the correctly processed consumption data`() {
        // given
        mockServer.expect(
            ExpectedCount.once(),
            requestTo(URI("${config.apiEndpoint}/electricity-meter-points/${config.meterMpan}/meters/${config.meterSerialNumber}/consumption?page_size=25000&period_from=2022-01-05T00:00")))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Basic ${config.getEncodedApiKey()}"))
            .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(getSampleResponse())
            )

        val expectedResponse = """
            [{"type":"LAST_YEAR","totalConsumption":16.0,"goPercentage":81},{"type":"LAST_30_DAYS","totalConsumption":12.0,"goPercentage":83},{"type":"YESTERDAY","totalConsumption":6.0,"goPercentage":83}]
        """.trimIndent()

        // when...then
        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }

    private fun getSampleResponse(): String =
        """
            {
    "count": 12,
    "next": null,
    "previous": null,
    "results": [
        {
            "consumption": 0.5,
            "interval_start": "2022-01-31T00:00:00Z",
            "interval_end": "2022-01-31T00:30:00Z"
        },
        {
            "consumption": 1.5,
            "interval_start": "2022-01-31T00:30:00Z",
            "interval_end": "2022-01-31T01:00:00Z"
        },
        {
            "consumption": 1.5,
            "interval_start": "2022-01-31T04:00:00Z",
            "interval_end": "2022-01-31T04:30:00Z"
        },
        {
            "consumption": 0.5,
            "interval_start": "2022-01-31T04:30:00Z",
            "interval_end": "2022-01-31T05:00:00Z"
        },
        {
            "consumption": 0.5,
            "interval_start": "2022-12-06T00:00:00Z",
            "interval_end": "2022-12-06T00:30:00Z"
        },
        {
            "consumption": 2.5,
            "interval_start": "2022-12-06T00:30:00Z",
            "interval_end": "2022-12-06T01:00:00Z"
        },
        {
            "consumption": 2.5,
            "interval_start": "2022-12-06T04:00:00Z",
            "interval_end": "2022-12-06T04:30:00Z"
        },
        {
            "consumption": 0.5,
            "interval_start": "2022-12-06T04:30:00Z",
            "interval_end": "2022-12-06T05:00:00Z"
        },
        {
            "consumption": 0.5,
            "interval_start": "2023-01-04T00:00:00Z",
            "interval_end": "2023-01-04T00:30:00Z"
        },
        {
            "consumption": 2.5,
            "interval_start": "2023-01-04T00:30:00Z",
            "interval_end": "2023-01-04T01:00:00Z"
        },
        {
            "consumption": 2.5,
            "interval_start": "2023-01-04T04:00:00Z",
            "interval_end": "2023-01-04T04:30:00Z"
        },
        {
            "consumption": 0.5,
            "interval_start": "2023-01-04T04:30:00Z",
            "interval_end": "2023-01-04T05:00:00Z"
        }
    ]
}
        """.trimIndent()

}