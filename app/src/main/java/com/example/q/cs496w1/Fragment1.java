package com.example.q.cs496w1;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment implements View.OnClickListener {

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;

    View layer;
    ListView listView;
    ContactAdapter adapter;

    JSONArray jarray;

    private final int ADD_USER_CODE = 1;

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

        layer = inflater.inflate(R.layout.fragment_fragment1, container, false);
        listView = layer.findViewById(R.id.list_frag1);

        // Fab
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        fab = (FloatingActionButton) layer.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) layer.findViewById(R.id.adduser);
        fab2 = (FloatingActionButton) layer.findViewById(R.id.deluser);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        return layer;

    }

    @Override
    public void onResume(){
        super.onResume();
        adapter = new ContactAdapter();
        if(PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            contactsShow();
        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }
        adapter.notifyDataSetChanged();
    }

    // TODO: Make the Activities to delete users.
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                Toast.makeText(getContext(), "FABs", Toast.LENGTH_SHORT).show();
                break;
            case R.id.adduser:
                anim();
                Toast.makeText(getContext(), "Add Users", Toast.LENGTH_SHORT).show();
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), AdduserActivity.class);
                        int contactwPermission = ContextCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.WRITE_CONTACTS);
                        if (contactwPermission == PackageManager.PERMISSION_GRANTED) {
                            startActivityForResult(intent, ADD_USER_CODE);
                        } else
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CONTACTS}, ADD_USER_CODE);
                    }
                });
                break;
            case R.id.deluser:
                anim();
                Toast.makeText(getContext(), "Delete Users", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case ADD_USER_CODE:
                    String name = data.getStringExtra("name");
                    String phone = data.getStringExtra("phone");
                    ArrayList < ContentProviderOperation > ops = new ArrayList <ContentProviderOperation> ();
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                            .build());
                    if(name != null) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                                .build());
                    }
                    if(phone != null) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                .build());
                    }

                    // Asking the Contact provider to create a new contact
                    try {

                        getContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    public void anim() {
        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
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
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts._ID
            };
            cursor = getContext().getContentResolver().query(uri, ad, null, null, phoneName);
            if(cursor.moveToFirst()) {
                do {
                    Log.d("json 받는부분", cursor.getString(0));
                    if (cursor.getString(1) != null) {
                        JSONObject personinfo = new JSONObject();
                        Log.d("hi ", cursor.getString(0));
                        Log.d("hi ", cursor.getString(1));
                        personinfo.put("name", cursor.getString(0));
                        personinfo.put("number", cursor.getString(1));
                        personinfo.put("photoid", cursor.getLong(2));
                        personinfo.put("id", cursor.getLong(3));
                        personArray.put(personinfo);
                    }
                } while (cursor.moveToNext());
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

    public void contactsShow(){
        jarray = new JSONArray();

        jarray = getAddr();

        if(jarray.length()==0){
            Log.d("Fragment1","lenth is 0");
        }
        adapter = new ContactAdapter();
        String[] str = new String[jarray.length()];

        for (int i = 0; i < jarray.length(); i++) {
            try {
                JSONObject jsonObject = jarray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String number = jsonObject.getString("number");
                Long photoid = jsonObject.getLong("photoid");
                Long id = jsonObject.getLong("id");
                str[i] = ("이름 : " + name + "\n" + "번호 : " + number);
                Log.d("Fragment1", str[i]);
                adapter.addItem(new SingleContact(name, number, photoid, id));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        listView.setAdapter(adapter);
        if(PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            saveContacts(jarray);
        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }
    }

    public void saveContacts(JSONArray jarray){
        try {
            String ContactsPath = getExternalPath();
            File file = new File(ContactsPath + "Contacts");

            if (!file.isDirectory())
                file.mkdir();
            FileWriter filew = new FileWriter(ContactsPath + "Contacts/JSONContacts");
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jsonObject = jarray.getJSONObject(i);
                filew.write(jsonObject.toString());
            }

            filew.flush();
            filew.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contactsShow();
                } else {
                    Toast toast = Toast.makeText(getContext(),"권한이 거부되어 보여줄 수 없습니다.", Toast.LENGTH_LONG);
                    toast.show();
                }
                return;
            }
            case 200:{
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    saveContacts(jarray);
                }else{
                    Toast toast = Toast.makeText(getContext(),"권한이 거부되어 저장할 수 없습니다.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public String getExternalPath(){
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED))
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        else{
            sdPath = getContext().getFilesDir() + "";
            Toast.makeText(getContext().getApplicationContext(), sdPath, Toast.LENGTH_SHORT).show();
        }
        return sdPath;
    }

    class ContactAdapter extends BaseAdapter {
        ArrayList<SingleContact> items = new ArrayList<SingleContact>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(SingleContact item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            SingleContactView view = new SingleContactView(getContext());

            SingleContact item = items.get(position);
            view.setName(item.getName());
            view.setPhone(item.getPhone());
            view.setImage(item.getResId());
            view.setImage(item.getPhotoId(), item.getId());
            return view;
        }
    }
}
