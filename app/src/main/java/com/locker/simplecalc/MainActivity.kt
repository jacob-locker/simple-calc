package com.locker.simplecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.locker.simplecalc.ui.screens.CalculatorScreen
import com.locker.simplecalc.ui.theme.SimpleCalcTheme

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleCalcTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = '\u2A2F'.toString())
}

@ExperimentalMaterialApi
@Preview(showBackground = true, widthDp = 389, heightDp = 822)
@Composable
fun AppPreview_Pixel() {
    SimpleCalcTheme {
        CalculatorScreen()
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true, widthDp = 389, heightDp = 822)
@Composable
fun AppPreview_Pixel_Dark() {
    SimpleCalcTheme(darkTheme = true) {
        CalculatorScreen()
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true, widthDp = 411, heightDp = 731)
@Composable
fun AppPreview_Pixel_XL() {
    SimpleCalcTheme {
        CalculatorScreen()
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true, widthDp = 320, heightDp = 569)
@Composable
fun AppPreview_Android_One() {
    SimpleCalcTheme {
        CalculatorScreen()
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true, widthDp = 822, heightDp = 389)
@Composable
fun AppPreview_Pixel_Landscape() {
    SimpleCalcTheme {
        CalculatorScreen()
    }
}