package com.fphoenixcorneae.ext

import java.io.BufferedReader
import java.io.InputStreamReader

fun executeCmd(command: String): String {
    val process = Runtime.getRuntime().exec(command)

    val resultReader = BufferedReader(InputStreamReader(process.inputStream))
    val errorReader = BufferedReader(InputStreamReader(process.errorStream))

    val resultBuilder = StringBuilder()
    var resultLine: String? = resultReader.readLine()

    val errorBuilder = StringBuilder()
    var errorLine = errorReader.readLine()

    while (resultLine != null) {
        resultBuilder.append(resultLine)
        resultLine = resultReader.readLine()
    }

    while (errorLine != null) {
        errorBuilder.append(errorLine)
        errorLine = errorReader.readLine()
    }

    return "$resultBuilder\n$errorBuilder"
}