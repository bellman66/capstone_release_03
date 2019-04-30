package com.example.capstone_release_01;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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
import org.w3c.dom.Text;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // API 요청을 분리하기 위한 요청 코드
    private static final int REQUEST_CODE = 1234;

    Button start;   // START 버튼
    Button GetList;     // 구현되지않음 리스트를 가져오는 버튼.
    TextView speech;        // SPEECH 텍스트 뷰
    Dialog match_text_dialog;   // API 에서 반환되는 값을 고르는 DIALOG
    ListView textlist;
    ArrayList<String> matches_text; // 매칭 되는 텍스트

    // 파일에서 반환되는 list 를 받는값    구현되지않음
    List<String> Filelist;

    // 버튼 클릭시 음성인식과 함께 텍스트를 한줄한줄 읽어주는 역할.
    // 해당 버튼 횟수는 텍스트 스트링으로 쪼개고 그 전체 횟수를 가져옴. 구현되지않음/
    private int clickcount;

    // 파일을 가져오는 엑티비티 구현
    FileList FL;

    // ON CREATE - 시작 부분.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_main.xml 화면 사용
        setContentView(R.layout.activity_main);

        // 음성인식 횟수를 카운팅 시킴.
        clickcount = -1;
        // xml 에서 R.id.~ 을 통해서 버튼 과 텍스트 뷰를 찾고 매칭.
        start = (Button) findViewById(R.id.start_reg);
        GetList = (Button) findViewById(R.id.get_list);
        speech = (TextView) findViewById(R.id.speech);

        // filelist 객체
        FL = new FileList();
    }

    // ON RESUME - 어플이 진행되는 상태.
    @Override
    protected void onResume() {
        super.onResume();

        // ===== GETLIST 클릭이 될때 실행되는 메소드. =====
        GetList.setOnClickListener(new View.OnClickListener() {

            // 클릭 실행 메소드.
            @Override
            public void onClick(View v) {
                // 리스트 클릭이 되는지 확인하는 메소드.
                Toast.makeText(getApplicationContext()," GET LIST 클릭 " , Toast.LENGTH_LONG).show();
                // INTENT - FILELIST 객체로 보내는 MESSAGE 제작.
                Intent intent = new Intent(getApplicationContext(),FileList.class);
                // INTENT 실행.
                startActivity(intent);

                // sstartActivityForResult(intent,REQUEST_CODE_SECOND) 로 전환 구상.

            }
        });

        // =====   API 요청 및 인터넷 연결 확인 메소드 =====
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 연결이 되는 경우
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

    // 인터넷 연결 확인 BOOLEAN 메소드
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

    // =====   API 결과값 (startActivityForResult) 을 받는 구간   =====
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // 요청코드(REQUEST_CODE)를 통해서 구분.
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){

            // DIALOG 제작 - API 에서 받은 음성인식을
           match_text_dialog = new Dialog(MainActivity.this);
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
                   speech.setText("말한 단어는 : " + matches_text.get(position));
                   match_text_dialog.hide();
               }
           });
           match_text_dialog.show();
       }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
