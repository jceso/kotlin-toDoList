package com.example.kotlinprova.models

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinprova.MainActivity
import com.example.kotlinprova.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TaskAdapter(private val tasks: List<Task>, private val onTaskUpdate: (Task) -> Unit) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.task_title)
        val time: TextView = itemView.findViewById(R.id.task_time)
        val check: ImageView = itemView.findViewById(R.id.is_completed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title

        task.dueTime?.let { dueTimeMillis ->
            val dateTime = Instant.ofEpochMilli(dueTimeMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

            val formatted = "Due time: " + dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            holder.time.text = formatted

            // Compare with current time
            val now = java.time.LocalDateTime.now()
            if (dateTime.isAfter(now))
                holder.time.setTextColor(holder.itemView.context.getColor(R.color.green)) // Use your actual color resource
            else
                holder.time.setTextColor(holder.itemView.context.getColor(R.color.red))
        }

        if (task.isCompleted) {
            holder.check.setImageResource(R.drawable.ic_check_full)
            holder.time.setTextColor(holder.itemView.context.getColor(R.color.black))
            holder.title.paint.isStrikeThruText = true
            holder.time.paint.isStrikeThruText = true
        } else
            holder.check.setImageResource(R.drawable.ic_check_empty)

        holder.check.setOnClickListener {
            task.isCompleted = !task.isCompleted
            onTaskUpdate(task)  // Notify the parent to update in DB
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = tasks.size
}