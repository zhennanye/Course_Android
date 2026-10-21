package com.yezhennan.exp04

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yezhennan.exp04.model.CardMatchingGame
import com.yezhennan.exp04.view.CardView

class GameActivity : AppCompatActivity() {

    private lateinit var game: CardMatchingGame
    private lateinit var grid: GridLayout
    private lateinit var tvScore: TextView
    private lateinit var tvHighScore: TextView

    private val cardCount = 12
    private val columnCount = 3
    private val cardViews = mutableListOf<CardView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        grid = findViewById(R.id.gridCards)
        tvScore = findViewById(R.id.tvScore)
        tvHighScore = findViewById(R.id.tvHighScore)

        findViewById<Button>(R.id.btnRestart).setOnClickListener { startNewGame() }

        startNewGame()
    }

    private fun startNewGame() {
        game = CardMatchingGame(cardCount)
        buildGrid()
        updateUI()
    }

    private fun buildGrid() {
        grid.removeAllViews()
        grid.columnCount = columnCount
        cardViews.clear()

        for (i in 0 until game.cardCount()) {
            val cardView = CardView(this)
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            cardView.layoutParams = params
            cardView.setOnClickListener { onCardClicked(i) }
            grid.addView(cardView)
            cardViews.add(cardView)
        }
    }

    private fun onCardClicked(index: Int) {
        game.chooseCardAtIndex(index)
        updateUI()
    }

    private fun updateUI() {
        for (i in 0 until game.cardCount()) {
            cardViews[i].render(game.cardAtIndex(i))
        }
        tvScore.text = getString(R.string.label_score, game.score)

        if (game.score > PlayerInfo.highScore) {
            PlayerInfo.highScore = game.score
        }
        tvHighScore.text = getString(R.string.label_score, PlayerInfo.highScore)
    }
}