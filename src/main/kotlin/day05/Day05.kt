package day05

import common.loadInput

enum class Category {
    SEED,
    SOIL,
    FERTILIZER,
    WATER,
    LIGHT,
    TEMPERATURE,
    HUMIDITY,
    LOCATION,
}

data class CategoryMap(val sourceCategory: Category, val destCategory: Category, val ranges: List<Range>) {
    fun convert(value: Long): Long {
        val range = ranges.find { it.contains(value) } ?: return value
        return range.dest + (value - range.source)
    }
}

data class Range(val source: Long, val dest: Long, val length: Long) {
    fun contains(value: Long) = value in source until (source + length)
}

fun main() {
    val input = loadInput(5)

    val seeds = parseSeeds(input)
    val categoryPairs = Category.values().dropLast(1).zip(Category.values().drop(1))
    val maps = categoryPairs.map { (sourceCategory, destCategory) -> parseMap(sourceCategory, destCategory, input) }

    val locations = seeds.map { convertSeedToLocation(it, maps) }

    val partOne = locations.min()
    println("Part One: $partOne")

    val seedRanges = parseSeedRanges(input)
    val partTwo = seedRanges.minOfOrNull { range ->
        println("Checking range $range")
        range.minOf { convertSeedToLocation(it, maps) }
    } ?: -1

    println("Part Two: $partTwo")
}

fun convertSeedToLocation(seed: Long, maps: List<CategoryMap>): Long {
    return maps.fold(seed) { value, map -> map.convert(value) }
}

fun parseMap(sourceCategory: Category, destCategory: Category, input: String): CategoryMap {
    val relevantInput = input
        .substringAfter("${sourceCategory.toString().lowercase()}-to-${destCategory.toString().lowercase()} map:")
        .substringBefore("\n\n")
        .trim()

    val ranges = relevantInput.substringAfter(":\n").lines().map { line ->
        val (destStart, sourceStart, length) = line.split(" ")
        Range(sourceStart.toLong(), destStart.toLong(), length.toLong())
    }

    return CategoryMap(sourceCategory, destCategory, ranges)
}

fun parseSeedRanges(input: String): List<LongRange> {
    val longs = parseSeeds(input)
    require(longs.size % 2 == 0)

    return longs.chunked(2).map { (start, length) -> start until (start + length) }
}

fun parseSeeds(input: String) =
    input.lines()[0]
    .substringAfter(":")
    .split(" ")
    .map { it.trim() }
    .filter { it.isNotEmpty() }
    .map { it.toLong() }