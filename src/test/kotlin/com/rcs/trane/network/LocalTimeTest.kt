package com.rcs.trane.network

import org.assertj.core.api.Assertions.assertThat
import org.example.com.rcs.trane.network.LocalTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.assertFailsWith

class LocalTimeTest {

    @Test
    fun `test happy path`() {
        // Arrange
        val instantToday = LocalDateTime.of(
            LocalDateTime.now().toLocalDate(),
            java.time.LocalTime.parse("17:54"))
            .toInstant(ZoneOffset.UTC)

        // Act
        val localTime = LocalTime("17:54")

        // Assert
        assertThat(localTime.toInstantToday()).isEqualTo(instantToday)
    }

    @ParameterizedTest
    @CsvSource("-30", "-1", "24", "99")
    fun `test invalid hour`(hour: Int) {
        // Act && Assert
        assertFailsWith<IllegalArgumentException> {
            LocalTime("$hour:20")
        }
    }

    @ParameterizedTest
    @CsvSource("-30", "-1", "60", "99")
    fun `test invalid minute`(minute: Int) {
        // Act && Assert
        assertFailsWith<IllegalArgumentException> {
            LocalTime("10:$minute")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["asdfasdf", "1212", " ", ""])
    fun `test invalid format`(string: String) {
        // Act && Assert
        assertFailsWith<IllegalArgumentException> {
            LocalTime(string)
        }
    }
}