package com.yezhennan.exp04

object PlayerInfo {
    var name: String = "张三"
    var gender: String = "男"
    var phone: String = "13800000000"
    var highScore: Int = 0

    fun snapshot(): PlayerProfile = PlayerProfile(name, gender, phone, highScore)

    fun restore(profile: PlayerProfile) {
        name = profile.name
        gender = profile.gender
        phone = profile.phone
        highScore = profile.highScore
    }
}

data class PlayerProfile(
    val name: String,
    val gender: String,
    val phone: String,
    val highScore: Int
) : java.io.Serializable