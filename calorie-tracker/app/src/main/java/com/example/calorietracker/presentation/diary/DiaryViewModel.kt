package com.example.calorietracker.presentation.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorietracker.data.CalorieRepository
import com.example.calorietracker.data.remote.dto.DayDiaryDto
import com.example.calorietracker.data.remote.userMessage
import com.example.calorietracker.util.DateUtil
import com.example.calorietracker.util.Resource
import kotlinx.coroutines.launch


class DiaryViewModel(private val repository: CalorieRepository) : ViewModel() {

    private val _date = MutableLiveData(DateUtil.today())
    val date: LiveData<String> = _date

    private val _state = MutableLiveData<Resource<DayDiaryDto>>(Resource.Loading)
    val state: LiveData<Resource<DayDiaryDto>> = _state

    /** Результат изменения дневной нормы (для всплывающего сообщения). */
    private val _goalResult = MutableLiveData<Resource<Unit>?>(null)
    val goalResult: LiveData<Resource<Unit>?> = _goalResult

    /** Результат редактирования записи (для всплывающего сообщения). */
    private val _entryResult = MutableLiveData<Resource<Unit>?>(null)
    val entryResult: LiveData<Resource<Unit>?> = _entryResult

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = Resource.Loading
            val date = _date.value ?: DateUtil.today()
            repository.day(date).fold(
                onSuccess = { diary -> _state.value = Resource.Success(diary) },
                onFailure = { error -> _state.value = Resource.Error(error.userMessage()) }
            )
        }
    }

    fun previousDay() {
        _date.value = DateUtil.shift(_date.value ?: DateUtil.today(), -1)
        load()
    }

    fun nextDay() {
        _date.value = DateUtil.shift(_date.value ?: DateUtil.today(), 1)
        load()
    }

    /** Изменение дневной нормы калорий; после успеха дневник перезагружается. */
    fun updateGoal(goal: Int) {
        if (_goalResult.value is Resource.Loading) return
        _goalResult.value = Resource.Loading
        viewModelScope.launch {
            repository.updateGoal(goal).fold(
                onSuccess = {
                    _goalResult.value = Resource.Success(Unit)
                    load()
                },
                onFailure = { error -> _goalResult.value = Resource.Error(error.userMessage()) }
            )
        }
    }

    fun resetGoalResult() {
        _goalResult.value = null
    }

    /** Редактирование записи: исправление названия и калорийности. */
    fun updateEntry(id: Int, foodName: String, calories: Double) {
        if (_entryResult.value is Resource.Loading) return
        _entryResult.value = Resource.Loading
        viewModelScope.launch {
            repository.updateEntry(id, foodName, calories).fold(
                onSuccess = {
                    _entryResult.value = Resource.Success(Unit)
                    load()
                },
                onFailure = { error -> _entryResult.value = Resource.Error(error.userMessage()) }
            )
        }
    }

    fun resetEntryResult() {
        _entryResult.value = null
    }

    fun deleteEntry(id: Int) {
        viewModelScope.launch {
            repository.deleteEntry(id).fold(
                onSuccess = { load() },
                onFailure = { error -> _state.value = Resource.Error(error.userMessage()) }
            )
        }
    }
}
