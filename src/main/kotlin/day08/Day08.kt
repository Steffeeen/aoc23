package day08

import common.loadInput

enum class Direction {
    LEFT, RIGHT
}

data class Node(val name: String, val left: String, val right: String)

fun main() {
    val input = loadInput(8)

    val (instructionsString, networkString) = input.split("\n\n")
    val instructions = instructionsString.map { if (it == 'R') Direction.RIGHT else Direction.LEFT }
    val nodeRegex = Regex("([A-Z0-9]{3}) = \\(([A-Z0-9]{3}), ([A-Z0-9]{3})\\)")
    val nodes = networkString.split("\n").map { nodeRegex.matchEntire(it)!! }
        .map { Node(it.groupValues[1], it.groupValues[2], it.groupValues[3]) }

    val partOne = findNumberOfSteps("AAA", "ZZZ", nodes, instructions)
    println("Part one: $partOne")

    val loops = nodes.filter { it.name.endsWith("A") }.map { findLoop(it, nodes, instructions) }

    val partTwo = loops.map { it.nodes.size.toLong() / instructions.size }.reduce(Long::times) * instructions.size
    println("Part two: $partTwo")
}

fun findNumberOfSteps(from: String, to: String, nodes: List<Node>, instructions: List<Direction>): Int {
    val nodeMap = nodes.associateBy { it.name }
    var currentNode = nodeMap[from]!!
    var steps = 0

    while (currentNode.name != to) {
        currentNode = when (instructions[steps % instructions.size]) {
            Direction.LEFT -> nodeMap[currentNode.left]!!
            Direction.RIGHT -> nodeMap[currentNode.right]!!
        }
        steps++
    }

    return steps
}

data class Loop(val start: Node, val nodes: List<String>, val instruction: Int, val steps: Int)

fun findLoop(start: Node, nodes: List<Node>, instructions: List<Direction>): Loop {
    val nodeMap = nodes.associateBy { it.name }
    var currentNode = start
    var steps = 0

    val seenStates = mutableSetOf<Pair<String, Int>>()

    while (true) {
        if (seenStates.contains(Pair(currentNode.name, steps % instructions.size))) {
            val nodesInLoop = mutableListOf(currentNode.name)
            val statesUntilLoop = steps
            while (true) {
                currentNode = when (instructions[steps % instructions.size]) {
                    Direction.LEFT -> nodeMap[currentNode.left]!!
                    Direction.RIGHT -> nodeMap[currentNode.right]!!
                }
                steps++

                if (currentNode.name == nodesInLoop.first()) {
                    break
                }
                nodesInLoop.add(currentNode.name)
            }
            return Loop(start, nodesInLoop, statesUntilLoop % instructions.size, statesUntilLoop)
        }

        if (currentNode.name.endsWith("Z")) {
            seenStates.add(Pair(currentNode.name, steps % instructions.size))
        }

        currentNode = when (instructions[steps % instructions.size]) {
            Direction.LEFT -> nodeMap[currentNode.left]!!
            Direction.RIGHT -> nodeMap[currentNode.right]!!
        }
        steps++
    }
}
