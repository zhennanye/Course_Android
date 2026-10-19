package com.yezhennan.exp03

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class Activity4 : ComponentActivity() {

    private lateinit var tvTime: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnReset: Button

    private var elapsed = 0L
    private var startBase = 0L
    private var running = false

    private val handler = Handler(Looper.getMainLooper())

    private val ticker = object : Runnable {
        override fun run() {
            if (running) {
                val total = elapsed + (SystemClock.elapsedRealtime() - startBase)
                tvTime.text = formatTime(total)
                handler.postDelayed(this, 30)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pad = (16 * resources.displayMetrics.density + 0.5f).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(pad, pad, pad, pad)
        }

        tvTime = TextView(this).apply {
            textSize = 40f
            setText(R.string.tv_time_default)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        btnStart = makeBtn(R.string.btn_start)
        btnPause = makeBtn(R.string.btn_pause)
        btnReset = makeBtn(R.string.btn_reset)

        val btnRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = pad * 2 }
        }

        val btnLp = LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        )
        btnRow.addView(btnStart, btnLp)
        btnRow.addView(btnPause, btnLp)
        btnRow.addView(btnReset, btnLp)

        root.addView(tvTime)
        root.addView(btnRow)
        setContentView(root)

        savedInstanceState?.let { state ->
            elapsed = state.getLong(getString(R.string.state_elapsed), 0L)
            val wasRunning = state.getBoolean(getString(R.string.state_running), false)
            if (wasRunning) {
                startBase = SystemClock.elapsedRealtime()
                running = true
                handler.post(ticker)
            }
        }
        updateUI()

        btnStart.setOnClickListener { start() }
        btnPause.setOnClickListener { pause() }
        btnReset.setOnClickListener { reset() }
    }

    private fun makeBtn(textRes: Int): Button = Button(this).apply {
        setText(textRes)
    }

    private fun start() {
        if (running) return
        startBase = SystemClock.elapsedRealtime()
        running = true
        handler.post(ticker)
        updateUI()
    }

    private fun pause() {
        if (!running) return
        elapsed += SystemClock.elapsedRealtime() - startBase
        running = false
        handler.removeCallbacks(ticker)
        tvTime.text = formatTime(elapsed)
        updateUI()
    }

    private fun reset() {
        running = false
        elapsed = 0L
        handler.removeCallbacks(ticker)
        tvTime.setText(R.string.tv_time_default)
        updateUI()
    }

    private fun updateUI() {
        btnStart.isEnabled = !running
        btnPause.isEnabled = running
    }

    private fun formatTime(ms: Long): String {
        val h = ms / 3600000
        val m = (ms % 3600000) / 60000
        val s = (ms % 60000) / 1000
        val msec = ms % 1000
        return String.format(Locale.getDefault(), "%02d:%02d:%02d.%03d", h, m, s, msec)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var current = elapsed
        if (running) {
            current += SystemClock.elapsedRealtime() - startBase
        }
        outState.putLong(getString(R.string.state_elapsed), current)
        outState.putBoolean(getString(R.string.state_running), running)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(ticker)
    }
}