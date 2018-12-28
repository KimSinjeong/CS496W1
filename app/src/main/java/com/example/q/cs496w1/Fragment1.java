package com.example.q.cs496w1;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;



/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {
    public static Fragment1 newInstance() {
        Bundle args = new Bundle();
        Fragment1 fragment = new Fragment1();
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        JSONArray jarray = new JSONArray();
        if(Permissioncheck()) {
            jarray = getAddr();
        }else{
            Toast toast = Toast.makeText(getContext(),"권한이 거부되어 표시할 수 없습니다.", Toast.LENGTH_LONG);
            toast.show();
        }
        View layout = inflater.inflate(R.layout.fragment_fragment1, container, false);
        if(jarray.length()==0){
            Log.d("taesu","lenth is 0");
        }
        String[] str = new String[jarray.length()];
        for(int i=0;i<jarray.length();i++){
            try{
                JSONObject jsonObject = jarray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String number = jsonObject.getString("number");
                str[i] = ("이름 : " + name + "\n" + "번호 : " + number);
                Log.d("taesu", str[i]);
                Log.d("taesu", "HHIHIHI");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        ListView listview = layout.findViewById(R.id.list_frag1);
        String[] strange = {"hi","codit","vvvdvadfv"};
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,str);

        listview.setAdapter(listViewAdapter);
       // Log.d(toString(listview.getItemsCanFocus()));
        return layout;
    }

    private JSONArray getAddr(){
        Cursor cursor = null;
        JSONArray personArray = new JSONArray();
        Log.d("Taesu", "들어감.getaddr");

        try{
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String phoneName = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
            String [] ad = new String[] {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            cursor = getContext().getContentResolver().query(uri, ad, null, null, phoneName);
            cursor.moveToFirst();
            while(cursor.moveToNext()){
                Log.d("json 받는부분",cursor.getString(0));
                if(cursor.getString(1)!=null){
                        JSONObject personinfo = new JSONObject();
                        Log.d("hi ", cursor.getString(0));
                        Log.d("hi ", cursor.getString(1));
                        personinfo.put("name",cursor.getString(0));
                        personinfo.put("number",cursor.getString(1));
                        personArray.put(personinfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(cursor != null){
                cursor.close();
                cursor = null;
            }
        }
        if(personArray.length()==0){
            Log.d("json 만드는부분","lenth is 0");
        }
        return personArray;
    }

    public int checkselfpermission(String permission) {
        return PermissionChecker.checkSelfPermission(getContext(), permission);
    }

    public boolean Permissioncheck() {
        if (checkselfpermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 100);
            if (checkselfpermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }
}
