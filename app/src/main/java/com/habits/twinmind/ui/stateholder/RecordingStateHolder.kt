package com.habits.twinmind.ui.stateholder

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object RecordingStateHolder{

    enum class MicFocus{
        TwinMind,
        OTHER

    }
    private val _isRecording = MutableStateFlow(false)
    val isRecording : StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _onCall = MutableStateFlow(false)
    val onCall : StateFlow<Boolean> = _onCall.asStateFlow()

    private val _audioFocus = MutableStateFlow<MicFocus>(MicFocus.TwinMind)
    val audioFocus : StateFlow<MicFocus> = _audioFocus.asStateFlow()

    var audioFileNumber = -1
    fun updateRecordingState(newState : Boolean)
    {
        _isRecording.update {
            newState
        }
    }

    fun updateOnCallStatus(newState : Boolean)
    {
        _onCall.update {
            newState
        }
    }

    fun updateAudioFocus(newState : MicFocus)
    {
        _audioFocus.update {
            newState
        }
    }

}

