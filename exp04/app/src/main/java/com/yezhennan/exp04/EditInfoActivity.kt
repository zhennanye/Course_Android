package com.yezhennan.exp04

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_info)

        val etName = findViewById<EditText>(R.id.etName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val rgGender = findViewById<RadioGroup>(R.id.rgGender)

        etName.setText(PlayerInfo.name)
        etPhone.setText(PlayerInfo.phone)
        if (PlayerInfo.gender == "男") rgGender.check(R.id.rbMale)
        else rgGender.check(R.id.rbFemale)

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "姓名和电话不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            PlayerInfo.name = name
            PlayerInfo.phone = phone
            PlayerInfo.gender = if (rgGender.checkedRadioButtonId == R.id.rbMale) "男" else "女"
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}