package day03

import common.loadInput

data class Point(val x: Int, val y: Int, val char: Char)

data class Grid(val points: List<Point>, val xSize: Int, val ySize: Int)

data class Number(val value: Int, val points: List<Point>)

fun main() {
    val input = loadInput(3)
    val points = input.lines().mapIndexed { y, line -> line.mapIndexed { x, char -> Point(x, y, char) } }.flatten()
    val xSize = input.lines()[0].length
    val ySize = input.lines().size

    val grid = Grid(points, xSize, ySize)

    val numbers = run {
        val numbers = mutableListOf<Number>()
        val currentNumberPoints = mutableListOf<Point>()

        for (y in 0 until ySize) {
            for (x in 0 until xSize) {
                val point = grid.points.first { it.x == x && it.y == y }
                if (point.char.isDigit()) {
                    currentNumberPoints += point
                } else if (currentNumberPoints.isNotEmpty()) {
                    val currentNumber = currentNumberPoints.map { it.char }.joinToString("").toInt()
                    numbers += Number(currentNumber, currentNumberPoints.toList())
                    currentNumberPoints.clear()
                }
            }
        }

        numbers.toList()
    }

    val isSymbol = {char: Char -> !char.isDigit() && char != '.' }
    val validNumbers = numbers.filter { it.points.any { point -> point.findAdjacentSymbol(grid, isSymbol) != null } }
    val partOne = validNumbers.sumOf { it.value }
    println("Part one: $partOne")

    val numbersWithStar = numbers.mapNotNull { number ->
        val starPoint = number.points.firstNotNullOfOrNull { point -> point.findAdjacentSymbol(grid) { !it.isDigit() && it == '*' }}
        starPoint?.let { number to it }
    }

    val gears = numbersWithStar.mapNotNull { (number, point) ->
        numbersWithStar.find { it.second == point && it.first != number }?.let { Triple(number, it.first, point) }
    }.distinctBy { it.third }
    val partTwo = gears.sumOf { (number1, number2) -> number1.value * number2.value }
    println("Part two: $partTwo")
}

fun Point.findAdjacentSymbol(grid: Grid, isSymbol: (Char) -> Boolean): Point? {
    val positionsToCheck = listOf(
        x - 1 to y,
        x + 1 to y,
        x to y - 1,
        x to y + 1,
        x - 1 to y - 1,
        x + 1 to y + 1,
        x - 1 to y + 1,
        x + 1 to y - 1
    ).filter { it.first >= 0 && it.first < grid.xSize && it.second >= 0 && it.second < grid.ySize }

    val pointsToCheck =
        positionsToCheck.map { grid.points.first { point -> point.x == it.first && point.y == it.second } }
    return pointsToCheck.find { isSymbol(it.char) }
}