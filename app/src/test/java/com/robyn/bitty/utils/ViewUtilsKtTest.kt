package com.robyn.bitty.utils

import org.junit.Test

class ViewUtilsKtTest {

    @Test
    fun trimText() {
        val input =
            "RT @kengarex: Before/after photographs reveal how the world has changed… (32 photos) https://t.co/vmpmTRlLjC https://t.co/C13q9K5AfX"
        val expectedOutput =
            "RT @kengarex: Before/after photographs reveal how the world has changed… (32 photos)  "
        val output = removeLastHttps(input)

        check(output == expectedOutput)
    }
}