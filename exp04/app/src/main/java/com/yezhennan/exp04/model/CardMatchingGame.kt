package com.yezhennan.exp04.model

class CardMatchingGame(
    count: Int,
    private val cardsToMatch: Int = 3
) : java.io.Serializable {
    var score = 0
        private set

    private val cards: MutableList<Card> = mutableListOf()

    init {
        val deck = Deck()
        for (i in 1..count) {
            deck.drawRandomCard()?.let { cards.add(it) }
        }
    }

    fun cardAtIndex(index: Int): Card = cards[index]

    fun cardCount(): Int = cards.size

    companion object {
        const val MISMATCH_PENALTY = 2
        const val MATCH_BONUS = 4
        const val COST_TO_CHOOSE = 1
    }

    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)

        if (card.isMatched) return

        // 点击已选中的牌：取消选中（退选，不扣分也不判定）
        if (card.isChosen) {
            card.isChosen = false
            return
        }

        val chosen = cards.filter { it.isChosen && !it.isMatched }

        if (chosen.size >= cardsToMatch) return

        // 翻开这张牌，扣费
        card.isChosen = true
        score -= COST_TO_CHOOSE

        // 凑满 3 张才判定
        if (chosen.size == cardsToMatch - 1) {
            val selected = (chosen + card).toTypedArray()
            val matchScore = card.match(chosen.toTypedArray())
            if (matchScore > 0) {
                score += matchScore * MATCH_BONUS
                selected.forEach {
                    it.isMatched = true
                    it.isChosen = false
                }
            } else {
                score -= MISMATCH_PENALTY
                selected.forEach { it.isChosen = false }
                // 若想失败后保留刚点的这张，改为：
                card.isChosen = true
            }
        }

    }

    fun isGameOver(): Boolean = cards.all { it.isMatched }

    fun reset(count: Int) {
        cards.clear()
        score = 0
        val deck = Deck()
        for (i in 1..count) {
            deck.drawRandomCard()?.let { cards.add(it) }
        }
    }
}