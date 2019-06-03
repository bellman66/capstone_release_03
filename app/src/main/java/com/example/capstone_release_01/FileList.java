package com.example.capstone_release_01;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


// 파일 리스트 관리 - 리스트 엑티비티.
public class FileList extends AppCompatActivity {

    private ArrayList<String> mylist;   // file 내 list.
    private ArrayList<String> list2;
    private ListView list1;
    private File selected_file; // 선택되는 file.

    private File dir;
    private File file;
    private File file2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        // 객체 구현.
        mylist = new ArrayList<String>();
        list1 = (ListView)findViewById(R.id.list) ;
        //####################### 이부분을 지정하자.

        // 파일내 list 를 가져옴.
        setlistfiles();

        File list[] = dir.listFiles();

        // list의 이름을 가져옴. + add
        for(int i =0 ; i< list.length ; i++) {
            mylist.add(list[i].getName());
        }

        // list1 = this.getListView();

        // Adapter 생성 , list view 로 매칭.
        final ArrayAdapter<String> Adapter = new ArrayAdapter<String> (this,
                android.R.layout.simple_list_item_1 , mylist);

        //setListAdapter(Adapter);
       // getListView().setTextFilterEnabled(true);

        list1.setAdapter(Adapter);

        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selected_file = new File(dir , mylist.get(position));

                // 파일 내용 저장 - 파일 내용 변수 str
                String File_str = File_READ2();

                Intent resultintent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("File_str", File_str );
                bundle.putString("title",selected_file.getName());

                // some value 안에 원하는 평서문을 넣어줌. 그럼보내게됨.
                resultintent.putExtras(bundle);
                setResult(RESULT_OK , resultintent);
                finish();

            }
        });


    }

    //===============================================================================================
    //===============================================================================================


    /*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        selected_file = new File(dir , mylist.get(position));

        // 파일 내용 저장 - 파일 내용 변수 str
        String File_str = File_READ2();

        // Toast.makeText(getApplicationContext(), "txt 내용 : " + File_str, Toast.LENGTH_SHORT).show();


            Intent resultintent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("File_str", File_str );
            bundle.putString("title",selected_file.getName());

            // some value 안에 원하는 평서문을 넣어줌. 그럼보내게됨.
            resultintent.putExtras(bundle);
            setResult(RESULT_OK , resultintent);
            finish();

        // }

    }
    */


    //===============================================================================================
    //===============================================================================================

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setlistfiles(){
        // 1. 어떤 발표문을 가져와서 확인하는지 해당 폴더를 가져오는 역할.

        // #########    외부저장소가 지정 , 저장이 안되는 상황.
        String pathname = Environment.getExternalStorageDirectory().getAbsolutePath();
        pathname += "/Capstone";

        dir = new File(pathname);

        if(!dir.exists()) {
            Toast.makeText(getApplicationContext()," 파일 존재 " , Toast.LENGTH_LONG).show();
            if (!dir.mkdirs()) {
                // 디텍토리 생성 x 인 경우 - 로그를 생성.
                Toast.makeText(getApplicationContext()," 파일 mkdir 완료 " , Toast.LENGTH_LONG).show();
                Log.d("capstone", "Failed to create directory");
                return;
            }
        }

        file = new File(dir , "발음 연습 예제.txt");
        file2 = new File(dir , "영어 연습 예제.txt");

        try {

            FileOutputStream fos= new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos,"MS949");
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);
            String str = "간장 공장 공장장은 강 공장장이고 된장 공장 공장장은 공 공장장이다. \n" +
                    "내가 그린 기린 그림은 긴 기린 그림이냐, 그냥 그린 기린 그림이냐. \n" +
                    "저기 계신 저 분이 박 법학박사이시고 여기 계신 이 분이 백 법학박사이시다.";
            writer.write(str);
            writer.flush();

            writer.close();
            fos.close();

            FileOutputStream fos2 = new FileOutputStream(file2);
            OutputStreamWriter outputStreamWriter2 = new OutputStreamWriter(fos2,"MS949");
            BufferedWriter writer2 = new BufferedWriter(outputStreamWriter2);
            str = "What time is it now.  \n" +
                    "She said God bless my child. \n" +
                    "He said to me May you live to be a hundred.";
            writer2.write(str);
            writer2.flush();

            writer2.close();
            fos2.close();

        } catch (FileNotFoundException e) {
            showMsg("지정된 파일을 생성할 수 없습니다.");
        } catch (IOException e) {
            showMsg("파일에 데이터를 쓸 수 없습니다.");
        }
    }

    private String File_READ2() {

        if(!selected_file.exists()){
            Toast.makeText(this, "파일 존재 x", Toast.LENGTH_SHORT).show();
        }

        StringBuffer strBuffer = new StringBuffer();

        try{

            FileInputStream is = new FileInputStream(selected_file.getPath());
            InputStreamReader inputStreamReader = new InputStreamReader(is,"MS949");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line="";
            while((line=reader.readLine())!=null){
                strBuffer.append(line+"\n");
            }

            reader.close();
            is.close();

        }
        catch (IOException e){
            e.printStackTrace();
            return "";
        }

        return strBuffer.toString();

    }

}
