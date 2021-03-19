package com.example.hilt

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import com.example.hilt.db.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    fun insertUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }


    val userList: MutableState<List<User>> = mutableStateOf(listOf())

    init {
        viewModelScope.launch() {
            try {
                val result: List<User> = repository.getAllUsers()
                userList.value = result
            } catch (e: Exception) {
                Log.e("SSS", "${e.message.toString()}; ${e.stackTrace}")
            }
        }
    }


}