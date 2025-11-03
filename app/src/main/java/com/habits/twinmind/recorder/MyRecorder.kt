package com.habits.twinmind.recorder

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habits.twinmind.R
import com.habits.twinmind.ui.stateholder.RecordingStateHolder
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.security.Permission

class MyRecorder() : Service() {


    private var notification : Notification? = null
    private var myAudioRecorder : AudioRecorder? = null


//    private var wavRecorder : WAVRecorder? = null

    val channelId = "101"
    val notificationId = 101

    private val serviceJob = SupervisorJob()
    private var callListenerJob : Job? = null
    private var recordingStateListenerJob : Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    var phoneStateListener : PhoneStateChangeListener? =null

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private var audioFile : File? = null

    override fun onCreate() {
        super.onCreate()
        Log.i("ServiceClass","OnCreate")
        myAudioRecorder = AudioRecorder(this)
        phoneStateListener = PhoneStateChangeListener(application)
        phoneStateListener?.registerCallStateListener()
        //prepare audio file
        audioFile = File(application.cacheDir,"TwinMindAudioFile${++RecordingStateHolder.audioFileNumber}")

        //listen to the recording state holder
//        callListenerJob = serviceScope.launch {
//            RecordingStateHolder.onCall.collect { newState ->
//                when(newState) {
//                    true ->{
//                        pauseRecording()
//                        Log.i("ServiceClass","onCallState = $newState")
//                    }
//
//                    false ->{
//                        resumeRecording()
//                        Log.i("ServiceClass","onCallState = $newState")
//                    }
//                }
//            }
//        }
        recordingStateListenerJob = serviceScope.launch {
            RecordingStateHolder.isRecording.collect { newState ->
                when(newState){
                    RecordingStateHolder.RecordingStates.STOPPED -> {
                        stopRecording()
                    }
                    RecordingStateHolder.RecordingStates.PAUSED -> {
                        pauseRecording()
                    }
                    RecordingStateHolder.RecordingStates.RESUMED -> {
                        resumeRecording()
                    }
                    else -> {
                        Log.i("ServiceClass","Invalid RecordingState OR Recording")
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("ServiceClass","OnStartCommand")

        Log.i("ServiceClass","OnStartCommand: AudioFile path ${audioFile?.path} AudioFile name: ${audioFile?.name}")
        //prepare notification
        notification = buildNotification()
        try{
            ServiceCompat.startForeground(
                this,
                notificationId,
                notification!!,
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                {
                    Log.i("ServiceClass","OnStartCommand: inside startForeground")
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                }else{
                    0
                }
            )

        }catch(e : Exception)
        {
            Log.i("ServiceClass","OnStartCommand: Exception $e")
        }
//        if(ContextCompat.checkSelfPermission(this@MyRecorder, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
//        {
//            startWAVRecording()
//        }

        showNotification()
        startRecording()
        return super.onStartCommand(intent, flags, startId)
    }
    fun startRecording()
    {
        RecordingStateHolder.updateRecordingState(RecordingStateHolder.RecordingStates.RECORDING)
        try {
            serviceScope.launch {
                myAudioRecorder?.start(audioFile!!)
            }
        } catch (e: Exception) {
            Log.i("ServiceClass","StartRecording: Exception ${e.message}")
            RecordingStateHolder.updateRecordingState(RecordingStateHolder.RecordingStates.STOPPED)
        }

    }


    fun buildNotification() : Notification
    {
        var channel : NotificationChannel? = null
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            channel = buildNotificationChannel()
        }
        val notification = NotificationCompat.Builder(this,channelId)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("TwinMind")
            .setContentText("TwinMind is recording")
        Log.i("ServiceClass","OnStartCommand: Notification Built")
        return notification.build()
    }
    fun showNotification()
    {
        Log.i("ServiceClass","showNotification: ")
        NotificationManagerCompat.from(this).apply {
            if (ActivityCompat.checkSelfPermission(
                    this@MyRecorder,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId,notification!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun buildNotificationChannel() : NotificationChannel
    {
        val notificationChannel : NotificationChannel

        val name = "Recording Service."
        val descriptionText = "TwinMind to display notification when it is recording."
        val importance = NotificationManager.IMPORTANCE_LOW
        notificationChannel = NotificationChannel(channelId,name,importance).apply {
            description = descriptionText
        }
        val notificationManager : NotificationManager = getSystemService(
                                            Context.NOTIFICATION_SERVICE
                                        ) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        Log.i("ServiceClass","OnStartCommand: Notification channel is created")
        return notificationChannel
    }


    fun pauseRecording()
    {
//        RecordingStateHolder.updateRecordingState(false)
        Log.i("ServiceClass","Pausing recording")
        myAudioRecorder?.pause()
    }
    fun stopRecording()
    {
//        RecordingStateHolder.updateRecordingState(false)
        Log.i("ServiceClass","Stopping recording")
        myAudioRecorder?.stop()
    }

    fun resumeRecording()
    {
//        RecordingStateHolder.updateRecordingState(true)
        if(myAudioRecorder?.isPaused==true)
        {
            myAudioRecorder?.resume()
            Log.i("ServiceClass","Resuming recording")
        }
        RecordingStateHolder.updateRecordingState(RecordingStateHolder.RecordingStates.RECORDING)

//        RecordingStateHolder.updateRecordingState(RecordingStateHolder.RecordingStates.RESUMED)
    }

    override fun onDestroy() {
        Log.i("ServiceClass","OnDestroy: Service Destroyed")
//        phoneStateListener?.unregisterCallStateListener()
//        stopRecording()
//        stopWAVRecording()
//        callListenerJob?.cancel()
        serviceScope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


}

//@RequiresPermission(Manifest.permission.RECORD_AUDIO)
//    fun startWAVRecording()
//    {
//        RecordingStateHolder.updateRecordingState(true)
//        try {
//            serviceScope.launch{
//
//                wavRecorder = WAVRecorder(application.cacheDir.path)
//                wavRecorder?.startRecording()
//
//            }
//        }catch (e : Exception)
//        {
//            Log.i("ServiceClass","OnStartCommand: Exception ${e.message}")
//            RecordingStateHolder.updateRecordingState(false)
//        }
//
//    }
//fun stopWAVRecording()
//    {
//        RecordingStateHolder.updateRecordingState(false)
//        wavRecorder?.stopRecording()
//        serviceJob.cancel()
//
//    }
//fun playWAVFile()
//{
//    val wavFile = File(application.cacheDir, "TwinMind.wav")
//    Log.i("MainViewModel","wavFile: ${wavFile.path}")
//    if (wavFile.exists()) {
//        val mediaPlayer = MediaPlayer.create(application, Uri.fromFile(wavFile))
//        mediaPlayer?.apply {
//            setOnCompletionListener {
//                it.release() // free resources after playback
//            }
//            start()
//        } ?: run {
//            Log.e("Player", "Unable to create MediaPlayer for file: ${wavFile.path}")
//        }
//    }
//}