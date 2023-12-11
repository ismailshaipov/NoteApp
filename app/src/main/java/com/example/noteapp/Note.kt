package com.example.noteapp

import java.time.LocalDateTime

enum class Priority {
    NONE,
    LOW,
    MEDIUM,
    HIGH
}

data class Note(
    var id: Int,
    var title: String,
    var content: String,
    val creationTime: LocalDateTime = LocalDateTime.now(),
    var priority: Priority = Priority.NONE
)
