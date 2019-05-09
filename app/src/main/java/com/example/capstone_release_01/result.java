package com.example.capstone_release_01;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class result extends AppCompatActivity {

    ArrayList<Integer> result_int;
    LineChart lineChart;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Set_FILE_Text();
        lineChart = (LineChart) findViewById (R.id.chart);

        List<Entry> entries = new ArrayList<>();

        for(int i = 0 ; i < result_int.size();i++){
            // 좌표 값 지정.
            entries.add(new Entry(i,result_int.get(i)));
        }

        LineDataSet dataset = new LineDataSet(entries, "# of Calls");
        dataset.setLineWidth(2);
        dataset.setCircleRadius(6);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        dataset.setCircleColorHole(Color.BLUE);
        dataset.setDrawHorizontalHighlightIndicator(false);
        dataset.setDrawHighlightIndicators(false);
        dataset.setDrawValues(true);
        dataset.setDrawCircles(true); //선 둥글게 만들기
        dataset.setDrawFilled(true); //그래프 밑부분 색칠
        LineData lineData = new LineData(dataset);
        lineChart.setData(lineData);

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

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();

        // label 미사용 했음 아직.
        ArrayList<String> labels = new ArrayList<String>();
        for(int i = 0 ; i < result_int.size();i++){
            labels.add(String.valueOf(i));
        }

    }

    private void Set_FILE_Text() {
        result_int = intent.getIntegerArrayListExtra("result");
    }
}
