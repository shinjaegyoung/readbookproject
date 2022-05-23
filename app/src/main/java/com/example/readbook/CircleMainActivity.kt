package com.example.readbook

import androidx.appcompat.app.AppCompatActivity
import com.dinuscxj.progressbar.CircleProgressBar.ProgressFormatter
import com.dinuscxj.progressbar.CircleProgressBar
import android.os.Bundle
import com.example.readbook.R
import com.example.readbook.CircleMainActivity

class CircleMainActivity : AppCompatActivity(), ProgressFormatter {
    var circleProgressBar: CircleProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_main)
        circleProgressBar = findViewById(R.id.cpb_circlebar)
        circleProgressBar?.setProgress(70)
//        circleProgressBar.setProgress(70) // 해당 퍼센트를 적용
    }

    override fun format(progress: Int, max: Int): CharSequence {
        return String.format(DEFAULT_PATTERN, (progress.toFloat() / max.toFloat() * 100).toInt())
    }

    companion object {
        private const val DEFAULT_PATTERN = "%d%%"
    }
}