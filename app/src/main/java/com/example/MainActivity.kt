package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.BrowserRepository
import com.example.ui.BrowserScreen
import com.example.ui.BrowserViewModel
import com.example.ui.BrowserViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Instantiate Room Database and Repository
    val database = AppDatabase.getDatabase(applicationContext)
    val repository = BrowserRepository(database.browserDao())
    val viewModelFactory = BrowserViewModelFactory(application, repository)

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          val browserViewModel: BrowserViewModel = viewModel(factory = viewModelFactory)
          BrowserScreen(viewModel = browserViewModel)
        }
      }
    }
  }
}
