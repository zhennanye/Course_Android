package com.yezhennan.exp03

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL

        val pad = 16

        root.setPadding(pad, pad, pad, pad)

        val btn1 = makeBtn(R.string.btn_activity1)
        val btn2 = makeBtn(R.string.btn_activity2)
        val act2Tv = TextView(this)
        val btn3 = makeBtn(R.string.btn_activity3)
        val btn4 = makeBtn(R.string.btn_activity4)

        btn1.setOnClickListener {
            val intent = Intent(this, Activity1::class.java)
            intent.putExtra(getString(R.string.param_key), getString(R.string.param_value))
            startActivity(intent)
        }

        val launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                act2Tv.text = result.data?.getStringExtra(getString(R.string.result_key))
            }
        }

        btn2.setOnClickListener {
            launcher.launch(Intent(this, Activity2::class.java))
        }

        btn3.setOnClickListener {
            startActivity(Intent(this, Activity3::class.java))
        }

        btn4.setOnClickListener {
            startActivity(Intent(this, Activity4::class.java))
        }

        val views = arrayOf(btn1, btn2, act2Tv, btn3, btn4)
        views.forEach { view ->
            root.addView(view)
        }

        setContentView(root)
    }

    fun makeBtn(textRes: Int) = Button(this).apply {
        text = getString(textRes)
    }
}
