package com.example.capstone_release_01;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
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
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private Activity mainActivity = this;

    // API 요청을 분리하기 위한 요청 코드
    private static final int FILE_REQUEST_CODE = 2345;
    private static final int REQUEST_EXTERNAL_STORAGE = 1111;

    Button start;   // START 버튼
    Button GetList;     // 구현되지않음 리스트를 가져오는 버튼.]
    Button Create_file;
    String File_str;
    String title;
    TextView Select_file;


    // 파일을 가져오는 엑티비티 구현
    FileList FL;

    // ON CREATE - 시작 부분.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPermissioncheck();

        // xml 에서 R.id.~ 을 통해서 버튼 과 텍스트 뷰를 찾고 매칭.
        start = (Button) findViewById(R.id.start_reg);
        GetList = (Button) findViewById(R.id.get_list);
        Create_file = (Button) findViewById(R.id.create_file);
        Select_file = (TextView)findViewById(R.id.select_File);
        File_str = null;

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

                // INTENT - FILELIST 객체로 보내는 MESSAGE 제작.
                Intent intent = new Intent(getApplicationContext(),FileList.class);
                // 결과값 요청.
                startActivityForResult(intent,FILE_REQUEST_CODE);

            }
        });

        Create_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FileCreate.class);

                startActivity(intent);

            }
        });

        // =====   API 요청 및 인터넷 연결 확인 메소드 =====
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(File_str == null){
                    Toast.makeText(getApplicationContext()," 지정된 파일이 없습니다. " , Toast.LENGTH_LONG).show();
                }
                else {
                    // Operator . class 로 보내는 intent 작성.
                    Select_Mode();
                }
            }

        });
    }
    //==============================================================================================

    // =====   API 결과값 (startActivityForResult) 을 받는 구간   =====
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       // FileList 에서 받는 텍스트 종류.
       if(resultCode == RESULT_OK){
           if (requestCode == FILE_REQUEST_CODE) {// 정보를 받는 부분.
               File_str = data.getStringExtra("File_str");
               title = data.getStringExtra("title");

               Select_file.setText(title);
           }
       }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 퍼미션 체크
    private void setPermissioncheck() {
        int permissioncheck_read = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissioncheck_write = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissioncheck_read == PackageManager.PERMISSION_DENIED || permissioncheck_write == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "read/write storage permission authorized", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "read/write storage permission denied", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void Select_Mode() {
        final Dialog match_text_dialog = new Dialog(MainActivity.this);
        // dialog_matches_frag.xml 화면 사용.
        match_text_dialog.setContentView(R.layout.dialog_matches_frag);
        // title 이름 지정.
        match_text_dialog.setTitle("MODE 선택");

        // dialog_matches_frag.xml 화면 중 list 찾음.
        ListView textlist = (ListView) match_text_dialog.findViewById(R.id.list);

        final ArrayList<String> matches_text = new ArrayList<String>();
        matches_text.add("연속 말하기 모드");
        matches_text.add("반복 말하기 모드");

        //###########################################################################
        // Apapter 에 매칭.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1, matches_text);

        // Apapter 실행.
        textlist.setAdapter(adapter);

        // 설정된 다이어로그 보여줌
        match_text_dialog.show();

        textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 말하는 단어 매칭.

                    // INTENT
                    Intent intent = new Intent(getApplicationContext(), Operator_API_FILE.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("File_str", File_str);
                    bundle.putString("title",title);
                    bundle.putInt("mode",position);
                    intent.putExtras(bundle);

                     // INTENT 실행.
                     startActivity(intent);

                // 닫고 제거
                match_text_dialog.hide();
                match_text_dialog.dismiss();
            }
        });
    }
}
