package com.example.capstone_release_01;

import android.Manifest;
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

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1111;
    private int permissioncheck ;

    private ArrayList<String> mylist;   // file 내 list.
    private File selected_file; // 선택되는 file.

    private String state;
    private TextView tv;
    private File dir;
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissioncheck = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissioncheck == PackageManager.PERMISSION_DENIED){
            // 권한 없음
           // ActivityCompat.requestPermissions(get, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);

        }
        else {
            // 권한 있음
        }

        // 객체 구현.
        mylist = new ArrayList<String>();
        // Toast.makeText(getApplicationContext(),Environment.getExternalStorageDirectory().getAbsolutePath()+"/capstone/" , Toast.LENGTH_LONG).show();

        // 1. 어떤 발표문을 가져와서 확인하는지 해당 폴더를 가져오는 역할.

        // #########    외부저장소가 지정 , 저장이 안되는 상황.
        String pathname = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        pathname += "/Capstone";

        File dir = new File(pathname);
        dir.mkdir();

        file = new File(dir , "file.txt");

        try {
            FileOutputStream fos= new FileOutputStream(file);
            String str = " 이 파일은 sd카드에 저장된 메세지";
            fos.write(str.getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            showMsg("지정된 파일을 생성할 수 없습니다.");
        } catch (IOException e) {
            showMsg("파일에 데이터를 쓸 수 없습니다.");
        }


            /*
            if(!file.exists()){
                Toast.makeText(getApplicationContext(),"존재하긴함" , Toast.LENGTH_LONG).show();

                if(!file.mkdirs()){
                    Toast.makeText(getApplicationContext(), " 생성 x", Toast.LENGTH_LONG).show();
                }
            }*/

            pathname += "/First_text.txt";

        file = new File(pathname);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // ########

        // 1-1../capstone 폴더가 존재하지 않으면 mkdir을 이용해서 폴더생성.
        /*
        if(!file.exists()) {
            Toast.makeText(getApplicationContext()," 파일 존재 " , Toast.LENGTH_LONG).show();
            if (!file.mkdirs()) {
                // 디텍토리 생성 x 인 경우 - 로그를 생성.
                Toast.makeText(getApplicationContext()," 파일 mkdir 완료 " , Toast.LENGTH_LONG).show();
                Log.d("capstone", "Failed to create directory");
                return;
            }
        }
        */

        // 파일내 list 를 가져옴.
       /*
        File list[] = file.listFiles();

        // list의 이름을 가져옴. + add
        for(int i =0 ; i< list.length ; i++) {
            mylist.add(list[i].getName());
        }

        // Adapter 생성 , list view 로 매칭.
        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this,
                R.layout.activity_file_list, R.id.listview ,mylist);
        setListAdapter(Adapter);
        */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                }
                return;
        }
    }


    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



    /*
    public void Get_list() {
        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.activity_file_list,
                R.id.listview,mylist));

        //############################### list 리턴해주는 부분 마무리.
    }
    */
    /*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        selected_file = new File(file , (String) mylist.get(position));

        // 선택되는 순간 setresult 로 보냄.

        // select 되는 파일이 있을경우 if문 통과.
        if(!selected_file.isFile()) {
            file = new File(file, (String) mylist.get(position));
            File list[] = file .listFiles();

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
            Intent intent = new Intent();
            // some value 안에 원하는 평서문을 넣어줌. 그럼보내게됨.
            intent.putExtra("file_result", "some value");
            setResult(RESULT_OK, intent);
            finish();
        }
    }
    */
    /*
    public List getSelected_file(){
        return (List) selected_file;
    }

    public  int getSelected_Count() {

        return 1;
    }
    */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
