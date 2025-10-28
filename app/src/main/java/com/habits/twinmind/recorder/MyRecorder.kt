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
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.habits.twinmind.R
import com.habits.twinmind.ui.stateholder.RecordingStateHolder
import java.io.File

class MyRecorder() : Service() {


    private var notification : Notification? = null
    private var myAudioRecorder : AudioRecorder? = null

    val channelId = "101"
    val notificationId = 101

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    companion object {
        var audioFileCount = -1
    }
    private var audioFile : File? = null


    override fun onCreate() {
        super.onCreate()
        Log.i("ServiceClass","OnCreate")
        myAudioRecorder = AudioRecorder(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("ServiceClass","OnStartCommand")
        //prepare audio file
        audioFile = File(application.cacheDir,"TwinMindAudioFile${++RecordingStateHolder.audioFileNumber}")
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

        myAudioRecorder?.start(audioFile!!)
        showNotification()
        return super.onStartCommand(intent, flags, startId)
    }
    fun buildNotification() : Notification
    {
        var channel : NotificationChannel? = null
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            channel = buildNotificationChannel()
        }
        val notification = NotificationCompat.Builder(this,channelId)
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
        val importance = NotificationManager.IMPORTANCE_HIGH
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


    fun startRecording()
    {


    }
    fun stopRecording()
    {
        myAudioRecorder?.stop()
    }

    override fun onDestroy() {
        Log.i("ServiceClass","OnDestroy: Service Destroyed")
        myAudioRecorder?.stop()
        RecordingStateHolder.updateRecordingState(false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


}