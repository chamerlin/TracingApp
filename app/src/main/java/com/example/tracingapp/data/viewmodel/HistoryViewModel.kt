package com.example.tracingapp.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.example.tracingapp.data.model.History
import com.example.tracingapp.data.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class HistoryViewModel(private val repository: HistoryRepository): ViewModel(), Observable {
    fun checkInUser(history: History) {
        viewModelScope.launch(Dispatchers.IO) { repository.checkInUser(history) }
    }

    fun ownHistories(userId: Int): LiveData<List<History>> {
        return repository.getOwnHistory(userId).asLiveData()
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}
}

class HistoryViewModelFactory(private val repository: HistoryRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel not found!")
    }
}