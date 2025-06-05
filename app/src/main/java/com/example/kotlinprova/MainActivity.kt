package com.example.kotlinprova

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.kotlinprova.models.TaskAdapter
import com.example.kotlinprova.models.TaskDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var db: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val addButton = findViewById<FloatingActionButton>(R.id.add_button)
        addButton.setOnClickListener {
            startActivity(Intent(this, AddTask::class.java))
            finish()
        }

        db = Room.databaseBuilder(applicationContext, TaskDatabase::class.java, "task-db")
            .allowMainThreadQueries().build()

        recyclerView = findViewById(R.id.rec_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val tasks = db.taskDao().getAll()
        if (tasks.isEmpty()) {
            recyclerView.visibility = View.GONE
            findViewById<TextView>(R.id.empty_list).visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            findViewById<TextView>(R.id.empty_list).visibility = View.GONE

            lifecycleScope.launch {
                val tasks = db.taskDao().getAll()
                adapter = TaskAdapter(tasks) { updatedTask ->
                    lifecycleScope.launch {
                        db.taskDao().update(updatedTask)
                    }
                }

                recyclerView.adapter = adapter
            }
            recyclerView.adapter = adapter
        }
    }
}