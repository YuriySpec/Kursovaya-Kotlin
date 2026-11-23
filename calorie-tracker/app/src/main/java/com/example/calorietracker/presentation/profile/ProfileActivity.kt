package com.example.calorietracker.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.calorietracker.R
import com.example.calorietracker.data.Graph
import com.example.calorietracker.data.remote.dto.UserDto
import com.example.calorietracker.databinding.ActivityProfileBinding
import com.example.calorietracker.presentation.auth.AuthActivity
import com.example.calorietracker.util.Resource


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels {
        viewModelFactory { initializer { ProfileViewModel(Graph.repository) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        // Переключатель тёмной темы: применяется немедленно и сохраняется
        binding.darkThemeSwitch.isChecked = Graph.session.darkTheme
        binding.darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            Graph.session.darkTheme = checked
            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.saveButton.setOnClickListener { submitGoal() }
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        viewModel.profile.observe(this) { state -> renderProfile(state) }
        viewModel.saveState.observe(this) { state -> renderSave(state) }
    }

    private fun submitGoal() {
        val goalText = binding.goalInput.text?.toString()?.trim().orEmpty()
        binding.goalLayout.error = null
        val goal = goalText.toIntOrNull()
        if (goal == null || goal < 800 || goal > 10000) {
            binding.goalLayout.error = getString(R.string.goal_error)
            return
        }
        viewModel.saveGoal(goal)
    }

    private fun renderProfile(state: Resource<UserDto>) {
        when (state) {
            is Resource.Loading -> {
                binding.progress.isVisible = true
                binding.content.isVisible = false
            }
            is Resource.Success -> {
                binding.progress.isVisible = false
                binding.content.isVisible = true
                binding.usernameText.text = state.data.username
                if (binding.goalInput.text.isNullOrBlank()) {
                    binding.goalInput.setText(state.data.dailyCalorieGoal.toString())
                }
            }
            is Resource.Error -> {
                binding.progress.isVisible = false
                binding.content.isVisible = false
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun renderSave(state: Resource<UserDto>?) {
        when (state) {
            is Resource.Loading -> binding.saveButton.isEnabled = false
            is Resource.Success -> {
                binding.saveButton.isEnabled = true
                Toast.makeText(this, R.string.goal_saved, Toast.LENGTH_SHORT).show()
            }
            is Resource.Error -> {
                binding.saveButton.isEnabled = true
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
            }
            null -> binding.saveButton.isEnabled = true
        }
    }
}
