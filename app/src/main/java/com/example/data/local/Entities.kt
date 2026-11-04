package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "USER" or "JARVIS"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val hasAnalysis: Boolean = false,
    val analysisTitle: String? = null,
    val analysisSeverity: String? = null,
    val stressMetric: String? = null,
    val vectorMetric: String? = null
)

@Entity(tableName = "recent_commands")
data class RecentCommandEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prompt: String,
    val summary: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean = true,
    val category: String = "COMMAND"
)
