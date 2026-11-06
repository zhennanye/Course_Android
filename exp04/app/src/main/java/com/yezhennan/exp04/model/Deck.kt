package com.yezhennan.exp04.model

import java.util.Random

class Deck {
    private val cards = mutableListOf<Card>()
    private val r = Random()

    init {
        for (suit in Card.validSuits) {
            for (rank in Card.rankStrings) {
                cards.add(Card(suit = suit, rank = rank))
            }
        }
    }

    fun drawRandomCard(): Card? {
        var randomCard: Card? = null
        if (cards.isNotEmpty()) {
            randomCard = cards.removeAt(r.nextInt(cards.size))
        }
        return randomCard
    }

    fun remainingCount(): Int = cards.size
}