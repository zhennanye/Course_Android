package com.yezhennan.exp04

import com.yezhennan.exp04.model.Card
import com.yezhennan.exp04.model.CardMatchingGame
import org.junit.Assert.*
import org.junit.Test

class CardMatchingGameTest {

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
    @Test
    fun testResetClearsMatchedAndScore() {
        val game = CardMatchingGame(12)
        game.chooseCardAtIndex(0)
        game.reset(12)
        assertEquals(0, game.score)
        assertTrue(game.cardAtIndex(0).isChosen.not())
    }

    @Test
    fun testThreeSameSuitMatch() {
        val a = Card("♥", "A"); val b = Card("♥", "K"); val c = Card("♥", "Q")
        assertEquals(8, c.match(arrayOf(a, b)))
    }

    @Test
    fun testThreeSameRankMatch() {
        val a = Card("♥", "A"); val b = Card("♠", "A"); val c = Card("♦", "A")
        assertEquals(8, c.match(arrayOf(a, b)))
    }

    @Test
    fun testThreeNoMatch() {
        val a = Card("♥", "A"); val b = Card("♠", "K"); val c = Card("♦", "Q")
        assertEquals(0, c.match(arrayOf(a, b)))
    }

}