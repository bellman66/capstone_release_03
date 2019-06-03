package com.example.capstone_release_01;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;


public class result extends AppCompatActivity {

    TextView set_result;
    TextView set_entityList;
    Spinner spinner;

    ArrayList<Integer> result_int;
    ArrayList<String> entityList;
    ArrayList<String> result_TEXT;
    ArrayList<String> Intent_text;

    HashMap<String,String> inputdata;
    ArrayList<HashMap<String,String>> data;

    LineChart lineChart;
    Intent intent;
    Intent intent2;
    ListView listView;

    String title;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Set_FILE_Text();
        Startsetresult2();
        StartChart();
        StartList();

        // Startkeyword();
    }

    @Override
    protected void onResume() {
        super.onResume();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    Toast.makeText(getApplicationContext(),"현재 페이지 입니다.",Toast.LENGTH_LONG).show();
                }
                else if (position == 1){
                    if(count == result_int.size()) {
                        intent2 = new Intent(result.this, result2.class);
                        intent2.putExtra("title", title);
                        intent2.putStringArrayListExtra("Intent_text", Intent_text);
                        intent2.putIntegerArrayListExtra("result_int", result_int);
                        startActivity(intent2);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"중간 종료로 인해 지원되지 않는 창",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    intent2 = new Intent(result.this,MainActivity.class);
                    startActivity(intent2);
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void Set_FILE_Text() {
        set_result = (TextView) findViewById(R.id.set_result);
        set_entityList = (TextView) findViewById(R.id.set_entityList);
        listView = (ListView) findViewById(R.id.list_view);
        spinner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.page,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        inputdata = new HashMap<>();
        data = new ArrayList<HashMap<String, String>>();

        intent2 = new Intent();
        intent = getIntent();
        result_int = intent.getIntegerArrayListExtra("result");
        entityList = intent.getStringArrayListExtra("entityList");
        result_TEXT = intent.getStringArrayListExtra("result_TEXT");
        Intent_text = intent.getStringArrayListExtra("Intent_text");
        title = intent.getStringExtra("title");
        count = (int) intent.getExtras().getInt("count");

        for(int i = 0 ; i < result_TEXT.size() ; i++){
            inputdata = new HashMap<>();
            inputdata.put("Intent_text" , String.valueOf(i+1)+"번 원문 : "+Intent_text.get(i));

            // 빈문자일 경우 실패를 입력.
            if(TextUtils.isEmpty(result_TEXT.get(i))){
                inputdata.put("result_TEXT" , String.valueOf(i+1)+"번 인식 : 실패");
            }
            else inputdata.put("result_TEXT" , String.valueOf(i+1)+"번 인식 : "+result_TEXT.get(i));

            data.add(inputdata);

        }
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

    private void Startkeyword() {
        set_entityList.setText(entityList.get(0));
    }

    private void StartList() {
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,data,android.R.layout.simple_list_item_2,new String[]{"Intent_text","result_TEXT"},new int[]{android.R.id.text1,android.R.id.text2});

        listView.setAdapter(simpleAdapter);
    }

    private void Startsetresult2(){
        String foldername2 = Environment.getExternalStorageDirectory().getAbsolutePath();
        foldername2 += "/Capstone_Result";
        String filename = title + ".txt";
        String path = foldername2 +"/"+filename;

        try{
            File dir = new File(foldername2);
            //디렉토리 폴더가 없으면 생성함

            if(!dir.exists()){
                dir.mkdir();
            }

            dir = new File(path);


                FileOutputStream fos = new FileOutputStream(path, true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos,"MS949");
                BufferedWriter writer = new BufferedWriter(outputStreamWriter);
                writer.write(String.valueOf(result_int) + "\n");
                writer.flush();

                writer.close();
                fos.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}

