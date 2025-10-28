package com.habits.twinmind.recorder

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.core.net.toUri
import com.habits.twinmind.ui.stateholder.RecordingStateHolder
import java.io.File
import java.io.FileOutputStream


interface  AudioRecorderInterface{
    fun start(outputFile : File)
    fun stop()
}
class AudioRecorder(private val context: Context) :  AudioRecorderInterface{

    private var recorder : MediaRecorder? = null

    private fun createRecorder() : MediaRecorder{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            MediaRecorder(context)
        }else{
            MediaRecorder()
        }
    }

    override fun start(outputFile: File) {
        RecordingStateHolder.updateRecordingState(true)
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000) // 128 kbps
            setAudioSamplingRate(44100)
            setOutputFile(FileOutputStream(outputFile).fd)
            prepare()
            start()

            recorder = this
        }


    }

    override fun stop() {

        recorder?.stop()
        recorder?.reset()
        recorder = null
        RecordingStateHolder.updateRecordingState(false)
    }
}

class AndroidAudioPlayer(private val context : Context)
{
    private var mediaPlayer : MediaPlayer? = null
    fun start(file : File)
    {
        MediaPlayer.create(context,file.toUri()).apply{
            mediaPlayer = this
            start()
        }
    }

    fun stop()
    {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer =null
    }
}