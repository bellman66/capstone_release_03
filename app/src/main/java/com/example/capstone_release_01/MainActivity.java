package com.example.capstone_release_01;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Activity mainActivity = this;

    private static final int REQUEST_EXTERNAL_STORAGE = 1111;
    private int permissioncheck_read ;
    private int permissioncheck_write;

    // API 요청을 분리하기 위한 요청 코드
    private static final int FILE_REQUEST_CODE = 2345;

    Button start;   // START 버튼
    Button GetList;     // 구현되지않음 리스트를 가져오는 버튼.
    ArrayList<String> file_text;    // 파일에서 오는 텍스트.
    TextView Select_file;

    // 파일에서 반환되는 list 를 받는값    구현되지않음
    List<String> Filelist;


    // 파일을 가져오는 엑티비티 구현
    FileList FL;

    // ON CREATE - 시작 부분.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_main.xml 화면 사용
        setContentView(R.layout.activity_main);

        setPermissioncheck();

        // xml 에서 R.id.~ 을 통해서 버튼 과 텍스트 뷰를 찾고 매칭.
        start = (Button) findViewById(R.id.start_reg);
        GetList = (Button) findViewById(R.id.get_list);
        Select_file = (TextView)findViewById(R.id.select_File);

        // filelist 객체
        FL = new FileList();
    }

    // ON RESUME - 어플이 진행되는 상태. =============================================================
    @Override
    protected void onResume() {
        super.onResume();

        // ===== GETLIST 클릭이 될때 실행되는 메소드. =====
        GetList.setOnClickListener(new View.OnClickListener() {

            // 클릭 실행 메소드.
            @Override
            public void onClick(View v) {
                /*
                if(!file_text.isEmpty()){
                    file_text.clear();
                }
                */


                // 리스트 클릭이 되는지 확인하는 메소드.
                Toast.makeText(getApplicationContext()," GET LIST 클릭 " , Toast.LENGTH_LONG).show();

                // INTENT - FILELIST 객체로 보내는 MESSAGE 제작.
                Intent intent = new Intent(getApplicationContext(),FileList.class);
                // INTENT 실행.
                // startActivity(intent);

                startActivityForResult(intent,FILE_REQUEST_CODE);

            }
        });

        // =====   API 요청 및 인터넷 연결 확인 메소드 =====
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Operator . class 로 보내는 intent 작성.
                Toast.makeText(getApplicationContext()," START 클릭 " , Toast.LENGTH_LONG).show();
                // INTENT
                Intent intent = new Intent(getApplicationContext(),Operator_API_FILE.class);
                intent.putStringArrayListExtra("Text", file_text);
                // INTENT 실행.
                startActivity(intent);
            }
        });
    }
    //==============================================================================================

    // =====   API 결과값 (startActivityForResult) 을 받는 구간   =====
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       // FileList 에서 받는 텍스트 종류.
       if(resultCode == RESULT_OK){
           switch (requestCode) {
               case FILE_REQUEST_CODE:
                    file_text = data.getStringArrayListExtra("result");
                    /*
                    String str;
                    for(int i =0 ; i < file_text.size() ; i++) {
                     str = (String) file_text.get(i);
                     Select_file.append(str);
                    }
                    */
                    Select_file.setText(file_text.get(1));
                    break;
           }
       }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 퍼미션 체크
    private void setPermissioncheck() {
        permissioncheck_read = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        permissioncheck_write = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissioncheck_read == PackageManager.PERMISSION_DENIED || permissioncheck_write == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getApplicationContext(),"read/write storage permission authorized" , Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"read/write storage permission denied" , Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
        }
    }
}
