package com.example.capstone_release_01;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Operator_API_FILE extends AppCompatActivity {

    private static final int REQUEST_CODE = 1234;
    // 다음으로 넘기는 Button
    Button next_text;
    // 보여지는 text
    TextView FILE_Text;
    TextView API_Text;

    // 버튼 클릭시 음성인식과 함께 텍스트를 한줄한줄 읽어주는 역할.
    // 해당 버튼 횟수는 텍스트 스트링으로 쪼개고 그 전체 횟수를 가져옴. 구현되지않음/
    private int clickcount;

    Dialog match_text_dialog;
    ListView textlist;
    ArrayList<String> matches_text; // API 에서 받는 리스트 텍스트
    ArrayList<String> Intent_text; // 파일 저장으로 받은 리스트 텍스트.
    Intent intent;

    // 초기 지정 - oncreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator__api__file);

        next_text = (Button) findViewById(R.id.next_button);
        FILE_Text = (TextView) findViewById(R.id.File_Text);
        API_Text = (TextView) findViewById(R.id.API_Text);

        Set_FILE_Text();
    }

    // Intent 로 부터 받은 텍스트 설정.
    private void Set_FILE_Text() {
        Intent_text = intent.getStringArrayListExtra("Text");

        String str;
        for(int i =0 ; i < Intent_text.size() ; i++) {
            str = (String) Intent_text.get(i);
            FILE_Text.append(str);
        }
    }

    // ==============================================================================================
    // 작동부.
    @Override
    protected void onResume() {
        super.onResume();

        next_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connect()){
                    // 요청을 확인하는 카운팅.
                    clickcount++;

                    // INTENT - 구글 API 서버로 요청하는 INTENT
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    // INTENT에 넣음 - 언어 모델 선택 ( 현재 - FREE )
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    // 인텐트 + RESQUEST_CODE 와 함께 실행 및 결과 받음.
                    startActivityForResult(intent , REQUEST_CODE);
                }

                // 인터넷 연결 X 경우
                else {
                    // 연결 요망 토스트 메세지.
                    Toast.makeText(getApplicationContext(), "인터넷 연결 요망", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private boolean Connect() {
        // 시스템 서비스 확인 , CONNECT 확인.
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm .getActiveNetworkInfo();

        // 맞을 경우 TRUE 반환.   ( START BUTTON CLICK 사용 )
        if(net != null && net.isAvailable() && net.isConnected())
            return true;
        else
            return  false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 요청코드(REQUEST_CODE)를 통해서 구분.
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){

            // DIALOG 제작 - API 에서 받은 음성인식을
            match_text_dialog = new Dialog(Operator_API_FILE.this);
            // dialog_matches_frag.xml 화면 사용.
            match_text_dialog.setContentView(R.layout.dialog_matches_frag);
            // title 이름 지정.
            match_text_dialog.setTitle("매칭되는 텍스트 선택");

            // dialog_matches_frag.xml 화면 중 list 찾음.
            textlist = (ListView) match_text_dialog.findViewById(R.id.list);

            // API 에서 받은 DATA 를 ArrayList 형태로 가져옴.
            matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // Apapter 에 매칭.
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1,matches_text);

            // Apapter 실행.
            textlist.setAdapter(adapter);
            // 클릭된 값 도출.
            textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    API_Text.setText("말한 단어는 : " + matches_text.get(position));
                    match_text_dialog.hide();
                }
            });
            match_text_dialog.show();
        }
    }

    // ==============================================================================================
}
