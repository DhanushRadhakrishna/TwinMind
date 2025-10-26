package com.habits.twinmind.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habits.twinmind.ui.viewmodel.MainViewModel

@Composable
fun Home(
    modifier: Modifier = Modifier,
    startRecording : () -> Unit,
    )
{
    Row(
        modifier = Modifier.padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Button(

            onClick = startRecording
        ){
            Text("Record")
        }
    }

}


