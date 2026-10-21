package com.yezhennan.exp04

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerInfoTest {

    @After
    fun tearDown() {
        PlayerInfo.name = "张三"
        PlayerInfo.gender = "男"
        PlayerInfo.phone = "13800000000"
        PlayerInfo.highScore = 0
    }

    @Test
    fun testModifyInfo() {
        PlayerInfo.name = "李四"
        PlayerInfo.gender = "女"
        PlayerInfo.phone = "123456"
        PlayerInfo.highScore = 42

        assertEquals("李四", PlayerInfo.name)
        assertEquals("女", PlayerInfo.gender)
        assertEquals("123456", PlayerInfo.phone)
        assertEquals(42, PlayerInfo.highScore)
    }
}