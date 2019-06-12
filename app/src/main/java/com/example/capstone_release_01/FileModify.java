package com.example.capstone_release_01;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileModify extends AppCompatActivity {

    private EditText title;
    private EditText contents;
    private Button Modi;
    private Button Back;
    private File dir;
    private int position;
    private String otitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_modify);

        Intent intent = getIntent();

        contents = (EditText) findViewById(R.id.Contents);
        title = (EditText)findViewById(R.id.Title);
        Modi = (Button)findViewById(R.id.Modi);
        Back = (Button)findViewById(R.id.Back);

        String ocontents = intent.getExtras().getString("File_str");
        contents.setText(ocontents);
        otitle = intent.getExtras().getString("title");
        title.setText(otitle);

        position = intent.getExtras().getInt("pos");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Modi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foldername = Environment.getExternalStorageDirectory().getAbsolutePath();
                foldername += "/Capstone";
                String filename = title.getText().toString() + ".txt";
                String path = foldername+"/"+filename;

                File ofile = new File(foldername, otitle+".txt");

                if (ofile.exists()) { //파일존재여부확인
                    ofile.delete();
                    showMsg("파일삭제 성공");
                } else {
                    showMsg("파일이 존재하지 않습니다.");
                }

                try{
                    dir = new File (foldername);
                    //디렉토리 폴더가 없으면 생성함
                    if(!dir.exists()){
                        dir.mkdir();
                    }
                    dir = new File(path);

                    if(dir.exists()){
                        Toast.makeText(getApplicationContext()," 파일이 중복됩니다. " , Toast.LENGTH_LONG).show();
                        Log.d("capstone", "Failed to create directory");
                        return;
                    }
                    else {

                        FileOutputStream fos = new FileOutputStream(path, true);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos,"MS949");
                        BufferedWriter writer = new BufferedWriter(outputStreamWriter);
                        writer.write(contents.getText().toString() + "\n");
                        writer.flush();

                        writer.close();
                        fos.close();

                        Toast.makeText(getApplicationContext()," 파일생성 완료" , Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}