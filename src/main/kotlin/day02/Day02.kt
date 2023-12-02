package day02

import common.loadInput

data class Reveal(val red: Int, val green: Int, val blue: Int)

data class Game(val id: Int, val reveals: List<Reveal>)

fun main() {
    val input = loadInput(2)
    val games = parseGames(input)

    val partOne = games.filter { it.isPossible(12, 13, 14) }.sumOf { it.id }
    println("Part one: $partOne")

    val partTwo = games.sumOf { it.power() }
    println("Part two: $partTwo")
}

fun Game.isPossible(availableRed: Int, availableGreen: Int, availableBlue: Int): Boolean =
    reveals.none { it.red > availableRed || it.green > availableGreen || it.blue > availableBlue }

fun Game.power() = reveals.maxOf { it.red } * reveals.maxOf { it.green } * reveals.maxOf { it.blue }

fun parseGames(input: String): List<Game> {
    return input.lines().map { line ->
        val id = line.substringBefore(":").substringAfter(" ").toInt()
        val reveals = line.substringAfter(":").split(";").map { reveal ->
            val colors = reveal.split(",")
            val red = colors.find { it.contains("red") }?.substringBefore("red")?.trim()?.toInt() ?: 0
            val green = colors.find { it.contains("green") }?.substringBefore("green")?.trim()?.toInt() ?: 0
            val blue = colors.find { it.contains("blue") }?.substringBefore("blue")?.trim()?.toInt() ?: 0
            Reveal(red, green, blue)
        }
        Game(id, reveals)
    }
}