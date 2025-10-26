package com.habits.twinmind.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.habits.twinmind.recorder.MyRecorder

class MainViewModel(val application: Application) : ViewModel() {



    fun startService() {
        val intent = Intent(application, MyRecorder::class.java)
        if (Build.VERSION.SDK_INT >= 26) {
            application.startForegroundService(intent)
        } else {
            application.startService(intent)
        }
    }


}
class MyViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}