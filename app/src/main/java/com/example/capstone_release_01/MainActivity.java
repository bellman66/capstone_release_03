package com.example.capstone_release_01;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1234;

    Button start;
    TextView speech;
    Dialog match_text_dialog;
    ListView textlist;
    ArrayList<String> matches_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.start_reg);
        speech = (TextView) findViewById(R.id.speech);
        
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connect()){
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
