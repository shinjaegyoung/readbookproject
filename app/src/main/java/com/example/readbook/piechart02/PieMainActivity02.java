package com.example.readbook.piechart02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readbook.R;
import com.github.mikephil.charting.charts.BarChart;

public class PieMainActivity02 extends AppCompatActivity {
    Button btnBarChart, btnPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_main02);
        BarChart barChart = (BarChart) findViewById(R.id.barchart);

        btnBarChart = findViewById(R.id.btnBarChart);
        btnPieChart = findViewById(R.id.btnPieChart);
        btnBarChart.setOnClickListener(view -> {
            Intent I = new Intent(PieMainActivity02.this, BarChartActivity.class);
            startActivity(I);
        });
        btnPieChart.setOnClickListener(view -> {
            Intent I = new Intent(PieMainActivity02.this, PiechartActivity.class);
            startActivity(I);
        });


    }
}