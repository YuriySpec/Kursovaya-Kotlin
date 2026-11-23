package com.example.calorietracker.presentation.profile

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

class ProfileViewModel(private val repository: CalorieRepository) : ViewModel() {

    private val _profile = MutableLiveData<Resource<UserDto>>(Resource.Loading)
    val profile: LiveData<Resource<UserDto>> = _profile

    private val _saveState = MutableLiveData<Resource<UserDto>?>(null)
    val saveState: LiveData<Resource<UserDto>?> = _saveState

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _profile.value = Resource.Loading
            repository.profile().fold(
                onSuccess = { user -> _profile.value = Resource.Success(user) },
                onFailure = { error -> _profile.value = Resource.Error(error.userMessage()) }
            )
        }
    }

    fun saveGoal(goal: Int) {
        if (_saveState.value is Resource.Loading) return
        _saveState.value = Resource.Loading
        viewModelScope.launch {
            repository.updateGoal(goal).fold(
                onSuccess = { user -> _saveState.value = Resource.Success(user) },
                onFailure = { error -> _saveState.value = Resource.Error(error.userMessage()) }
            )
        }
    }

    fun logout() {
        Graph.session.clear()
    }
}
