package com.andymcg.octopusgowatcher

object TestUtils {

    fun classpathResourceAsString(path: String): String =
        TestUtils::class.java.getResource(path).readText(Charsets.UTF_8)
}