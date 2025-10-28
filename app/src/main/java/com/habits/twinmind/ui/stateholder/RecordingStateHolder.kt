package com.habits.twinmind.ui.stateholder

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object RecordingStateHolder{
    private val _isRecording = MutableStateFlow(false)
    var isRecording : StateFlow<Boolean> = _isRecording.asStateFlow()

    var audioFileNumber = -1
    fun updateRecordingState(newState : Boolean)
    {
        _isRecording.update {
            newState
        }
    }

}