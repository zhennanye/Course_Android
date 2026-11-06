package com.yezhennan.exp04.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yezhennan.exp04.AppStorage
import com.yezhennan.exp04.PlayerInfo
import com.yezhennan.exp04.R
import com.yezhennan.exp04.adapter.CardAdapter
import com.yezhennan.exp04.model.CardMatchingGame

abstract class CardGameFragment(
    private val cardCount: Int,
    private val storageFile: String
) : Fragment(R.layout.fragment_game) {
    private lateinit var game: CardMatchingGame
    private lateinit var adapter: CardAdapter
    private lateinit var scoreView: TextView
    private lateinit var highScoreView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val saved = AppStorage.load<CardMatchingGame>(requireContext(), storageFile)
        game = saved ?: CardMatchingGame(cardCount, cardCount)
        scoreView = view.findViewById(R.id.tvScore)
        highScoreView = view.findViewById(R.id.tvHighScore)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerCards)
        adapter = CardAdapter(game) {
            game.chooseCardAtIndex(it)
            adapter.refresh()
            updateScore()
            saveGame()
        }
        val spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cardCount
        } else {
            cardCount
        }
        recycler.layoutManager = GridLayoutManager(requireContext(), spanCount)
        recycler.adapter = adapter
        view.findViewById<Button>(R.id.btnRestart).setOnClickListener {
            game.reset(cardCount)
            adapter.refresh()
            updateScore()
            saveGame()
        }
        updateScore()
    }

    override fun onPause() {
        super.onPause()
        if (::game.isInitialized) saveGame()
    }

    private fun saveGame() {
        AppStorage.save(requireContext(), storageFile, game)
    }

    private fun updateScore() {
        scoreView.text = getString(R.string.label_score, game.score)
        if (game.score > PlayerInfo.highScore) {
            PlayerInfo.highScore = game.score
            AppStorage.save(requireContext(), "player_profile.dat", PlayerInfo.snapshot())
        }
        highScoreView.text = getString(R.string.label_high_score, PlayerInfo.highScore)
    }
}
