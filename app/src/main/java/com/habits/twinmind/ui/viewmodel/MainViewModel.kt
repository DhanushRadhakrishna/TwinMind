package com.habits.twinmind.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habits.twinmind.recorder.AndroidAudioPlayer
import com.habits.twinmind.recorder.MyRecorder
import com.habits.twinmind.recorder.PhoneStateChangeListener
import com.habits.twinmind.ui.stateholder.RecordingStateHolder
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(val application: Application) : ViewModel() {

    var intent  = Intent()
    var audioPlayer : AndroidAudioPlayer? = null

    private val _timer = MutableStateFlow(0L)
    val timer : StateFlow<Long> = _timer.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying : StateFlow<Boolean> = _isPlaying.asStateFlow()

    var elapsedTime = 0L

    init {
        intent = Intent(application, MyRecorder::class.java)
    }

    fun startService() {
        viewModelScope.launch{
            while(true)
            {
                delay(1000L)
                _timer.value = elapsedTime
                elapsedTime+=1L
            }
        }

        if (Build.VERSION.SDK_INT >= 26) {
            application.startForegroundService(intent)
        } else {
            application.startService(intent)
            return
        }
    }

    fun stopService()
    {
        viewModelScope.cancel()
        Log.i("MainViewModel","stopService: ViewModel stop service method")
        RecordingStateHolder.updateRecordingState(RecordingStateHolder.RecordingStates.STOPPED)
        application.stopService(intent)
    }

    fun pauseRecording()
    {
        RecordingStateHolder.updateRecordingState(RecordingStateHolder.RecordingStates.PAUSED)
    }

    fun resumeRecording()
    {
        RecordingStateHolder.updateRecordingState(RecordingStateHolder.RecordingStates.RESUMED)
    }

    fun playAudio()
    {
        _isPlaying.value = true
        val file = File(application.cacheDir,"TwinMindAudioFile${RecordingStateHolder.audioFileNumber}")
        Log.i("MainViewModel","Audio file: ${file.name} + ${file.path}")
        audioPlayer = AndroidAudioPlayer(application)
//        val audioPlayer by lazy {
//            AndroidAudioPlayer(application)
//        }
        audioPlayer!!.start(file) { onFinishedPlayingAudio() }
    }

    fun onFinishedPlayingAudio()
    {
        _isPlaying.value = false
    }

    fun stopPlayingAudio()
    {
        _isPlaying.value = false
        if(audioPlayer!= null && audioPlayer?.isPlaying() == true)
        {
            audioPlayer?.stop()
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