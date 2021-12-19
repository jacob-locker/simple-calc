package com.locker.simplecalc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.locker.simplecalc.model.OperatorType
import com.locker.simplecalc.model.ParseException
import com.locker.simplecalc.model.parseExpression

@ExperimentalMaterialApi
@Composable
fun CalculatorScreen() {
    Column(modifier = Modifier.fillMaxHeight()) {
        val inputState = remember { mutableStateOf("") }
        val answerState = remember { mutableStateOf("") }

        Column(modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom) {
            InputDisplay(inputState)
            RunningAnswerDisplay(answerState)
        }

        Column(modifier = Modifier.weight(2f),
            verticalArrangement = Arrangement.Bottom) {
            ButtonGrid(inputState, answerState)
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun InputDisplay(inputState: MutableState<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp), horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = inputState.value,
            textAlign = TextAlign.End,
            maxLines = 1,
            style = MaterialTheme.typography.h4
        )
    }
}

@Composable
fun RunningAnswerDisplay(answerState: MutableState<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp, bottom = 8.dp), horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = answerState.value,
            textAlign = TextAlign.End,
            maxLines = 1,
            style = MaterialTheme.typography.h6
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun ButtonGrid(inputState: MutableState<String>, answerState: MutableState<String>) {
    CalcColumn {
        row { CalcRow {
            item {
                CalcButton(buttonValue = "AC", inputState = inputState, answerState = answerState,
                    onClick = {
                        inputState.value = ""
                        answerState.value = ""
                    })
            }
            item {
                CalcButton(
                    buttonValue = "(",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = ")",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = "${OperatorType.DIVISION.symbol}",
                    inputState = inputState,
                    answerState = answerState
                )
            }
        } }
        row { CalcRow {
            item {
                CalcButton(
                    buttonValue = "7",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = "8",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = "9",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = "${OperatorType.PRODUCT.symbol}",
                    inputState = inputState,
                    answerState = answerState
                )
            }
        } }
        row { CalcRow {
            item {
                CalcButton(
                    buttonValue = "4",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = "5",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = "6",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = "${OperatorType.SUBTRACT.symbol}",
                    inputState = inputState,
                    answerState = answerState
                )
            }
        } }
        row { CalcRow {
            item {
                CalcButton(
                    buttonValue = "1",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = "2",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = "3",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = "${OperatorType.ADD.symbol}",
                    inputState = inputState,
                    answerState = answerState
                )
            }
        }  }
        row { CalcRow {
            item {
                CalcButton(
                    buttonValue = "0",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = ".",
                    inputState = inputState,
                    answerState = answerState
                )
            }
            item {
                CalcButton(
                    buttonValue = "",
                    inputState = inputState,
                    answerState = answerState,
                    onClick = {
                        inputState.value =
                            if (inputState.value.isNotEmpty()) inputState.value.dropLast(1) else ""
                        try {
                            answerState.value = inputState.value.parseExpression().value.toString()
                        } catch (e: ParseException) {
                            answerState.value = ""
                        }
                    }) {
                        Icon(imageVector = Icons.Outlined.Backspace, contentDescription = "Back")
                }
            }
            item {
                CalcButton(
                    buttonValue = "=",
                    inputState = inputState,
                    answerState = answerState,
                    onClick = {
                        try {
                            inputState.value = inputState.value.parseExpression().value.toString()
                            answerState.value = ""
                        } catch (e: ParseException) {
                            answerState.value = "${e.message}"
                        }
                    })
            }
        } }
    }
}

class CalcColScope {
    val rows
        get() = _rows.toList()
    private val _rows = mutableListOf<@Composable () -> Unit>()

    fun row(content: @Composable () -> Unit) {
        _rows.add(content)
    }
}

@Composable
fun CalcColumn(content: CalcColScope.() -> Unit) {
    val colScope = CalcColScope()
    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        content(colScope)
        val rowHeight = 1F / colScope.rows.size
        colScope.rows.forEachIndexed { index, rowContent ->
            Surface(modifier = Modifier.fillMaxHeight(rowHeight / (1 - index * rowHeight))) {
                rowContent()
            }
        }
    }
}

class CalcRowScope {
    val items
        get() = _items.toList()
    private val _items = mutableListOf<@Composable () -> Unit>()

    fun item(content: @Composable () -> Unit) {
        _items.add(content)
    }
}

@Composable
fun CalcRow(content: CalcRowScope.() -> Unit) {
    val rowScope = CalcRowScope()
    Row(
        modifier = Modifier.padding(start = 6.dp, end = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        content(rowScope)
        val itemWidth = 1F / rowScope.items.size
        rowScope.items.forEachIndexed { idx, item ->
            Surface(modifier = Modifier.fillMaxWidth(itemWidth / (1 - idx * itemWidth))) {
                item()
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun CalcButton(
    buttonValue: String,
    inputState: MutableState<String>,
    answerState: MutableState<String>,
    onClick: () -> Unit = {
        inputState.value += buttonValue
        try {
            answerState.value = inputState.value.parseExpression().value.toString()
        } catch (e: ParseException) {
            answerState.value = ""
        }
    },
    content: @Composable () -> Unit = {
        Text(text = buttonValue, style = MaterialTheme.typography.h6)
    }
) {
    var height by remember { mutableStateOf<Dp>(80.dp) }
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    Card(
        modifier = Modifier
            .padding(2.dp)
            .wrapContentSize(Alignment.Center)
            .fillMaxWidth()
            .onGloballyPositioned { layout ->
                height = density.run { layout.size.width.toDp() }
            }
            .height(height),
        elevation = 2.dp,
        shape = CircleShape,
        onClick = { onClick().also { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
        ) {
            Box {
                content()
            }
        }
    }
}