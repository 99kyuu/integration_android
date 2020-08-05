package com.virtusize.libsource.data.parsers

import com.google.common.truth.Truth.assertThat
import com.virtusize.libsource.TestFixtures
import com.virtusize.libsource.data.remote.ProductType
import com.virtusize.libsource.data.remote.Weight
import org.junit.Test

class ProductTypeJsonParserTest {

    @Test
    fun parse_validJsonData_shouldReturnExpectedObject() {
        val actualProductType = ProductTypeJsonParser().parse(TestFixtures.PRODUCT_TYPE_JSON_OBJECT)

        val expectedProductType = ProductType(
            1,
            mutableSetOf(
                Weight("bust", 1f),
                Weight("waist", 1f),
                Weight("height", 0.25f)
            )
        )

        assertThat(actualProductType).isEqualTo(expectedProductType)
    }

    @Test
    fun parse_emptyJsonData_shouldReturnExpectedObject() {
        val productType = ProductTypeJsonParser().parse(TestFixtures.EMPTY_JSON_DATA)

        assertThat(productType).isNull()
    }
}