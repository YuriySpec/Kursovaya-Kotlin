package com.example.calorietracker.presentation.auth

import com.example.calorietracker.data.remote.dto.UserDto
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.calorietracker.data.Graph
import com.example.calorietracker.databinding.ActivityAuthBinding
import com.example.calorietracker.presentation.diary.MainActivity
import com.example.calorietracker.util.Resource
import kotlinx.coroutines.launch


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels {
        viewModelFactory { initializer { AuthViewModel(Graph.repository) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { submit() }
        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.state.observe(this) { state -> render(state) }
    }

    private fun submit() {
        val username = binding.usernameInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString().orEmpty()

        binding.usernameLayout.error = null
        binding.passwordLayout.error = null

        var valid = true
        if (username.isEmpty()) {
            binding.usernameLayout.error = "Введите логин"
            valid = false
        }
        if (password.isEmpty()) {
            binding.passwordLayout.error = "Введите пароль"
            valid = false
        }
        if (valid) viewModel.login(username, password)
    }

    private fun render(state: Resource<UserDto>?) {
        when (state) {
            is Resource.Loading -> {
                binding.progress.isVisible = true
                binding.loginButton.isEnabled = false
                binding.errorText.isVisible = false
            }
            is Resource.Success -> {
                binding.progress.isVisible = false
                openMain()
            }
            is Resource.Error -> {
                binding.progress.isVisible = false
                binding.loginButton.isEnabled = true
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
