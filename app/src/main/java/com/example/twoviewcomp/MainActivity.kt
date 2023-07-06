package com.example.twoviewcomp

import MainViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode.Companion.Screen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()

        viewModel.init(applicationContext) // Перенесено сюда

        setContent {
            MyApp(viewModel)
        }
    }
}

@Composable
fun MyApp(viewModel: MainViewModel) {
    val currentScreen = viewModel.currentScreen.value

    when (currentScreen) {
        is Screen.First -> FirstView(viewModel)
        is Screen.Second -> SecondView(viewModel)
    }

    // Обработка перехода на второй экран
    val navigateToSecondScreen = viewModel.navigateToSecondScreen.value
    if (navigateToSecondScreen) {
        viewModel.navigateToSecondScreen.value = false // Сброс значения
        SecondView(viewModel)
    }
}

