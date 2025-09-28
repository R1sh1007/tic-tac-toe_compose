package com.rp.tictoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rp.tictoc.ui.theme.TicTocTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rp.tictoc.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeApp()
        }
    }
}

// Custom color scheme for modern 2025 look
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0066CC),
    secondary = Color(0xFF66BB6A),
    tertiary = Color(0xFF29B6F6)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF80CBC4),
    secondary = Color(0xFF81C784),
    tertiary = Color(0xFF4FC3F7)
)

@Composable
fun TicTacToeApp() {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        val viewModel: GameViewModel = viewModel()
        TicTacToeScreen(viewModel = viewModel)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TicTocTheme {
        TicTacToeApp()
    }
}