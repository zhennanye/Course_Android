package com.yezhennan.exp04

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvHighScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvName = findViewById(R.id.tvName)
        tvGender = findViewById(R.id.tvGender)
        tvPhone = findViewById(R.id.tvPhone)
        tvHighScore = findViewById(R.id.tvHighScore)

        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            startActivity(Intent(this, EditInfoActivity::class.java))
        }
        findViewById<Button>(R.id.btnGame).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        tvName.text = getString(R.string.label_name, PlayerInfo.name)
        tvGender.text = getString(R.string.label_gender, PlayerInfo.gender)
        tvPhone.text = getString(R.string.label_phone, PlayerInfo.phone)
        tvHighScore.text = getString(R.string.label_score, PlayerInfo.highScore)
    }
}