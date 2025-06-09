package com.example.kotlinprova.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@Entity(tableName = "task_table")
class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,           // Identificatore unico
    @ColumnInfo(name = "title") val title: String,                // Titolo della task
    @ColumnInfo(name = "desc") val desc: String,                // Descrizione
    @ColumnInfo(name = "dueTimeLong") val dueTime: Long?,       // Data di scadenza (opzionale)
    @ColumnInfo(name = "isCompleted") var isCompleted: Boolean, // Stato della task (completata/non completata)
)
{
    fun dueTime(): LocalTime? = if (dueTime == null) null
        else Instant.ofEpochMilli(dueTime).atZone(ZoneId.systemDefault()).toLocalTime()
}