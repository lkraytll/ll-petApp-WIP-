package com.example.languagelearning.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.languagelearning.data.remote.dto.CourseDto
import com.example.languagelearning.databinding.ItemTasksBinding

class CoursesAdapter(
    private val onCourseClick: (CourseDto) -> Unit,
) : ListAdapter<CourseDto, CoursesAdapter.CourseViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemTasksBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return CourseViewHolder(binding, onCourseClick)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CourseViewHolder(
        private val binding: ItemTasksBinding,
        private val onCourseClick: (CourseDto) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(course: CourseDto) {
            binding.tvTaskTitle.text = course.title
            binding.tvTaskLesson.text = course.description
            binding.root.setOnClickListener {
                onCourseClick(course)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CourseDto>() {
        override fun areItemsTheSame(oldItem: CourseDto, newItem: CourseDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CourseDto, newItem: CourseDto): Boolean {
            return oldItem == newItem
        }
    }
}
