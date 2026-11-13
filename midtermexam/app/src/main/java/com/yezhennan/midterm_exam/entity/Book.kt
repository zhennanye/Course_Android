package com.yezhennan.midterm_exam.entity

import java.io.Serializable

data class Book(
    val title: String,
    val author: String,
    val publisher: String,
    val price: Double,
    val isbn: String,
    val description: String
) : Serializable