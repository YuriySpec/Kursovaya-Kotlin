package com.example.calorietracker.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorietracker.data.CalorieRepository
import com.example.calorietracker.data.Graph
import com.example.calorietracker.data.remote.dto.UserDto
import com.example.calorietracker.data.remote.userMessage
import com.example.calorietracker.util.Resource
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: CalorieRepository) : ViewModel() {

    private val _state = MutableLiveData<Resource<UserDto>?>(null)
    val state: LiveData<Resource<UserDto>?> = _state

    fun register(username: String, password: String) {
        if (_state.value is Resource.Loading) return
        _state.value = Resource.Loading
        viewModelScope.launch {
            repository.register(username.trim(), password).fold(
                onSuccess = {
                    // После создания учётной записи выполняем вход для проверки данных
                    Graph.session.save(username.trim(), password)
                    repository.login().fold(
                        onSuccess = { user -> _state.value = Resource.Success(user) },
                        onFailure = { error ->
                            Graph.session.clear()
                            _state.value = Resource.Error(error.userMessage())
                        }
                    )
                },
                onFailure = { error -> _state.value = Resource.Error(error.userMessage()) }
            )
        }
    }

    fun reset() {
        _state.value = null
    }
}
