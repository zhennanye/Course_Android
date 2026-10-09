package com.example.yezhennan2024110543exp02

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class ProgramAdviser (private val context: Context) {
    fun getAdvise(idx: Int): List<String> {
        val resId = when (idx) {
            0 -> R.array.advise_web
            1 -> R.array.advise_mobile
            2 -> R.array.advise_data_ai
            3 -> R.array.advise_system
            else -> R.array.advise_default
        }
        return context.resources.getStringArray(resId).toList()
    }
}

class MainActivity : ComponentActivity() {
    private val adviser = ProgramAdviser(this)

    private val wrapCenteredParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        wrapCenteredParams.gravity = Gravity.CENTER

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        section1(root)

        root.addView(View(this).apply{
            setBackgroundColor(getColor(R.color.divider_color))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                10
            )
        })

        section2(root)

        setContentView(root)
    }

    fun section1(layout: LinearLayout) {
        val addTextViewBtn = Button(this).apply {
            text = getString(R.string.add_text_view_btn)
            layoutParams = wrapCenteredParams
        }
        layout.addView(addTextViewBtn)

        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
            )
        }
        layout.addView(scrollView)

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(getColor(R.color.section_one_color))
        }

        scrollView.addView(
            container,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        fun addView(cnt: Int) {
            container.addView(TextView(this).apply {
                text = getString(R.string.text_view_example, cnt)
            })
            addTextViewBtn.setOnClickListener {
                addView(cnt+1)
            }
        }

        addTextViewBtn.setOnClickListener {
            addView(1)
        }
    }

    fun section2(layout: LinearLayout) {
        val label = TextView(this).apply {
            text = getString(R.string.label_select_program)
        }
        layout.addView(label)

        val spinner = Spinner(this)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.program_types,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter
        layout.addView(spinner)

        val findBtn = Button(this).apply {
            text = getString(R.string.btn_find_adviser)
            layoutParams = wrapCenteredParams
        }
        layout.addView(findBtn)

        val resultTv = TextView(this).apply {
            text = getString(R.string.result_default)
            setBackgroundColor(getColor((R.color.section_two_color)))
        }
        layout.addView(resultTv)

        findBtn.setOnClickListener {
            val adviceList = adviser.getAdvise(spinner.selectedItemPosition)
            val prefix = getString(R.string.result_prefix)
            resultTv.text = adviceList.joinToString(separator = "\n• ", prefix = "$prefix\n• ")
        }
    }

}