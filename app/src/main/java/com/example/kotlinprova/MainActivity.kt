package com.example.kotlinprova

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.kotlinprova.models.TaskAdapter
import com.example.kotlinprova.models.TaskDatabase

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


        db = Room.databaseBuilder(applicationContext, TaskDatabase::class.java, "task-db")
            .allowMainThreadQueries().build()

        recyclerView = findViewById(R.id.rec_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val tasks = db.taskDao().getAll()
        adapter = TaskAdapter(tasks)
        recyclerView.adapter = adapter
    }
}