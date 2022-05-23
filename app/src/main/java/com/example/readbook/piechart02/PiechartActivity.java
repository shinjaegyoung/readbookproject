package com.example.readbook.piechart02;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readbook.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class PiechartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);
        PieChart pieChart = findViewById(R.id.piechart);
        ArrayList NoOfEmp = new ArrayList();

        NoOfEmp.add(new BarEntry(1f, 0));
//        NoOfEmp.add(new BarEntry(10f, 1));
        NoOfEmp.add(new BarEntry(10f, 1));



        PieDataSet dataSet = new PieDataSet(NoOfEmp,"No oF Employee");
        ArrayList user = new ArrayList();

        user.add(" ");
        user.add("10권 목표!");

        PieData data = new PieData(user, dataSet);
        data.setValueTextSize(20);
        pieChart.setCenterText("신동률 독서량");
        pieChart.setCenterTextSize(20);

        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(5000,5000);


    }
}