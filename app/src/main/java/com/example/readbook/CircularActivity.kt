package com.example.readbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast

class CircularActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circular)

        val buttonStart = findViewById<Button>(R.id.btn_progress)
        val CProgressBar = findViewById<ProgressBar>(R.id.progressbar)
        var iPos : Int = 10
        var iMaxValue : Int = 0

        iMaxValue = CProgressBar.max

        buttonStart.setOnClickListener{
            if( iMaxValue <= iPos)
            {
                CProgressBar.visibility = View.GONE
                Toast.makeText(this,"완료됨", Toast.LENGTH_SHORT).show()
                iPos = 0
                CProgressBar.setProgress(iPos)
            } else
            {
                if( 0 == iPos )
                { CProgressBar.visibility = View.VISIBLE
                }
                iPos += 10
                CProgressBar.setProgress(iPos) }
        }
    }
}