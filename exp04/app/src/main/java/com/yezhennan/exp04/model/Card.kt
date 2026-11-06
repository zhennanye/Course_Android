package com.yezhennan.exp04.model

import java.io.Serializable

class Card(
    private var suit: String,
    private var rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
): Serializable {
    companion object {
        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")
    }

    override fun toString(): String = suit + rank

    fun getSuit(): String = suit
    fun getRank(): String = rank

    fun match(otherCards: Array<Card>): Int {
        if (otherCards.isEmpty()) return 0
        val allCards = otherCards + this

        if (allCards.size == 2 || allCards.size == 3) {
            val sameSuit  = allCards.count { it.suit == this.suit }
            val sameRank  = allCards.count { it.rank == this.rank }

            return when {
                sameSuit == allCards.size || sameRank == allCards.size -> 8
                else -> 0
            }
        }

        return 0
    }
}