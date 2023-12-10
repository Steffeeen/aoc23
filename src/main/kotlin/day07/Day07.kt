package day07

import common.loadInput

data class Hand(val cards: String, val bid: Int)
data class JokerHand(val cards: String, val bid: Int, val bestCards: String)

fun main() {
    val input = loadInput(7)

    val hands = input.lines().map {
        val (hand, bid) = it.split(" ")
        Hand(hand, bid.toInt())
    }

    val sortedHands = hands.sortedWith(HandComparator)
    val partOne = sortedHands.mapIndexed { index, (_, bid) -> (index + 1) * bid }.sum()
    println("Part one: $partOne")

    val jokerHands = hands.map { it.toJokerHand() }
    val sortedJokerHands = jokerHands.sortedWith(JokerHandComparator)
    val partTwo = sortedJokerHands.mapIndexed { index, (_, bid, _) -> (index + 1) * bid }.sum()
    println("Part two: $partTwo")
}

fun Hand.toJokerHand(): JokerHand {
    // count the occurrences of each card, except for jokers
    val cardCounts = this.cards.groupingBy { it }.eachCount().filter { (char, _) -> char != 'J' }
        .map { (char, count) -> char to count }

    val sortedCardCounts = cardCounts.sortedByDescending { (_, count) -> count }
    // get all the most common chars, as there may be multiple
    val mostCommonCount = sortedCardCounts.first().second
    val mostCommonChars = sortedCardCounts.takeWhile { (_, count) -> count == mostCommonCount }.map { (char, _) -> char }
    val highestValueMostCommonChar =
        mostCommonChars.sortedWith(SingleCharComparator(jokerCharOrdering)).lastOrNull() ?: 'A'

    val bestCards = this.cards.replace('J', highestValueMostCommonChar)
    return JokerHand(this.cards, this.bid, bestCards)
}

val comparisonFunctions = listOf(
    String::isFiveOfAKind,
    String::isFourOfAKind,
    String::isFullHouse,
    String::isThreeOfAKind,
    String::isTwoPair,
    String::isOnePair,
    String::isHighCard
)

fun compareHandString(hand1: String, hand2: String): Int {
    if (hand1 == hand2) return 0
    for (comparisonFunction in comparisonFunctions) {
        val hand1Result = comparisonFunction(hand1)
        val hand2Result = comparisonFunction(hand2)
        when {
            hand1Result && !hand2Result -> return 1
            !hand1Result && hand2Result -> return -1
        }
    }
    return 0
}

val normalCharOrdering = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
val jokerCharOrdering = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')

object HandComparator : Comparator<Hand> {
    private val cardByCardComparator =
        CardByCardComparator(normalCharOrdering)

    override fun compare(hand1: Hand, hand2: Hand): Int {
        val hand1Result = compareHandString(hand1.cards, hand2.cards)
        if (hand1Result != 0) return hand1Result
        return cardByCardComparator.compare(hand1.cards, hand2.cards)
    }
}

object JokerHandComparator : Comparator<JokerHand> {
    private val cardByCardComparator =
        CardByCardComparator(jokerCharOrdering)

    override fun compare(hand1: JokerHand, hand2: JokerHand): Int {
        val hand1Result = compareHandString(hand1.bestCards, hand2.bestCards)
        if (hand1Result != 0) return hand1Result
        return cardByCardComparator.compare(hand1.cards, hand2.cards)
    }
}

class CardByCardComparator(charOrdering: List<Char>) : Comparator<String> {
    private val singleCharComparator = SingleCharComparator(charOrdering)
    override fun compare(hand1: String, hand2: String): Int {
        if (hand1 == hand2) return 0
        return hand1.zip(hand2).map { (c1, c2) -> singleCharComparator.compare(c1, c2) }.firstOrNull { it != 0 } ?: 0
    }
}

class SingleCharComparator(private val charOrdering: List<Char>) : Comparator<Char> {
    override fun compare(c1: Char, c2: Char): Int {
        if (c1 == c2) return 0

        val c1Index = charOrdering.indexOf(c1)
        val c2Index = charOrdering.indexOf(c2)
        require(c1Index != -1 && c2Index != -1)
        return when {
            c1Index < c2Index -> 1
            c1Index > c2Index -> -1
            else -> error("Should not happen")
        }
    }
}

fun String.isFiveOfAKind(): Boolean = this.chars().distinct().count() == 1L
fun String.isFourOfAKind(): Boolean = this.groupingBy { it }.eachCount().values.any { it == 4 }
fun String.isFullHouse(): Boolean {
    val counts = this.groupingBy { it }.eachCount()
    if (counts.size != 2) return false
    return counts.values.any { it == 3 } && counts.values.any { it == 2 }
}

fun String.isThreeOfAKind(): Boolean = this.groupingBy { it }.eachCount().values.any { it == 3 }
fun String.isTwoPair(): Boolean = this.groupingBy { it }.eachCount().values.count { it == 2 } == 2
fun String.isOnePair(): Boolean = this.groupingBy { it }.eachCount().values.any { it == 2 }
fun String.isHighCard(): Boolean = this.chars().distinct().count() == 5L
