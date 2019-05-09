package com.example.capstone_release_01;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 파일 리스트 관리 - 리스트 엑티비티.
public class FileList extends ListActivity {

    private static final int FILE_REQUEST_CODE = 2345;

    private ArrayList<String> mylist;   // file 내 list.
    private File selected_file; // 선택되는 file.

    private File dir;
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 객체 구현.
        mylist = new ArrayList<String>();

        // 파일내 list 를 가져옴.
        setlistfiles();
        File list[] = dir.listFiles();

        // list의 이름을 가져옴. + add
        for(int i =0 ; i< list.length ; i++) {
            mylist.add(list[i].getName());
        }

        // Adapter 생성 , list view 로 매칭.
        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this,
                R.layout.activity_file_list, R.id.listview ,mylist);
        setListAdapter(Adapter);

    }

    //===============================================================================================
    //===============================================================================================

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        selected_file = new File(dir , (String) mylist.get(position));

        // 선택되는 순간 setresult 로 보냄.

        // select 되는 파일이 있을경우 if문 통과.
        /*
        if(!selected_file.isFile()) {
            dir = new File(dir, (String) mylist.get(position));
            File list[] = dir .listFiles();

            mylist.clear();

            for(int i = 0 ; i<list.length ; i++)
            {
                mylist.add(list[i].getName());
            }
            Toast.makeText(getApplicationContext(),file.toString() , Toast.LENGTH_LONG).show();

            setListAdapter
                    (new ArrayAdapter
                            (this, R.layout.activity_file_list,mylist)
                    );
        }
        else {
        */
            Intent resultintent = new Intent();
            // some value 안에 원하는 평서문을 넣어줌. 그럼보내게됨.
            resultintent.putExtra("result", "some value");
            setResult(RESULT_OK , resultintent);
            finish();
        // }
    }

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

        file = new File(dir , "file.txt");

        try {

            FileOutputStream fos= new FileOutputStream(file);
            String str = " 최초 생성 텍스트 ";
            fos.write(str.getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            showMsg("지정된 파일을 생성할 수 없습니다.");
        } catch (IOException e) {
            showMsg("파일에 데이터를 쓸 수 없습니다.");
        }
    }

}
