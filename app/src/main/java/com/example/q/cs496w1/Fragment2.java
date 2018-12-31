package com.example.q.cs496w1;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {

    GridView gridView;
    PhotoAdapter photoAdapter;
    String TAG;

    public static Fragment2 newInstance() {
        Bundle args = new Bundle();
        Fragment2 fragment = new Fragment2();
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TAG = "fragment2";
        View view = inflater.inflate(R.layout.fragment_fragment2, container, false);
        gridView =  view.findViewById(R.id.gridView);

        photoAdapter = new PhotoAdapter(getContext());
        gridView.setAdapter(photoAdapter);
        // 이 부분 이벤트는 클릭했을 때 이미지가 확대되어 보여주는 부분.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
           public void onItemClick(AdapterView parent, View v, int position, long id){
               photoAdapter.callImageViewer(position);
           }
        });


        // Inflate the layout for this fragment
        return view;
    }


    class PhotoAdapter extends BaseAdapter {
        Context mContext;
        private String imgData;
        private String geoData;
        private ArrayList<String> thumbsDataList;
        private ArrayList<String> thumbsIDList;


        public PhotoAdapter(Context c){
            mContext = c;
            thumbsDataList = new ArrayList<String>();
            thumbsIDList = new ArrayList<String>();
            if(Permissioncheck()){
                getThumbInfo(thumbsIDList, thumbsDataList);
            }
        }

        public void updatethumbnail(){
            thumbsDataList = new ArrayList<String>();
            thumbsIDList = new ArrayList<String>();
            if(Permissioncheck()){
                getThumbInfo(thumbsIDList, thumbsDataList);
            }
        }

        public final void callImageViewer(int selectedIndex){
            Intent i = new Intent(mContext, SingleImageViewer.class);
            String imgPath = getImageInfo(imgData, geoData, thumbsIDList.get(selectedIndex));
            i.putExtra("filename", imgPath);
            startActivityForResult(i, 1);
        }

        public boolean deleteSelected(int sIndex){
            return true;
        }

        @Override
        public int getCount() {
            return thumbsIDList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null){
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(gridView.getColumnWidth(), gridView.getColumnWidth()));
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(1, 1, 1, 1);
                Log.d(TAG,"convertView is generated");
            }else{
                imageView = (ImageView) convertView;
            }
            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 32;
            Bitmap bmp = BitmapFactory.decodeFile(thumbsDataList.get(position), bo);
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 95, 95, true);
            imageView.setImageBitmap(resized);

            return imageView;
        }

        private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas){
            String[] proj = {MediaStore.Images.Media._ID,
                             MediaStore.Images.Media.DATA,
                             MediaStore.Images.Media.DISPLAY_NAME,
                             MediaStore.Images.Media.SIZE};
            Cursor imageCursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,proj,null,null,null);

            if (imageCursor != null && imageCursor.moveToFirst()){
                String title;
                String thumbsID;
                String thumbsImageID;
                String thumbsData;


                int thumbsIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
                int thumbsDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int thumbsImageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int num = 0;
                do {
                    thumbsID = imageCursor.getString(thumbsIDCol);
                    thumbsData = imageCursor.getString(thumbsDataCol);
                    thumbsImageID = imageCursor.getString(thumbsImageIDCol);
                    num++;
                    if (thumbsImageID != null){
                        thumbsIDs.add(thumbsID);
                        thumbsDatas.add(thumbsData);
                    }
                }while (imageCursor.moveToNext());
            }
            imageCursor.close();
            return;
        }

        private String getImageInfo(String ImageData, String Location, String thumbID){
            String imageDataPath = null;
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};
            Cursor imageCursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, "_ID='" + thumbID + "'", null, null);

            if (imageCursor != null && imageCursor.moveToFirst()){
                if (imageCursor.getCount() > 0){
                    int imgData = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    imageDataPath = imageCursor.getString(imgData);
                }
            }
            imageCursor.close();
            return imageDataPath;
        }

    }

    public int checkselfpermission(String permission) {
        return PermissionChecker.checkSelfPermission(getContext(), permission);
    }

    public boolean Permissioncheck() {
        if (checkselfpermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkselfpermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1052);
            if (checkselfpermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkselfpermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1052: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && checkselfpermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        checkselfpermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    photoAdapter.getThumbInfo(photoAdapter.thumbsIDList, photoAdapter.thumbsDataList);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "권한이 거부되었습니다", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
