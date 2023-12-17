package day09

import common.loadInput

data class SequencePart(val numbers: List<Int>)
data class Sequence(val parts: List<SequencePart>)

fun main() {
    val input = loadInput(9)

    val sequences = input.lines().map { line -> calculateSequence(line.split(" ").map { it.toInt() }) }
    val partOne = sequences.sumOf { it.nextNumberRight() }
    println("Part one: $partOne")

    val partTwo = sequences.sumOf { it.nextNumberLeft() }
    println("Part two: $partTwo")
}

fun calculateSequence(ints: List<Int>): Sequence {
    val parts = mutableListOf(SequencePart(ints))

    var currentLevel = ints
    while (currentLevel.any { it != 0 }) {
        currentLevel = currentLevel.windowed(2).map { (a, b) -> b - a }
        parts.add(SequencePart(currentLevel))
    }

    return Sequence(parts)
}

fun Sequence.nextNumberRight(): Int = parts.first().nextNumberRight(parts.drop(1))

fun SequencePart.nextNumberRight(partsBelow: List<SequencePart>): Int {
    if (numbers.all { it == 0 }) return 0
    return numbers.last() + partsBelow.first().nextNumberRight(partsBelow.drop(1))
}

fun Sequence.nextNumberLeft(): Int = parts.first().nextNumberLeft(parts.drop(1))

fun SequencePart.nextNumberLeft(partsBelow: List<SequencePart>): Int {
    if (numbers.all { it == 0 }) return 0
    return numbers.first() - partsBelow.first().nextNumberLeft(partsBelow.drop(1))
}
