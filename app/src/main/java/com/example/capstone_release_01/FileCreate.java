package com.example.capstone_release_01;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileCreate extends AppCompatActivity {

    private EditText title;
    private EditText contents;
    private Button Save;
    private Button Back;
    private File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_create);

       contents = (EditText) findViewById(R.id.Contents);
       title = (EditText)findViewById(R.id.Title);
       Save = (Button)findViewById(R.id.Save);
       Back = (Button)findViewById(R.id.Back);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foldername = Environment.getExternalStorageDirectory().getAbsolutePath();
                foldername += "/Capstone";
                String filename = title.getText().toString() + ".txt";
                String path = foldername+"/"+filename;

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
                        onBackPressed();
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

}
