package com.habits.twinmind.recorder

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream


interface  AudioRecorderInterface{
    fun start(outputFile : File)
    fun stop()
    fun pause()
    fun resume()
}
class AudioRecorder(private val context: Context) :  AudioRecorderInterface{

    private var recorder : MediaRecorder? = null
    var isPaused = false

    private fun createRecorder() : MediaRecorder{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            MediaRecorder(context)
        }else{
            MediaRecorder()
        }
    }

    override fun start(outputFile: File) {
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
            isPaused = false
        }
//        recorder?.activeMicrophones?.forEach { it ->{
//            Log.i("AudioRecorder: after start", "AudioRecorder Config: ${it.address}")
//        }}
//        Log.i("AudioRecorder: Start", "AudioRecorder Config: ${recorder?.activeRecordingConfiguration.toString()}")
//        Log.i("AudioRecorder: Start", "AudioRecorder Config: ${recorder?.routedDevice.toString()}")



    }

    override fun resume() {
        //check if recorder is active, read MediaRecorder API docs
        //see how you can record to the same file
        try {
            if(isPaused)
            {
                recorder?.resume()
                isPaused = false
//                recorder?.activeMicrophones?.forEach { it ->{
//                    Log.i("AudioRecorder: after Resume", "AudioRecorder Config: ${it.address}")
//                }}
//                Log.i("AudioRecorder: after Resume", "AudioRecorder Config: ${recorder?.activeRecordingConfiguration.toString()}")
//                Log.i("AudioRecorder: after Resume", "AudioRecorder Config: ${recorder?.routedDevice.toString()}")
            }
        } catch (e: Exception) {
            Log.i("AudioRecorder", "Resuming error: ${e.message}")
        }
    }

    override fun pause() {
        recorder?.pause()
        isPaused = true
//        recorder?.activeMicrophones?.forEach { it ->{
//            Log.i("AudioRecorder: after Pause", "AudioRecorder Config: ${it.address}")
//        }}
//        Log.i("AudioRecorder: after Pause", "AudioRecorder Config: ${recorder?.activeRecordingConfiguration.toString()}")
//        Log.i("AudioRecorder: after Pause", "AudioRecorder Config: ${recorder?.routedDevice.toString()}")
    }


    override fun stop() {

        recorder?.stop()
        recorder?.reset()
        recorder = null
        isPaused = false
//        RecordingStateHolder.updateRecordingState(false)
    }
}

class AndroidAudioPlayer(private val context : Context)
{
    private var mediaPlayer : MediaPlayer? = null
    fun start(file : File, onCompletion: () -> Unit)
    {
        MediaPlayer.create(context,file.toUri()).apply{
            mediaPlayer = this
            start()
        }
        mediaPlayer?.setOnCompletionListener {
            onCompletion()
        }
    }

    fun stop()
    {
        mediaPlayer?.stop()
//        mediaPlayer?.reset()
//        mediaPlayer =null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun resetAndClearPlayer()
    {
        mediaPlayer?.reset()
        mediaPlayer = null
        mediaPlayer?.release()
    }
}