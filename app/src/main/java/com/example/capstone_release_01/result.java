package com.example.capstone_release_01;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.lang.String;
import java.util.ArrayList;


public class result extends AppCompatActivity {

    TextView set_result;

    ArrayList<Integer> result_int;
    LineChart lineChart;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        set_result = (TextView) findViewById(R.id.set_result);

        Set_FILE_Text();

        StartChart();

    }

    private void Set_FILE_Text() {
        intent = getIntent();
        result_int = intent.getIntegerArrayListExtra("result");
    }


    private void StartChart() {

        // 라인 차트 시작.
        lineChart = (LineChart) findViewById (R.id.chart);
        lineChart.invalidate();
        lineChart.clear();

        // 내용이 담겨지는 구간.
        // 1번문항 ,, n번 문항 , 각 문항의 정확도가 담김.

        ArrayList<String> xAXES = new ArrayList<>();
        ArrayList<Entry> yAXES = new ArrayList<>();

        for(int i = 0 ; i < result_int.size();i++){
            // 좌표 값 지정.
            float yAXE = Float.parseFloat(String.valueOf(result_int.get(i)));

            yAXES.add(new Entry(yAXE,i));
            xAXES.add(i , String.valueOf(i+1)+"번 문항");

        }

        String[] xaxes = new String[xAXES.size()];
        for(int i=0; i<xAXES.size(); i++){
            xaxes[i] = xAXES.get(i).toString(); // 아래그림의 동그란 부분에 표시되는 x축 값.
        }

        LineDataSet lineDataSet1 = new LineDataSet(yAXES, "corret");
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setLineWidth(2);
        lineDataSet1.setCircleRadius(6);
        lineDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);

        lineChart.setData(new LineData(xaxes,lineDataSet1));
        lineChart.setVisibleXRangeMaximum(65f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(true);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();

    }

}

