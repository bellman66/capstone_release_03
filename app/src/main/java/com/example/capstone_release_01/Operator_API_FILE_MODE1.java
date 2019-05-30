package com.example.capstone_release_01;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Operator_API_FILE_MODE1 extends AppCompatActivity {

    private static final int REQUEST_CODE = 1234;

    TextView FILE_Text;
    TextView API_Text;
    ArrayList<String> Intent_text;
    ArrayList<String> matches_text;
    ArrayList<String> entityList;
    ArrayList<Integer> result;
    Intent google_intent;
    Intent intent;

    int clickcount;
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator__api__file__mode1);

        FILE_Text = (TextView) findViewById(R.id.File_Text);
        API_Text = (TextView) findViewById(R.id.API_Text);

        result = new ArrayList<>();
        Intent_text = new ArrayList<>();
        entityList = new ArrayList<>();

        // Main 에서 주는 intent 를 받음.
        intent = getIntent();
        google_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        clickcount = -1;

        Handler handler = new Handler();

        //==========================================================================================

        FILE_Text.setText(Intent_text.get(clickcount));


        for (int i = 0 ; i < Intent_text.size() ; i++) {
            clickcount++;

            if(!google_intent.getExtras().isEmpty()){
                google_intent.getExtras().clear();
            }

            // INTENT에 넣음 - 언어 모델 선택 ( 현재 - FREE )
            google_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // 인텐트 + RESQUEST_CODE 와 함께 실행 및 결과 받음.
            startActivityForResult(google_intent, REQUEST_CODE);

        }

        showmessage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 요청코드(REQUEST_CODE)를 통해서 구분.
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){

            matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            API_Text.setText(matches_text.get(0));

            int shame = getDistance(matches_text.get(0),Intent_text.get(clickcount));
            int result_int = ((matches_text.get(0).length()-shame) * 100) / matches_text.get(0).length() ;
            if(result_int  < 0){
                result_int = 0;
            }
            else if(result_int  > 100){
                result_int = 100;
            }
            result.add(result_int);
            Toast.makeText(getApplicationContext(),"발음 정확도 : " + result_int , Toast.LENGTH_LONG).show();

            // 연속 읽기 경우
            if(mode == 1)     {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void showmessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("안내");
        builder.setMessage("발표가 완료되었습니다 \n결과를 확인하시겠습니까?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent_result = new Intent(getApplicationContext(),result.class);
                intent_result.putIntegerArrayListExtra("result" , result);
                intent_result.putStringArrayListExtra("entityList" , entityList);
                // INTENT 실행.
                startActivity(intent_result);

            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"발표 마무리",Toast.LENGTH_LONG).show();
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static int getDistance(String s1, String s2) {
        int longStrLen = s1.length() + 1;
        int shortStrLen = s2.length() + 1; // 긴 단어 만큼 크기가 나올 것이므로, 가장 긴단어 에 맞춰 Cost를 계산
        int[] cost = new int[longStrLen];
        int[] newcost = new int[longStrLen]; // 초기 비용을 가장 긴 배열에 맞춰서 초기화 시킨다.
        for (int i = 0; i < longStrLen; i++) { cost[i] = i; } // 짧은 배열을 한바퀴 돈다.
        for (int j = 1; j < shortStrLen; j++) {
            // 초기 Cost는 1, 2, 3, 4...
            newcost[0] = j; // 긴 배열을 한바퀴 돈다.
            for (int i = 1; i < longStrLen; i++) {
                // 원소가 같으면 0, 아니면 1
                int match = 0;
                if (s1.charAt(i - 1) != s2.charAt(j - 1)) { match = 1; }
                // 대체, 삽입, 삭제의 비용을 계산한다.
                int replace = cost[i - 1] + match;
                int insert = cost[i] + 1;
                int delete = newcost[i - 1] + 1;
                // 가장 작은 값을 비용에 넣는다.
                newcost[i] = Math.min(Math.min(insert, delete), replace);
            }
            // 기존 코스트 & 새 코스트 스위칭
            int[] temp = cost;
            cost = newcost;
            newcost = temp;
        }
        // 가장 마지막값 리턴
        return cost[longStrLen - 1];
    }
}
