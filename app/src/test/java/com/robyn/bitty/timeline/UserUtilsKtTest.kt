package com.robyn.bitty.timeline

import org.junit.Assert.*
import org.junit.Test

class UserUtilsKtTest {

    @Test fun modifyUrl() {
        val inputUrl = "http://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png"
        val expectedUrl = "http://pbs.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_bigger.png"
        val outputUrl = urlBigger(inputUrl)

        check(outputUrl.matches(Regex.fromLiteral(expectedUrl)))
    }
}