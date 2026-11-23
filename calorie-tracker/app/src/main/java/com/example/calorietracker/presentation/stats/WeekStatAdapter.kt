package com.example.calorietracker.presentation.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.calorietracker.data.remote.dto.WeekStatDto
import com.example.calorietracker.databinding.ItemDayStatBinding
import com.example.calorietracker.util.DateUtil
import com.example.calorietracker.util.formatNumber

class WeekStatAdapter : ListAdapter<WeekStatDto, WeekStatAdapter.Holder>(Diff) {

    class Holder(val binding: ItemDayStatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemDayStatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val day = getItem(position)
        with(holder.binding) {
            dayDate.text = DateUtil.shortDisplay(day.date)
            dayCalories.text = buildString {
                append(formatNumber(day.calories))
                append(" / ")
                append(day.goal)
                append(" ккал")
            }
            dayProgress.progress = if (day.goal <= 0) 0 else {
                (day.calories / day.goal * 100).toInt().coerceIn(0, 100)
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<WeekStatDto>() {
        override fun areItemsTheSame(oldItem: WeekStatDto, newItem: WeekStatDto): Boolean =
            oldItem.date == newItem.date

        override fun areContentsTheSame(oldItem: WeekStatDto, newItem: WeekStatDto): Boolean =
            oldItem == newItem
    }
}
