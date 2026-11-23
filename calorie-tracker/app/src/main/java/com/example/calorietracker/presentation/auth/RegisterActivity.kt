package com.example.calorietracker.presentation.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.calorietracker.data.Graph
import com.example.calorietracker.data.remote.dto.UserDto
import com.example.calorietracker.databinding.ActivityRegisterBinding
import com.example.calorietracker.presentation.diary.MainActivity
import com.example.calorietracker.util.Resource


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        viewModelFactory { initializer { RegisterViewModel(Graph.repository) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.registerButton.setOnClickListener { submit() }
        binding.authLink.setOnClickListener { finish() }

        viewModel.state.observe(this) { state -> render(state) }
    }

    private fun submit() {
        val username = binding.usernameInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString().orEmpty()

        binding.usernameLayout.error = null
        binding.passwordLayout.error = null

        var valid = true
        if (username.length < 3) {
            binding.usernameLayout.error = "Логин должен содержать от 3 символов"
            valid = false
        }
        if (password.length < 6) {
            binding.passwordLayout.error = "Пароль должен содержать от 6 символов"
            valid = false
        }
        if (valid) viewModel.register(username, password)
    }

    private fun render(state: Resource<UserDto>?) {
        when (state) {
            is Resource.Loading -> {
                binding.progress.isVisible = true
                binding.registerButton.isEnabled = false
                binding.errorText.isVisible = false
            }
            is Resource.Success -> {
                binding.progress.isVisible = false
                openMain()
            }
            is Resource.Error -> {
                binding.progress.isVisible = false
                binding.registerButton.isEnabled = true
                binding.errorText.isVisible = true
                binding.errorText.text = state.message
            }
            null -> {
                binding.progress.isVisible = false
                binding.errorText.isVisible = false
            }
        }
    }

    private fun openMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
