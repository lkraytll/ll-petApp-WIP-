package com.example.languagelearning.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.languagelearning.databinding.ItemTasksBinding
import com.example.languagelearning.presentation.items.TaskItem

class CompletedTaskAdapter :
    ListAdapter<TaskItem, CompletedTaskAdapter.TaskViewHolder>(
        DiffCallBack
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {

        val binding = ItemTasksBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class TaskViewHolder(
        private val binding: ItemTasksBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TaskItem) {

            binding.ivCompleted.visibility =
                if (item.isComplete) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            binding.tvTaskTitle.text = item.title
            binding.tvTaskLesson.text = item.lesson
        }
    }
    private object DiffCallBack : DiffUtil.ItemCallback<TaskItem>(){
        override fun areItemsTheSame(
            oldItem: TaskItem,
            newItem: TaskItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TaskItem,
            newItem: TaskItem
        ): Boolean {
            return oldItem == newItem
        }

    }
}