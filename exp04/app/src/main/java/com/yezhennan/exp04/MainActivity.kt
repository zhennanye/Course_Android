package com.yezhennan.exp04

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.yezhennan.exp04.fragment.ProfileFragment
import com.yezhennan.exp04.fragment.ThreeCardFragment
import com.yezhennan.exp04.fragment.TwoCardFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        if (savedInstanceState == null) {
            showFragment(ProfileFragment())
        }
        findViewById<android.view.View>(R.id.btnProfile).setOnClickListener {
            showFragment(ProfileFragment())
        }
        findViewById<android.view.View>(R.id.btnTwoCards).setOnClickListener {
            showFragment(TwoCardFragment())
        }
        findViewById<android.view.View>(R.id.btnThreeCards).setOnClickListener {
            showFragment(ThreeCardFragment())
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
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
