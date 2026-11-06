package com.yezhennan.exp04.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.yezhennan.exp04.AppStorage
import com.yezhennan.exp04.EditInfoActivity
import com.yezhennan.exp04.PlayerInfo
import com.yezhennan.exp04.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var name: TextView
    private lateinit var gender: TextView
    private lateinit var phone: TextView
    private lateinit var highScore: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppStorage.load<com.yezhennan.exp04.PlayerProfile>(requireContext(), "player_profile.dat")
            ?.let(PlayerInfo::restore)
        name = view.findViewById(R.id.tvName)
        gender = view.findViewById(R.id.tvGender)
        phone = view.findViewById(R.id.tvPhone)
        highScore = view.findViewById(R.id.tvHighScore)
        view.findViewById<Button>(R.id.btnEdit).setOnClickListener {
            startActivity(Intent(requireContext(), EditInfoActivity::class.java))
        }
        refresh()
    }

    override fun onResume() {
        super.onResume()
        if (::name.isInitialized) refresh()
    }

    override fun onPause() {
        super.onPause()
        if (::name.isInitialized) AppStorage.save(requireContext(), "player_profile.dat", PlayerInfo.snapshot())
    }

    private fun refresh() {
        name.text = getString(R.string.label_name, PlayerInfo.name)
        gender.text = getString(R.string.label_gender, PlayerInfo.gender)
        phone.text = getString(R.string.label_phone, PlayerInfo.phone)
        highScore.text = getString(R.string.label_score, PlayerInfo.highScore)
    }
}
