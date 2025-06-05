package com.example.kotlinprova

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.kotlinprova.models.Task
import com.example.kotlinprova.models.TaskDatabase
import kotlinx.coroutines.*
import java.util.Calendar
import java.util.Locale

class AddTask : AppCompatActivity() {
    private lateinit var titleET: EditText
    private lateinit var descET: EditText
    private lateinit var dueTime: TextView
    private lateinit var saveBtn: Button
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dueTime = findViewById(R.id.due_time)
        dueTime.setOnClickListener {
            showDateTimePicker()
        }

        titleET = findViewById(R.id.title)
        descET = findViewById(R.id.desc)
        saveBtn = findViewById(R.id.save_btn)

        val db = Room.databaseBuilder(applicationContext, TaskDatabase::class.java, "task-db").build()
        val taskDao = db.taskDao()

        saveBtn.setOnClickListener {
            val title = titleET.text.toString().trim()
            val description = descET.text.toString().trim()
            val dueTimeMillis = calendar.timeInMillis

            if (title.isNotEmpty() && description.isNotEmpty()) {
                val newTask = Task(
                    title = title,
                    desc = description,
                    dueTime = dueTimeMillis,
                    isCompleted = false
                )

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) { taskDao.insert(newTask) }
                    Toast.makeText(this@AddTask, "Task salvata!", Toast.LENGTH_SHORT).show()
                    Log.d("AddTask", "Task aggiunta al database: $newTask")
                    startActivity(Intent(this@AddTask, MainActivity::class.java))
                    finish()
                }
            } else
                Toast.makeText(this, "Completa tutti i campi", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDateTimePicker() {
        // Step 1: Date Picker
        DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Step 2: Time Picker
                TimePickerDialog(this,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        // Formatta data/ora e mostrala nel TextView
                        val formatted = String.format(Locale.getDefault(), "%02d/%02d/%04d %02d:%02d", dayOfMonth, month + 1, year, hourOfDay, minute)
                        dueTime.text = formatted
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}