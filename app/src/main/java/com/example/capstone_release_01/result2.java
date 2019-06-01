package com.example.capstone_release_01;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class result2 extends AppCompatActivity {

    // 각 회차별 평균 값
    ArrayList<Integer> result_int;
    // 각 단원별 값.
    ArrayList<Integer> result_int_2;
    // 문장별 정확도
    ArrayList<Integer> result_int_3;
    ArrayList<String> Intent_text;


    LineChart lineChart;
    File result_file;
    String title;
    Spinner spinner;

    Intent intent;
    ListView listView;
    HashMap<String,String> inputdata;
    ArrayList<HashMap<String,String>> data;
    int length;
    int mCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);

        Set_FILE_Text();
        StartChart();

        StartSettingList();
        StartList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    if(mCount++==0)
                        return;
                    onBackPressed();
                }
                else if (position == 1) {
                    Toast.makeText(getApplicationContext(),"현재 페이지 입니다.",Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent2 = new Intent(result2.this,MainActivity.class);
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
        result_int = new ArrayList<>();
        result_int_2 = new ArrayList<>();
        result_int_3 = new ArrayList<>();
        intent = getIntent();
        title = String.valueOf(intent.getStringExtra("title"));        // 텍스트 이름가져옴.
        Intent_text = intent.getStringArrayListExtra("Intent_text");
        result_int_3 = intent.getIntegerArrayListExtra("result_int");
        listView = (ListView) findViewById(R.id.list_view);

        mCount = 0;
        inputdata = new HashMap<>();
        data = new ArrayList<HashMap<String, String>>();
        spinner = (Spinner) findViewById (R.id.spinner1);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.page,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 차트에 넣을 result_int 지정
        // 각 부분에 들어가는 int는 __평균값__ 으로 지정.
        SetArray();

    }

    // 차트 손안뎀 지정해줘야함.
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
            xAXES.add(i , String.valueOf(i+1)+"번 회차");

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

    private void SetArray() {
        String pathname = String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Capstone_Result");
        String filename = title + ".txt";

        // 위치 지정.
        String path = String.valueOf(pathname+"/"+filename);

        File dir = new File(pathname);
        File file = new File(pathname , filename) ;

        if(!file.exists()){
            Toast.makeText(this, "file not found", Toast.LENGTH_SHORT).show();
        }

        StringBuffer strBuffer = new StringBuffer();

        try{

            FileInputStream is = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(is,"MS949");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line="";
            while((line=reader.readLine())!=null){
                strBuffer.append(line+"\n");
            }

            reader.close();
            is.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        // 받은 스트링 버퍼를 이용
        String[] words = strBuffer.toString().replace("[","").replace("]","").split("\n");

        // 현재 words[0] 경우 0,40,0 이렇게 , 이 포함된 상황.
        for (int i = 0 ; i < words.length ; i ++){
           String[] str = words[i].split(",");
           length = str.length;
           int sum = 0;

           // 계산되는 for문 .
           for(int j = 0 ; j < str.length ; j ++){
               sum += Integer.parseInt(str[j].replace(" ",""));

               if (i == 0){
                   result_int_2.add(Integer.parseInt(str[j].replace(" ","")));
               }
               else {
                   // 각 구문별 정확도 추측.
                   result_int_2.set( j , result_int_2.get(j) + Integer.parseInt(str[j].replace(" ","")));
               }
           }

           // 회차별 평균값을 도출해냄 ( 차트에 쓰일 예정 )
           result_int.add(sum/str.length);
        }
        for(int k = 0 ; k < length ; k ++){
            // 구문 별 평균값.
            result_int_2.set( k , result_int_2.get(k) / words.length);
        }

    }

    private void StartSettingList(){
        for(int i = 0 ; i < result_int_2.size() ; i++){
            inputdata = new HashMap<>();

            inputdata.put("Intent_text" , String.valueOf(i+1)+"번 문항 : " + Intent_text.get(i));

            // 빈문자일 경우 실패를 입력.
            if(TextUtils.isEmpty(String.valueOf(result_int_2.get(i)))){
                inputdata.put("result_TEXT" , String.valueOf(i+1)+"번 평균 정확도 : 실패");
            }
            else inputdata.put("result_TEXT" ,String.valueOf(i+1)+"번 문항 정확도 : " +String.valueOf(result_int_3.get(i))+" / 평균 정확도 : "+ String.valueOf(result_int_2.get(i)));

            data.add(inputdata);

        }
    }
    private void StartList() {
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,data,android.R.layout.simple_list_item_2,new String[]{"Intent_text","result_TEXT"},new int[]{android.R.id.text1,android.R.id.text2});

        listView.setAdapter(simpleAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
