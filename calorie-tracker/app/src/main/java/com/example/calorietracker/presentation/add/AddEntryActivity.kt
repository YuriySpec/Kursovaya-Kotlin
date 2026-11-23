package com.example.calorietracker.presentation.add

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.calorietracker.R
import com.example.calorietracker.data.Graph
import com.example.calorietracker.data.remote.dto.DayDiaryDto
import com.example.calorietracker.databinding.ActivityAddEntryBinding
import com.example.calorietracker.util.DateUtil
import com.example.calorietracker.util.Resource
import com.example.calorietracker.util.hideKeyboard


class AddEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEntryBinding
    private val viewModel: AddEntryViewModel by viewModels {
        viewModelFactory { initializer { AddEntryViewModel(Graph.repository) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.addButton.setOnClickListener { submit() }

        viewModel.state.observe(this) { state -> render(state) }
    }

    private fun submit() {
        binding.nameLayout.error = null
        binding.caloriesLayout.error = null

        val name = binding.nameInput.text?.toString()?.trim().orEmpty()
        val caloriesText = binding.caloriesInput.text?.toString()
            ?.replace(',', '.')?.trim().orEmpty()
        val calories = caloriesText.toDoubleOrNull()

        var valid = true
        if (name.isEmpty() || name.length > 100) {
            binding.nameLayout.error = getString(R.string.entry_name_error)
            valid = false
        }
        if (calories == null || calories < 0.0 || calories > 10000.0) {
            binding.caloriesLayout.error = getString(R.string.entry_calories_error)
            valid = false
        }
        if (valid) {
            hideKeyboard(this, binding.nameInput)
            viewModel.add(name, calories!!, DateUtil.today())
        }
    }

    private fun render(state: Resource<DayDiaryDto>?) {
        when (state) {
            is Resource.Loading -> {
                binding.progress.isVisible = true
                binding.addButton.isEnabled = false
            }
            is Resource.Success -> {
                binding.progress.isVisible = false
                Toast.makeText(this, R.string.entry_added, Toast.LENGTH_SHORT).show()
                finish()
            }
            is Resource.Error -> {
                binding.progress.isVisible = false
                binding.addButton.isEnabled = true
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
            }
            null -> {
                binding.progress.isVisible = false
                binding.addButton.isEnabled = true
            }
        }
    }
}
