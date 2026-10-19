package com.example.languagelearning.presentation.items

data class TaskItem (
    var id: Int,
    val title: String,
    val lesson: String,
    var isComplete: Boolean
)