package com.example.calorietracker.presentation.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.calorietracker.data.remote.dto.EntryDto
import com.example.calorietracker.databinding.ItemEntryBinding
import com.example.calorietracker.util.formatNumber


class EntryAdapter(
    private val onEdit: (EntryDto) -> Unit,
    private val onDelete: (EntryDto) -> Unit
) : ListAdapter<EntryDto, EntryAdapter.Holder>(Diff) {

    class Holder(val binding: ItemEntryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val entry = getItem(position)
        with(holder.binding) {
            foodName.text = entry.foodName
            entryCalories.text = buildString {
                append(formatNumber(entry.calories))
                append(" ккал")
            }
            editButton.setOnClickListener { onEdit(entry) }
            deleteButton.setOnClickListener { onDelete(entry) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<EntryDto>() {
        override fun areItemsTheSame(oldItem: EntryDto, newItem: EntryDto): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EntryDto, newItem: EntryDto): Boolean =
            oldItem == newItem
    }
}
