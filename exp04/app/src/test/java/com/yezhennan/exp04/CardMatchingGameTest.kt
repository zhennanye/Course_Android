package com.yezhennan.exp04

import com.yezhennan.exp04.model.Card
import com.yezhennan.exp04.model.CardMatchingGame
import org.junit.Assert.*
import org.junit.Test

class CardMatchingGameTest {

    @Test
    fun testCardMatchSameRank() {
        val a = Card("♥", "A")
        val b = Card("♠", "A")
        assertEquals(4, a.match(arrayOf(b)))
    }

    @Test
    fun testCardMatchSameSuit() {
        val a = Card("♥", "A")
        val b = Card("♥", "K")
        assertEquals(1, a.match(arrayOf(b)))
    }

    @Test
    fun testCardNoMatch() {
        val a = Card("♥", "A")
        val b = Card("♠", "K")
        assertEquals(0, a.match(arrayOf(b)))
    }

    @Test
    fun testChooseCardDeductsCost() {
        val game = CardMatchingGame(4)
        val before = game.score
        game.chooseCardAtIndex(0)
        assertTrue(game.score < before)
        assertTrue(game.cardAtIndex(0).isChosen)
    }

    @Test
    fun testChooseSameCardDeselects() {
        val game = CardMatchingGame(4)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(0)
        assertFalse(game.cardAtIndex(0).isChosen)
    }

    @Test
    fun testCardCount() {
        val game = CardMatchingGame(10)
        assertEquals(10, game.cardCount())
    }
}