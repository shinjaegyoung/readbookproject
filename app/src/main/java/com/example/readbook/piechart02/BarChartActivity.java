package com.example.readbook.piechart02;

import androidx.appcompat.app.AppCompatActivity;


import com.example.readbook.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import android.os.Bundle;

import java.util.ArrayList;

public class BarChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        BarChart chart = findViewById(R.id.barchart);

        ArrayList NoOfEmp = new ArrayList();
        NoOfEmp.add(new BarEntry(500f, 0));
        NoOfEmp.add(new BarEntry(1040f, 1));
        NoOfEmp.add(new BarEntry(900f, 2));
        NoOfEmp.add(new BarEntry(1300f, 3));
        NoOfEmp.add(new BarEntry(800f, 4));


        ArrayList user = new ArrayList();

        user.add("user1");
        user.add("user2");
        user.add("user3");
        user.add("user4");
        user.add("user5");

        BarDataSet bardataSet = new BarDataSet(NoOfEmp, "No oF Employee");
        chart.animateY(5000);
        BarData data = new BarData(user, bardataSet);
        bardataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(data);
    }
}