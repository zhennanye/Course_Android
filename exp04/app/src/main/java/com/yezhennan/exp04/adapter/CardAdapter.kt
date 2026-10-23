package com.yezhennan.exp04.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yezhennan.exp04.R
import com.yezhennan.exp04.model.CardMatchingGame
import com.yezhennan.exp04.view.CardView

class CardAdapter(
    private val game: CardMatchingGame,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardHolder>() {

    class CardHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false) as CardView
        val holder = CardHolder(view)
        view.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) onCardClick(pos)
        }
        return holder
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val card = game.cardAtIndex(position)
        holder.cardView.render(card)
        holder.cardView.setOnClickListener { onCardClick(position) }
    }

    override fun getItemCount(): Int = game.cardCount()

    fun refresh() {
        notifyDataSetChanged()
    }
}