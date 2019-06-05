package com.example.capstone_release_01;

import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.gson.Gson;
import com.google.protobuf.StringValue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class  Operator_API_FILE extends AppCompatActivity {

    private static final int REQUEST_CODE = 1234;
    private final int GOOGLE_STT = 1000, MY_UI=1001;

    // 다음으로 넘기는 Button
    Button next_button;
    Button end_button;
    Button cycle_button;
    // 보여지는 text
    TextView FILE_Text;
    TextView API_Text;

    // 버튼 클릭시 음성인식과 함께 텍스트를 한줄한줄 읽어주는 역할.
    // 해당 버튼 횟수는 텍스트 스트링으로 쪼개고 그 전체 횟수를 가져옴. 구현되지않음/
    int clickcount;

    ArrayList<String> matches_text; // API 에서 받는 리스트 텍스트
    ArrayList<String> Intent_text; // 파일 저장으로 받은 리스트 텍스트.
    ArrayList<Integer> result;
    ArrayList<String> result_TEXT;
    ArrayList<List> result_keyword;

    String File_str;
    String title;
    Intent intent;
    Intent google_intent;

    ArrayList<String> entityList;
    private int mode;
    private int check;
    Handler handler;

    Context mCtxContext;
    String[] words;

    // 초기 지정 - oncreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator__api__file);
        // 실험중 ...
        try {
            // analyzeEntitiesFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 버튼 , 텍스트 매핑.
        next_button = (Button) findViewById(R.id.next_button);
        end_button = (Button) findViewById(R.id.end_button);
        cycle_button = (Button) findViewById(R.id.cycle_button);
        FILE_Text = (TextView) findViewById(R.id.File_Text);
        API_Text = (TextView) findViewById(R.id.API_Text);

        result = new ArrayList<>();
        result_TEXT = new ArrayList<>();
        result_keyword = new ArrayList<>();
        Intent_text = new ArrayList<>();
        entityList = new ArrayList<>();

        // Main 에서 주는 intent 를 받음.
        intent = getIntent();

        clickcount = -1;
        check = 1;

        if(!result.isEmpty()){
            result.clear();
        }
        if(!result_keyword.isEmpty())
        {
            result_keyword.clear();
        }

        Set_FILE_Text();

    }

    // Intent 로 부터 받은 텍스트 설정.
    private void Set_FILE_Text() {
        File_str = intent.getStringExtra("File_str");
        title = intent.getStringExtra("title");
        mode = intent.getIntExtra("mode",0);
        mCtxContext = Operator_API_FILE.this;
        check = 0;

        if(!Intent_text.isEmpty()) {
            Intent_text.clear();
            entityList.clear();
        }

        // . 단위로 스플릿해서 words 에 넣음
        words = File_str.split("[.]");

        // Intent_text 변수에 . 단위로 잘린 word를 넣음.
        Intent_text.addAll(Arrays.asList(words));

        //Toast.makeText(getApplicationContext(),Intent_text.get(0),Toast.LENGTH_LONG).show();

        try {
            entityList = analyzeEntitiesFile2();
            Toast.makeText(getApplicationContext(),entityList.get(0),Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ==============================================================================================
    // 작동부.
    @Override
    protected void onResume() {
        super.onResume();

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 인터넷 연결시.
                if (Connect()) {
                    // 요청을 확인하는 카운팅.

                    clickcount++;

                    if (mode == 0) {
                        startmode1();
                    }
                    else if (mode == 1) {
                        startmode2();
                    }
                    else if (mode == 2) {
                        startmode3();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "잘못된 모드 선택", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                // 인터넷 연결 X 경우
                else {
                    // 연결 요망 토스트 메세지.
                    Toast.makeText(getApplicationContext(), "인터넷 연결 요망", Toast.LENGTH_LONG).show();
                }
            }
        });

        cycle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 인터넷 연결시.
                if (Connect()) {
                    // 요청을 확인하는 카운팅.

                    check = 1;

                    if (mode == 0) {
                        startmode1();
                    }
                    else if (mode == 1) {
                        startmode2();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "잘못된 모드 선택", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                // 인터넷 연결 X 경우
                else {
                    // 연결 요망 토스트 메세지.
                    Toast.makeText(getApplicationContext(), "인터넷 연결 요망", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 중간 종료 버튼.
        end_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result.isEmpty()){
                    finish();
                }
                else {
                    Intent intent_result = new Intent(getApplicationContext(), result.class);
                    intent_result.putIntegerArrayListExtra("result" , result);
                    intent_result.putStringArrayListExtra("result_TEXT" , result_TEXT);
                    intent_result.putStringArrayListExtra("Intent_text" , Intent_text);
                    intent_result.putStringArrayListExtra("entityList" , entityList);
                    intent_result.putExtra("title",title);
                    // INTENT 실행.
                    startActivity(intent_result);
                }
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){   // Message id 가 0 이면
                    Toast.makeText(getApplicationContext(),String.valueOf(clickcount),Toast.LENGTH_LONG).show();
                }
                else {
                    FILE_Text.setText(Intent_text.get(clickcount));
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 요청코드(REQUEST_CODE)를 통해서 구분.
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (mode == 0) {
                API_Text.setText("연속 말하기 모드");
            }
            else if (mode == 2) {
                API_Text.setText("실전 말하기 모드");
            }
            else {
                API_Text.setText(matches_text.get(0));
            }

            int shame = getDistance(matches_text.get(0), Intent_text.get(clickcount));
            int result_int = ((matches_text.get(0).length() - shame) * 100) / matches_text.get(0).length();
            if (result_int < 0) {
                result_int = 0;
            } else if (result_int > 100) {
                result_int = 100;
            }

            if(check == 0 ) {
                result.add(result_int);
                result_TEXT.add(matches_text.get(0));
            }
            else if (check == 1){
                Toast.makeText(getApplicationContext(),"연습 정확도 : " + String.valueOf(result_int),Toast.LENGTH_LONG).show();

                check = 0;
            };

            if (mode == 0 || mode ==2) {

                clickcount++;
                if ( (clickcount) < (Intent_text.size()-1) || (Intent_text.size()-1) == 1 ) {
                    if(clickcount < 0 || clickcount > Intent_text.size()){
                        Toast.makeText(getApplicationContext(),"검사 종료가 되었습니다",Toast.LENGTH_LONG).show();
                        return ;
                    }
                    // 한줄식 세팅되는 텍스트
                    if(mode == 0) {
                        FILE_Text.setText(Intent_text.get(clickcount));
                    }
                    // 변경 구문 -
                    else if(mode == 2) {
                        String[] word_first_end = Intent_text.get(clickcount).trim().split("\\s+");
                        // __ 으로 빈칸 처리
                        String str = new String();
                        for (int i = 0 ; i < word_first_end.length ; i++){
                            if( i == 0){
                                str = word_first_end[i] ;
                            }
                            else if ( i == word_first_end.length -1){
                                str += "  ";
                                str += word_first_end[i];
                            }
                            else {
                                str += "  ";
                                for(int j = 0 ; j < word_first_end[i].length() ; j++){
                                    str += "_";
                                }
                            }
                        }
                        // FILE_Text.setText(word_first_end[0] + " ~ " + word_first_end[word_first_end.length - 1]);
                        FILE_Text.setText(str);
                    }

                    // INTENT - 구글 API 서버로 요청하는 INTENT
                    // 있을 경우 초기화.


                    google_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                    // INTENT에 넣음 - 언어 모델 선택 ( 현재 - FREE )
                    google_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    google_intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"예제 " + String.valueOf(clickcount+1) + "번");
                    // 인텐트 + RESQUEST_CODE 와 함께 실행 및 결과 받음.
                    startActivityForResult(google_intent , REQUEST_CODE);
                }
                else if( (clickcount) >= (Intent_text.size()-1))
                {
                    showmessage();
                }
                else {
                    Toast.makeText(getApplicationContext(),"잘못된 카운트" ,Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    // ==============================================================================================
    // 연결확인.
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

    // 거리유사도 비교 .
    // =============================== 거리유사도와 유사도 비교하는 부분 개선해야함.

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

    public void showmessage() {
        // 잘못된 오입력 필터.
        if((words.length-1) == result.size()) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("안내");
        builder.setMessage("발표가 완료되었습니다 \n결과를 확인하시겠습니까?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent_result = new Intent(getApplicationContext(), result.class);
                    intent_result.putIntegerArrayListExtra("result", result);
                    intent_result.putStringArrayListExtra("result_TEXT", result_TEXT);
                    intent_result.putStringArrayListExtra("Intent_text", Intent_text);
                    intent_result.putStringArrayListExtra("entityList", entityList);
                    intent_result.putExtra("count", words.length-1 );
                    intent_result.putExtra("title", title);
                    // INTENT 실행.
                    startActivity(intent_result);

                }
            });

            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "발표 마무리", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else {
            Toast.makeText(getApplicationContext(), "잘못된 입력입니다 . \n 다시 실행해주세요.", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    public ArrayList<String> analyzeEntitiesFile2() throws Exception { // 키워드 추출 (추가)

        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            Toast.makeText(getApplicationContext(),String.valueOf(2),Toast.LENGTH_LONG).show();
            Document doc2 = Document.newBuilder().setContent(File_str).setType(Type.PLAIN_TEXT).build();
            ArrayList<String> entityList2 = new ArrayList<>();
            entityList2.clear();

            AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder().setDocument(doc2).setEncodingType(EncodingType.UTF16).build();

            AnalyzeEntitiesResponse response = language.analyzeEntities(request);

            for(Entity entity : response.getEntitiesList()) {
                entityList2.add(entity.getName());
            }

            Toast.makeText(getApplicationContext(),entityList2.get(0) , Toast.LENGTH_LONG).show();

            return entityList2;
        }
    }


    private void startmode1(){

        FILE_Text.setText(Intent_text.get(clickcount));

        google_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // INTENT에 넣음 - 언어 모델 선택 ( 현재 - FREE )
        google_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        google_intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"예제 " + String.valueOf(clickcount+1) + "번");
        // 인텐트 + RESQUEST_CODE 와 함께 실행 및 결과 받음.
        startActivityForResult(google_intent , REQUEST_CODE);

    }

    private void startmode2() {

        if ( (clickcount) < (Intent_text.size()-1) || (Intent_text.size()-1) == 1 ) {
            if(clickcount < 0 || clickcount > Intent_text.size()){
                Toast.makeText(getApplicationContext(),"검사 종료가 되었습니다",Toast.LENGTH_LONG).show();
                return ;
            }
            // 한줄식 세팅되는 텍스트
            FILE_Text.setText(Intent_text.get(clickcount));

            // INTENT - 구글 API 서버로 요청하는 INTENT
            // 있을 경우 초기화.


            google_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            // INTENT에 넣음 - 언어 모델 선택 ( 현재 - FREE )
            google_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            google_intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"예제 " + String.valueOf(clickcount+1) + "번");
            // 인텐트 + RESQUEST_CODE 와 함께 실행 및 결과 받음.
            startActivityForResult(google_intent , REQUEST_CODE);
        }
        else if( (clickcount) >= (Intent_text.size()-1))
        {
            showmessage();
        }
        else {
            Toast.makeText(getApplicationContext(),"잘못된 카운트" ,Toast.LENGTH_LONG).show();
        }
    }

    private void startmode3(){
        String[] word_first_end = Intent_text.get(clickcount).trim().split("\\s+");
        String str = new String();
        for (int i = 0 ; i < word_first_end.length ; i++){
            if( i == 0){
                str = word_first_end[i] ;
            }
            else if ( i == word_first_end.length -1){
                str += "  ";
                str += word_first_end[i];
            }
            else {
                str += "  ";
                for(int j = 0 ; j < word_first_end[i].length() ; j++){
                    str += "_";
                }
            }
        }
        // FILE_Text.setText(word_first_end[0] + " ~ " + word_first_end[word_first_end.length - 1]);
        FILE_Text.setText(str);

        google_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // INTENT에 넣음 - 언어 모델 선택 ( 현재 - FREE )
        google_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        google_intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"예제 " + String.valueOf(clickcount+1) + "번");
        // 인텐트 + RESQUEST_CODE 와 함께 실행 및 결과 받음.
        startActivityForResult(google_intent , REQUEST_CODE);

    }

    // 실험 중 ...
    public void analyzeEntitiesFile() throws Exception { // 키워드 추출 (추가)

        class Morpheme {
            final String text;
            final String type;
            Integer count;

            public Morpheme(String text, String type, Integer count) {
                this.text = text;
                this.type = type;
                this.count = count;
            }
        }

        class NameEntity {
            final String text;
            final String type;
            Integer count;

            public NameEntity(String text, String type, Integer count) {
                this.text = text;
                this.type = type;
                this.count = count;
            }
        }

        String openApiURL = "http://aiopen.etri.re.kr:8000/WiseNLU";
        String accessKey = "7d090783-9979-4cef-afd3-66176f0e2938";   // 발급받은 API Key
        String analysisCode = "ner";        // 언어 분석 코드
        String text = File_str;           // 분석할 텍스트 데이터
        Gson gson = new Gson();

        Map<String, Object> request = new HashMap<>();
        Map<String, String> argument = new HashMap<>();

        argument.put("analysis_code", analysisCode);
        argument.put("text", text);

        request.put("access_key", accessKey);
        request.put("argument", argument);

        URL url;
        Integer responseCode = null;
        String responBodyJson = null;
        Map<String, Object> responeBody = null;
        try {
            url = new URL(openApiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream()); // <-- 이것부터 실행안돼...

            wr.write(gson.toJson(request).getBytes("UTF-8"));
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();

            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            responBodyJson = sb.toString();

            // http 요청 오류 시 처리
            if (responseCode != 200) {
                // 오류 내용 출력
                System.out.println("[error] " + responBodyJson);
                return;
            }

            responeBody = gson.fromJson(responBodyJson, Map.class);
            Integer result = ((Double) responeBody.get("result")).intValue();
            Map<String, Object> returnObject;
            List<Map> sentences;

            // 분석 요청 오류 시 처리
            if (result != 0) {

                // 오류 내용 출력
                System.out.println("[error] " + responeBody.get("result"));
                return;
            }

            // 분석 결과 활용
            returnObject = (Map<String, Object>) responeBody.get("return_object");
            sentences = (List<Map>) returnObject.get("sentence");

            Map<String, Morpheme> morphemesMap = new HashMap<String, Morpheme>();
            Map<String, NameEntity> nameEntitiesMap = new HashMap<String, NameEntity>();
            List<Morpheme> morphemes = null;
            List<NameEntity> nameEntities = null;

            for (Map<String, Object> sentence : sentences) {

                // 개체명 분석 결과 수집 및 정렬
                List<Map<String, Object>> nameEntityRecognitionResult = (List<Map<String, Object>>) sentence.get("NE");
                for (Map<String, Object> nameEntityInfo : nameEntityRecognitionResult) {
                    String name = (String) nameEntityInfo.get("text");
                    NameEntity nameEntity = nameEntitiesMap.get(name);
                    if (nameEntity == null) {
                        nameEntity = new NameEntity(name, (String) nameEntityInfo.get("type"), 1);
                        nameEntitiesMap.put(name, nameEntity);
                    } else {
                        nameEntity.count = nameEntity.count + 1;
                    }
                }
            }

            if (0 < nameEntitiesMap.size()) {
                nameEntities = new ArrayList<NameEntity>(nameEntitiesMap.values());
                nameEntities.sort((nameEntity1, nameEntity2) -> {
                    return nameEntity2.count - nameEntity1.count;
                });
            }

            // 인식된 개채명들 많이 노출된 순으로 출력 ( 최대 3개 )
            System.out.println("");
            nameEntities
                    .stream()
                    .limit(3)
                    .forEach(nameEntity -> {
                        Toast.makeText(getApplicationContext(), nameEntity.text, Toast.LENGTH_LONG).show();
                    });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


};



