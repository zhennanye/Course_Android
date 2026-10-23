package com.yezhennan.exp04.controller

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yezhennan.exp04.PlayerInfo
import com.yezhennan.exp04.R
import com.yezhennan.exp04.adapter.CardAdapter
import com.yezhennan.exp04.model.CardMatchingGame
import com.yezhennan.exp04.view.CardView

class GameActivity : AppCompatActivity() {
    private lateinit var game: CardMatchingGame
    private lateinit var adapter: CardAdapter
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        recycler = findViewById(R.id.recyclerCards)
        val tvScore = findViewById<TextView>(R.id.tvScore)
        val tvHigh = findViewById<TextView>(R.id.tvHighScore)

        // 横竖屏决定牌数与列数
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val cardCount = if (isLandscape) 24 else 12
        val spanCount = if (isLandscape) 12 else 3

        game = CardMatchingGame(cardCount)
        adapter = CardAdapter(game) { index ->
            game.chooseCardAtIndex(index)
            adapter.refresh()
            updateScore(tvScore, tvHigh)
        }

        recycler.layoutManager = GridLayoutManager(this, spanCount)
        recycler.adapter = adapter

        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            game.reset(if (isLandscape) 24 else 12)
            adapter.refresh()
            updateScore(tvScore, tvHigh)
        }
        updateScore(tvScore, tvHigh)
    }

    private fun updateScore(tvScore: TextView, tvHigh: TextView) {
        tvScore.text = getString(R.string.label_score, game.score)
        if (game.score > PlayerInfo.highScore) PlayerInfo.highScore = game.score
        tvHigh.text = getString(R.string.label_score, PlayerInfo.highScore)
    }
}