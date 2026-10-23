package com.yezhennan.exp04

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.yezhennan.exp04.controller.GameActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvHighScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1, 0, getString(R.string.aboutBtn_text))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.aboutBtn_text))
                .setMessage(getString(R.string.about_text))
                .setPositiveButton(getString(R.string.confirm_text), null)
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}