package com.habits.twinmind.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habits.twinmind.ui.stateholder.RecordingStateHolder
import com.habits.twinmind.ui.viewmodel.MainViewModel

@Composable
fun Home(
    modifier: Modifier = Modifier,
    startRecording : () -> Unit,
    stopRecording : () -> Unit,
    startPlaying : () -> Unit
    )
{
    val isRecording by RecordingStateHolder.isRecording.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(top = 50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        )
        {
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { startRecording() }
            ){
                Text("Record")
            }
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { startPlaying() }
            ) {
                Text("Play")
            }
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { stopRecording() }
            ) {
                Text("Stop")
            }
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            text = if(isRecording)
            {
                "Recording in progress"
            }else{
                "Not recording"
            }
        )
    }


}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    Home(modifier = Modifier.fillMaxWidth(), startRecording = {}, stopRecording = {},{})
}
