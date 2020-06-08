package com.virtusize.libsource.data.remote.parsers

import com.google.common.truth.Truth.assertThat
import com.virtusize.libsource.data.JsonResponseSamples
import com.virtusize.libsource.data.remote.Data
import com.virtusize.libsource.data.remote.UserData
import org.junit.Test

class DataJsonParserTest {

    @Test
    fun parse_shouldReturnExpectedObject() {
        val actualData = DataJsonParser().parse(JsonResponseSamples.PRODUCT_DATA_CHECK_DATA)
        val expectedData = Data(
            true,
            false,
            UserData(false, false, false, false, false),
            7110384,
            "pants",
            "virtusize",
            2,
            5
        )

        assertThat(actualData).isEqualTo(expectedData)
    }
}