package com.habits.twinmind.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.habits.twinmind.recorder.AndroidAudioPlayer
import com.habits.twinmind.recorder.MyRecorder
import com.habits.twinmind.recorder.MyRecorder.Companion.audioFileCount
import com.habits.twinmind.ui.stateholder.RecordingStateHolder
import java.io.File

class MainViewModel(val application: Application) : ViewModel() {


    var intent  = Intent()


    fun startService() {
        intent = Intent(application, MyRecorder::class.java)
        if (Build.VERSION.SDK_INT >= 26) {
            application.startForegroundService(intent)
        } else {
//            application.startService(intent)
            return
        }
    }

    fun stopService()
    {
        Log.i("ServiceClass","stopService: ViewModel stop service method")
        application.stopService(intent)
    }

    fun playAudio()
    {
        val file = File(application.cacheDir,"TwinMindAudioFile${RecordingStateHolder.audioFileNumber}")
        val audioPlayer by lazy {
            AndroidAudioPlayer(application)
        }
        audioPlayer.start(file)

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