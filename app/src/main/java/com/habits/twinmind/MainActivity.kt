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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.habits.twinmind.ui.screens.Home
import com.habits.twinmind.ui.theme.TwinMindTheme
import com.habits.twinmind.ui.viewmodel.MainViewModel
import com.habits.twinmind.ui.viewmodel.MyViewModelFactory
import java.security.Permission

class MainActivity : ComponentActivity() {

    var statusText : String = ""
    private val viewModel : MainViewModel by viewModels(factoryProducer = { MyViewModelFactory(application) })
    private val PERMISSION_MICROPHONE_CODE = 1
    private val PERMISSION_NOTIFICATION_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TwinMindTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Home(
                        modifier = Modifier.padding(innerPadding).fillMaxWidth(),
                        startRecording = { startRecording() },
                        stopRecording =  { viewModel.stopService() },
                        startPlaying = { viewModel.playAudio() }
                    )
                }
            }
        }
    }



    private fun startRecording()
    {
        Log.i("ServiceClass", "ButtonClicked")
        val permissionsNeeded = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.RECORD_AUDIO),PERMISSION_MICROPHONE_CODE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.POST_NOTIFICATIONS),PERMISSION_NOTIFICATION_CODE)
            }
        }
        viewModel.startService()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_MICROPHONE_CODE ->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    return
                }
                else{
                    Toast.makeText(this,"Need Microphone permission to record audio", Toast.LENGTH_LONG).show()
                }
            }
            PERMISSION_NOTIFICATION_CODE ->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    return
                }
                else{
                    Toast.makeText(this,"Need notification permission to record audio", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    TwinMindTheme {
//        Greeting("Android")
//    }
//}