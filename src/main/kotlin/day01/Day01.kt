package day01

import common.loadInput

fun main() {
    val input = loadInput(1)
    val partOne = input.lines().sumOf { line ->
        val first = line.find { it.isDigit() }!!.toString().toInt()
        val last = line.findLast { it.isDigit() }!!.toString().toInt()
        first * 10 + last
    }
    println("Part one: $partOne")

    val map = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9
    ) + (0..9).associateBy { it.toString() }

    val partTwo = input.lines().sumOf { line ->
        val firstNumber = map.entries.map {line.indexOf(it.key) to it.value}.filter { it.first != -1 }.minByOrNull { it.first }!!.second
        val secondNumber = map.entries.map {line.lastIndexOf(it.key) to it.value}.filter { it.first != -1 }.maxByOrNull { it.first }!!.second

        firstNumber * 10 + secondNumber
    }

    println("Part two: $partTwo")
}

