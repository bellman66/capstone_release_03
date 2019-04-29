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

    private static final int REQUEST_CODE = 1234;

    Button start;
    Button GetList;     //구현되지않음/
    TextView speech;
    Dialog match_text_dialog;
    ListView textlist;
    ArrayList<String> matches_text;

    // 파일에서 반환되는 list 를 받는값;    구현되지않음/
    List<String> Filelist;

    // 버튼 클릭시 음성인식과 함께 텍스트를 한줄한줄 읽어주는 역할.
    // 해당 버튼 횟수는 텍스트 스트링으로 쪼개고 그 전체 횟수를 가져옴. 구현되지않음/
    private int clickcount;

    // 파일을 가져오는 엑티비티 구현
    FileList FL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 파일이 파싱되는 값을 가져와야함 즉 list의 최대값 수.
        clickcount = -1;
        start = (Button) findViewById(R.id.start_reg);
        GetList = (Button) findViewById(R.id.get_list);
        speech = (TextView) findViewById(R.id.speech);
        FL = new FileList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        GetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 여기서 리스트를 받아야함
                // 파일안에 내용을 띄우는 줄   구현 x

                //############################ 폴더 구현중 마무리.

                Toast.makeText(getApplicationContext()," 클릭 " , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),FileList.class);
                startActivity(intent);

                //  FL.Get_list();
                //  Filelist = FL.getSelected_file();
                // clickcount = Filelist.size();
                // 파일안에 내용을 눌러서 list를 반환하는 줄.   구현 x
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connect()){
                    clickcount++;

                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent , REQUEST_CODE);
                }

                else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결 요망", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean Connect() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm .getActiveNetworkInfo();

        if(net != null && net.isAvailable() && net.isConnected())
            return true;
        else
            return  false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){

           match_text_dialog = new Dialog(MainActivity.this);
           match_text_dialog.setContentView(R.layout.dialog_matches_frag);
           match_text_dialog.setTitle("매칭되는 텍스트 선택");

           textlist = (ListView) match_text_dialog.findViewById(R.id.list);

           // 일치되는 텍스트 결과값 도출
           matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
           ArrayAdapter<String> adapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1,matches_text);

           textlist.setAdapter(adapter);
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
