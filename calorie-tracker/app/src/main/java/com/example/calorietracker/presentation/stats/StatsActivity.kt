package com.example.calorietracker.presentation.stats

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calorietracker.R
import com.example.calorietracker.data.Graph
import com.example.calorietracker.data.remote.dto.WeekStatsDto
import com.example.calorietracker.databinding.ActivityStatsBinding
import com.example.calorietracker.util.DateUtil
import com.example.calorietracker.util.Resource
import com.example.calorietracker.util.formatNumber


class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding
    private val viewModel: StatsViewModel by viewModels {
        viewModelFactory { initializer { StatsViewModel(Graph.repository) } }
    }
    private val adapter = WeekStatAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.retryButton.setOnClickListener { viewModel.load() }

        binding.daysList.layoutManager = LinearLayoutManager(this)
        binding.daysList.adapter = adapter

        viewModel.state.observe(this) { state -> render(state) }
    }

    private fun render(state: Resource<WeekStatsDto>) {
        when (state) {
            is Resource.Loading -> {
                binding.progress.isVisible = true
                binding.content.isVisible = false
                binding.errorBlock.isVisible = false
            }
            is Resource.Success -> {
                binding.progress.isVisible = false
                binding.content.isVisible = true
                binding.errorBlock.isVisible = false
                showStats(state.data)
            }
            is Resource.Error -> {
                binding.progress.isVisible = false
                binding.content.isVisible = false
                binding.errorBlock.isVisible = true
                binding.errorText.text = state.message
            }
        }
    }

    private fun showStats(stats: WeekStatsDto) {
        binding.averageText.text = getString(
            R.string.average_format,
            formatNumber(stats.averageCalories)
        )
        binding.maxDayText.text = stats.maxDay?.let { max ->
            getString(
                R.string.max_day_format,
                DateUtil.shortDisplay(max.date),
                formatNumber(max.calories)
            )
        } ?: getString(R.string.no_data)
        adapter.submitList(stats.days)
    }
}
