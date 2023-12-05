package day04

import common.loadInput
import kotlin.math.pow

data class Card(val id: Int, val winning: Set<Int>, val mine: Set<Int>) {
    val winningCount by lazy { winning.intersect(mine).count() }
    val value by lazy { if (winningCount == 0) 0 else 2.0.pow(winningCount - 1).toInt() }
}

fun main() {
    val input = loadInput(4)

    val cards = input.lines().map { parseCard(it) }
    val partOne = cards.sumOf { it.value }
    println("Part one: $partOne")

    val partTwo = cards.sumOf { expandCard(it, cards) }
    println("Part two: $partTwo")
}

fun expandCard(card: Card, cards: List<Card>): Int {
    if(card.value == 0) return 1

    val otherCards = cards.subList(card.id, card.id + card.winningCount)
    return otherCards.sumOf { expandCard(it, cards) } + 1
}

fun parseCard(line: String): Card {
    val id = line.substringBefore(":").substringAfter("Card").trim().toInt()

    fun processNumbers(numbers: String) =
        numbers.split(" ").map { it.trim() }.filter { it.isNotBlank() }.map { it.toInt() }.toSet()

    val winning = processNumbers(line.substringAfter(":").substringBefore("|"))
    val mine = processNumbers(line.substringAfter("|"))

    return Card(id, winning, mine)
}