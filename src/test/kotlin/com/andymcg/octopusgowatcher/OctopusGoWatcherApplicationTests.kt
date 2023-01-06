package com.andymcg.octopusgowatcher

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.support.GenericApplicationContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class OctopusGoWatcherApplicationTests {

	@Autowired lateinit var genericApplicationContext: GenericApplicationContext

	@Test
	fun contextLoads() {
		then(genericApplicationContext.isRunning)
	}

}
