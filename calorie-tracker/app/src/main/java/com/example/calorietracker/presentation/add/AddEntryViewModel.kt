package com.example.calorietracker.presentation.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorietracker.data.CalorieRepository
import com.example.calorietracker.data.remote.dto.DayDiaryDto
import com.example.calorietracker.data.remote.userMessage
import com.example.calorietracker.util.Resource
import kotlinx.coroutines.launch

class AddEntryViewModel(private val repository: CalorieRepository) : ViewModel() {

    private val _state = MutableLiveData<Resource<DayDiaryDto>?>(null)
    val state: LiveData<Resource<DayDiaryDto>?> = _state

    fun add(foodName: String, calories: Double, date: String) {
        if (_state.value is Resource.Loading) return
        _state.value = Resource.Loading
        viewModelScope.launch {
            repository.addEntry(foodName, calories, date).fold(
                onSuccess = { diary -> _state.value = Resource.Success(diary) },
                onFailure = { error -> _state.value = Resource.Error(error.userMessage()) }
            )
        }
    }

    fun reset() {
        _state.value = null
    }
}
