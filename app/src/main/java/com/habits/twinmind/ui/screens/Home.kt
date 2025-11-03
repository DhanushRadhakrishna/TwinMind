package com.habits.twinmind.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.habits.twinmind.ui.stateholder.RecordingStateHolder
import kotlinx.coroutines.delay

@Composable
fun Home(
    modifier: Modifier = Modifier,
    startRecording : () -> Unit,
    stopRecording : () -> Unit,
    startPlaying : () -> Unit,
    stopPlaying : () -> Unit,
    elapsedTime : Long,
    pauseRecording: () -> Unit,
    resumeRecording: () -> Unit,
    isPlaying : Boolean,
    isRecording : RecordingStateHolder.RecordingStates
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Timer display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = if(isRecording == RecordingStateHolder.RecordingStates.RECORDING) "Recording.." else "",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = String.format("%02d:%02d", elapsedTime / 60, elapsedTime % 60),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            if (isRecording == RecordingStateHolder.RecordingStates.RECORDING) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription =  "Pause Recording",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable {
                                pauseRecording()
                            }
                )
            }
            if(isRecording!= RecordingStateHolder.RecordingStates.RECORDING){
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription =  "Resume Recording",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable {
                            resumeRecording()
                        }

                )
            }

        }

        // Buttons row aligned at bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier.padding(start = 18.dp,end = 4.dp),
                onClick = {
                    if (isRecording == RecordingStateHolder.RecordingStates.RECORDING) {
                        stopRecording()

                    } else {
                        startRecording()
                    }
                }
            ) {
                if (isRecording == RecordingStateHolder.RecordingStates.RECORDING) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Stop Recording",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Stop Recording")
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Recording",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Start Recording")
                }
            }
            Button(
                modifier = Modifier.padding(end =18.dp),
//                enabled = isRecording == RecordingStateHolder.RecordingStates.RECORDING,
                onClick = {
                    if(!isPlaying)
                    {
                        startPlaying()
                    }else{
                        stopPlaying()
                    }
                }
            ) {
                if (isPlaying) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically)
                    {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Stop Playing",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Stop Playing")
                    }

                } else {
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                        )
                    {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Recording",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Play Recording")
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    Home(
        modifier = Modifier.fillMaxSize(),
        {},
        {},
        {},
        {},
        resumeRecording = {},
        pauseRecording = {},
        elapsedTime = 0,
        isPlaying = false,
        isRecording = RecordingStateHolder.RecordingStates.RECORDING,
        )

}
