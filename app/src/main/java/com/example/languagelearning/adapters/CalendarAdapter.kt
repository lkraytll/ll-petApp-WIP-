package com.example.languagelearning.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.languagelearning.databinding.ItemDayBinding
import com.example.languagelearning.presentation.items.DayItem

class CalendarAdapter :
    ListAdapter<DayItem, CalendarAdapter.DayViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DayViewHolder {

        val binding = ItemDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DayViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class DayViewHolder(
        private val binding: ItemDayBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: DayItem) {
            binding.tvDayName.text = day.dayName
            binding.tvDayNumber.text = day.dayNumber.toString()

            binding.root.setBackgroundResource(
                if (day.isSelected) {
                    com.example.languagelearning.R.drawable.bg_day_selected
                } else {
                    com.example.languagelearning.R.drawable.bg_day
                },
            )
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<DayItem>() {

        override fun areItemsTheSame(
            oldItem: DayItem,
            newItem: DayItem,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DayItem,
            newItem: DayItem,
        ): Boolean {
            return oldItem == newItem
        }
    }
}