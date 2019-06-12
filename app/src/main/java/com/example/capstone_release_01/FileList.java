package com.example.capstone_release_01;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

import static android.app.Activity.RESULT_OK;


// 파일 리스트 관리 - 리스트 엑티비티.
public class FileList extends AppCompatActivity {

    public static Context mContext;

    public ArrayList<ItemData> mylist;   // file 내 list.
    private ArrayList<String> list2;
    private ListView list1 = null;

    public File selected_file; // 선택되는 file.

    public File dir;
    public File file;
    public File file2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        // 객체 구현.
        mylist = new ArrayList<ItemData>();
        //####################### 이부분을 지정하자.

        // 파일내 list 를 가져옴.
        setlistfiles();
        File list[] = dir.listFiles();
        // 삽입될 변수들

        // list의 이름을 가져옴. + add
        for (int i = 0; i < list.length; i++) {
            ItemData oItem = new ItemData();
            oItem.strTitle = list[i].getName();
            //oItem.onClickListener = this;
            mylist.add(oItem);
        }

        // list1 = this.getListView();

        // Adapter 생성 , list view 로 매칭.
        /*final ArrayAdapter<String> Adapter = new ArrayAdapter<String> (this,
                android.R.layout.activity_list_item , mylist);*/

        //setListAdapter(Adapter);
        // getListView().setTextFilterEnabled(true);

        //list1.setAdapter(Adapter);        원래 코드

        list1 = (ListView) findViewById(R.id.list);
        List_Adapter oAdapter = new List_Adapter(mylist, this, this);
        list1.setAdapter(oAdapter);

        mContext = this;

        /*      원래 코드
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 이상 무!!!!!!!!!!!!!!!!!!!!!!!
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selected_file = new File(dir , mylist.get(position).getName());

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
        */
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

    public void setlistfiles() {
        // 1. 어떤 발표문을 가져와서 확인하는지 해당 폴더를 가져오는 역할.

        // #########    외부저장소가 지정 , 저장이 안되는 상황.
        String pathname = Environment.getExternalStorageDirectory().getAbsolutePath();
        pathname += "/Capstone";

        dir = new File(pathname);

        if (!dir.exists()) {
            Toast.makeText(getApplicationContext(), " 파일 존재 ", Toast.LENGTH_LONG).show();
            if (!dir.mkdirs()) {
                // 디텍토리 생성 x 인 경우 - 로그를 생성.
                Toast.makeText(getApplicationContext(), " 파일 mkdir 완료 ", Toast.LENGTH_LONG).show();
                Log.d("capstone", "Failed to create directory");
                return;
            }
        }

        file = new File(dir, "발음 연습 예제.txt");
        file2 = new File(dir, "영어 연습 예제.txt");

        try {

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos, "MS949");
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);
            String str = "간장 공장 공장장은 강 공장장이고 된장 공장 공장장은 공 공장장이다. \n" +
                    "내가 그린 기린 그림은 긴 기린 그림이냐, 그냥 그린 기린 그림이냐. \n" +
                    "저기 계신 저 분이 박 법학박사이시고 여기 계신 이 분이 백 법학박사이시다.";
            writer.write(str);
            writer.flush();

            writer.close();
            fos.close();

            FileOutputStream fos2 = new FileOutputStream(file2);
            OutputStreamWriter outputStreamWriter2 = new OutputStreamWriter(fos2, "MS949");
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

    public String File_READ2() {

        if (!selected_file.exists()) {
            Toast.makeText(this, "파일 존재 x", Toast.LENGTH_SHORT).show();
        }

        StringBuffer strBuffer = new StringBuffer();

        try {

            FileInputStream is = new FileInputStream(selected_file.getPath());
            InputStreamReader inputStreamReader = new InputStreamReader(is, "MS949");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = reader.readLine()) != null) {
                strBuffer.append(line + "\n");
            }

            reader.close();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        return strBuffer.toString();

    }


    // 버튼 클릭 시, 액션
    /*public void onClick(View v)
    {
        View oParentView = (View)v.getParent(); // 부모의 View를 가져온다. 즉, 아이템 View임.
        TextView oTextTitle = (TextView) oParentView.findViewById(R.id.textTitle);
        String position = (String) oParentView.getTag();

        selected_file = new File(dir , mylist.get(Integer.parseInt(position)).getName());

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
    }*/

}


class ItemData {
    public String strTitle;


    public String getName() {
        return strTitle;
    }
}

class List_Adapter extends BaseAdapter {
    FileList m;
    LayoutInflater inflater = null;
    private ArrayList<ItemData> m_oData = null;
    private int nListCnt = 0;
    Context context;
    private static final int FILE_REQUEST_CODE = 2345;

    public List_Adapter(ArrayList<ItemData> _oData, AppCompatActivity mainActivity, FileList FL) {
        m_oData = _oData;
        nListCnt = m_oData.size();
        context = mainActivity;
        m = FL;
    }

    public interface ListBtnClickListener {
        void onListBtnClick(int position);
    }

    private ListBtnClickListener listBtnClickListener;

    @Override
    public int getCount() {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final Context context = parent.getContext();
            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView oTextTitle = (TextView) convertView.findViewById(R.id.textTitle);

        Button button1 = (Button) convertView.findViewById(R.id.button1);
        Button button2 = (Button) convertView.findViewById(R.id.button2);
        Button button3 = (Button) convertView.findViewById(R.id.button3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.selected_file = new File(m.dir, m.mylist.get(position).getName());
                // 파일 내용 저장 - 파일 내용 변수 str
                String File_str = m.File_READ2();

                Intent resultintent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("File_str", File_str);
                bundle.putString("title", m.selected_file.getName());

                // some value 안에 원하는 평서문을 넣어줌. 그럼보내게됨.
                resultintent.putExtras(bundle);
                m.setResult(RESULT_OK, resultintent);
                m.finish();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*m.selected_file = new File(m.dir, m.mylist.get(position).getName());
                // 파일 내용 저장 - 파일 내용 변수 str
                String File_str = m.File_READ2();

                Intent resultintent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("File_str", File_str);
                bundle.putString("title", m.selected_file.getName());

                // some value 안에 원하는 평서문을 넣어줌. 그럼보내게됨.
                resultintent.putExtras(bundle);
                m.setResult(RESULT_OK, resultintent);
                m.finish();*/
                m.selected_file = new File(m.dir, m.mylist.get(position).getName());
                String File_str = m.File_READ2();

                Intent intent = new Intent(m.getApplicationContext(),FileModify.class);

                Bundle bundle = new Bundle();
                bundle.putString("File_str", File_str);

                String otitle = m.selected_file.getName();
                int idx = otitle.lastIndexOf(".");
                String title = otitle.substring(0, idx);
                bundle.putString("title", title);
                bundle.putInt("pos", position);

                intent.putExtras(bundle);
                m.startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.selected_file = new File(m.dir, m.mylist.get(position).getName());

                try {
                    if (m.selected_file.exists()) { //파일존재여부확인
                        m.selected_file.delete();
                        showMsg("파일삭제 성공");
                        Intent intent = m.getIntent();
                        m.finish();
                        context.startActivity((intent));

                        /*Intent mIntent = new Intent(context.getApplicationContext(),FileList.class);        //Uri 를 이용하여 웹브라우저를 통해 웹페이지로 이동하는 기능
                        context.startActivity((mIntent));
                        */
                        // 다른 페이지로 넘어갈 때 인텐트를 써야한다.
                    } else {
                        showMsg("파일이 존재하지 않습니다.");
                    }
                } catch (Exception e) {
                }

            }
        });

        /*button1.setOnClickListener(m_oData.get(position).onClickListener);
        button2.setOnClickListener(m_oData.get(position).onClickListener);
        button3.setOnClickListener(m_oData.get(position).onClickListener);
*/
        oTextTitle.setText(m_oData.get(position).strTitle);

        convertView.setTag("" + position);
        return convertView;
    }

    public void onClick(View v) {
        // ListBtnClickListener(MainActivity)의 onListBtnClick() 함수 호출.
        if (this.listBtnClickListener != null) {
            this.listBtnClickListener.onListBtnClick((int) v.getTag());
        }
    }

    private void showMsg(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
