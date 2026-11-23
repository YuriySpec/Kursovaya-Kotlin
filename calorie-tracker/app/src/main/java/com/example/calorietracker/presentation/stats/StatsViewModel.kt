package com.example.calorietracker.presentation.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorietracker.data.CalorieRepository
import com.example.calorietracker.data.remote.dto.WeekStatsDto
import com.example.calorietracker.data.remote.userMessage
import com.example.calorietracker.util.DateUtil
import com.example.calorietracker.util.Resource
import kotlinx.coroutines.launch

class StatsViewModel(private val repository: CalorieRepository) : ViewModel() {

    private val _state = MutableLiveData<Resource<WeekStatsDto>>(Resource.Loading)
    val state: LiveData<Resource<WeekStatsDto>> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = Resource.Loading
            repository.week(DateUtil.mondayOfCurrentWeek()).fold(
                onSuccess = { stats -> _state.value = Resource.Success(stats) },
                onFailure = { error -> _state.value = Resource.Error(error.userMessage()) }
            )
        }
    }
}
