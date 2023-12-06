package day06

import common.loadInput

data class Race(val time: Long, val distance: Long)

fun main() {
    val input = loadInput(6)
    val races = parseRaces(input)

    val partOne = races.map { it.waysToWin() }.reduce(Int::times)
    println("Part one: $partOne")

    val race = Race(
        input.lines()[0].substringAfter(":").replace(" ", "").toLong(),
        input.lines()[1].substringAfter(":").replace(" ", "").toLong()
    )

    val partTwo = race.waysToWin()
    println("Part two: $partTwo")
}

fun Race.waysToWin(): Int = (0..this.time).count {
    calculateTravelledDistance(it, this.time - it) > this.distance
}

fun calculateTravelledDistance(speed: Long, time: Long): Long = speed * time

fun parseRaces(input: String): List<Race> {
    val times = input.lines()[0].substringAfter(":").trim().split(" ").map { it.trim() }.filter { it.isNotEmpty() }
        .map { it.toLong() }
    val distances = input.lines()[1].substringAfter(":").trim().split(" ").map { it.trim() }.filter { it.isNotEmpty() }
        .map { it.toLong() }

    return times.zip(distances) { time, distance -> Race(time, distance) }
}