package com.habits.twinmind

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habits.twinmind.ui.screens.Home
import com.habits.twinmind.ui.stateholder.RecordingStateHolder
import com.habits.twinmind.ui.theme.TwinMindTheme
import com.habits.twinmind.ui.viewmodel.MainViewModel
import com.habits.twinmind.ui.viewmodel.MyViewModelFactory
import java.security.Permission

class MainActivity : ComponentActivity() {

    private val viewModel : MainViewModel by viewModels(factoryProducer = { MyViewModelFactory(application) })
    private val PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val isRecording by RecordingStateHolder.isRecording.collectAsStateWithLifecycle()
            val elapsedTime by viewModel.timer.collectAsStateWithLifecycle()
            val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
            TwinMindTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Home(
                        modifier = Modifier.padding(innerPadding).fillMaxWidth(),
                        startRecording = { startRecording() },
                        stopRecording =  { viewModel.stopService() },
                        startPlaying = { viewModel.playAudio() },
                        stopPlaying = { viewModel.stopPlayingAudio() },
                        elapsedTime= elapsedTime,
                        isPlaying= isPlaying,
                        isRecording = isRecording,
                        pauseRecording = { viewModel.pauseRecording() },
                        resumeRecording = { viewModel.resumeRecording() }
                    )
                }
            }
            LaunchedEffect(RecordingStateHolder.isRecording) {
                Log.i("ComposeUI", "Recomposition: isRecording = $RecordingStateHolder.isRecording")
            }
        }

    }

    private fun askMicrophonePermission()
    {
        Log.i("ServiceClass", "Microphone permission: askMic")
        ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.RECORD_AUDIO),PERMISSION_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askNotificationPermission()
    {
        Log.i("ServiceClass", "Microphone permission: askNotification")
        ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.POST_NOTIFICATIONS),PERMISSION_REQUEST_CODE)
    }
    private fun startRecording()
    {
        Log.i("ServiceClass", "ButtonClicked")
        val permissionsNeeded = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            permissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
            return
        }
        viewModel.startService()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = mutableListOf<String>()
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    permissions[i]?.let { deniedPermissions.add(it) }
                }
            }
            if (deniedPermissions.isEmpty()) {
                viewModel.startService()
            } else {
                for (permission in deniedPermissions) {
                    when (permission) {
                        Manifest.permission.RECORD_AUDIO -> Toast.makeText(this, "Need Microphone permission to record audio", Toast.LENGTH_LONG).show()
                        Manifest.permission.POST_NOTIFICATIONS -> Toast.makeText(this, "Need notification permission to record audio", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }



    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopService()
    }



}

