package com.example.q.cs496w1;


import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {
    public static final int REQUEST_IMAGE_CAPTURE = 532;

    private String imageFilePath;
    private Uri photoUri;

    GridView gridView;
    PhotoAdapter photoAdapter;
    String TAG;
    private FloatingActionButton Cam;

    public static Fragment2 newInstance() {
        Bundle args = new Bundle();
        Fragment2 fragment = new Fragment2();
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment2() {
        // Required empty public constructor
    }

    // TODO: Immediate update for new pictures.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TAG = "fragment2";
        View view = inflater.inflate(R.layout.fragment_fragment2, container, false);
        /*
        gridView =  view.findViewById(R.id.gridView);

        photoAdapter = new PhotoAdapter(getContext());
        gridView.setAdapter(photoAdapter);
        // 이 부분 이벤트는 클릭했을 때 이미지가 확대되어 보여주는 부분.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
           public void onItemClick(AdapterView parent, View v, int position, long id){
               photoAdapter.callImageViewer(position);
           }
        });

        Cam = view.findViewById(R.id.cam);
        Cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CamPermissioncheck())
                    sendTakePhotoIntent();
                else{
                    Toast toast = Toast.makeText(getContext(),"권한이 거부되어 사진을 찍을 수 없습니다.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });*/

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        TAG = "fragment2";
        Log.d(TAG,"onResume");
        //View view = inflater.inflate(R.layout.fragment_fragment2, container, false);
        View view = getView();
        gridView =  view.findViewById(R.id.gridView);

        photoAdapter = new PhotoAdapter(getContext());
        gridView.setAdapter(photoAdapter);
        // 이 부분 이벤트는 클릭했을 때 이미지가 확대되어 보여주는 부분.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView parent, View v, int position, long id){
                photoAdapter.callImageViewer(position);
            }
        });

        Cam = view.findViewById(R.id.cam);
        Cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CamPermissioncheck())
                    sendTakePhotoIntent();
                else{
                    Toast toast = Toast.makeText(getContext(),"권한이 거부되어 사진을 찍을 수 없습니다.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });



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

            /*
            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 32;
            Bitmap bmp = BitmapFactory.decodeFile(thumbsDataList.get(position), bo);
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 95, 95, true);
            imageView.setImageBitmap(resized);*/
            Glide.with(getContext()).load(thumbsDataList.get(position)).into(imageView);


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
    // Taking a photo functionality.
    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                Log.d("photo","카메라 앱 호출 직전");
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        String temppath = Environment.getExternalStorageDirectory().toString() + "/images";
        File storageDir = new File(temppath);
        Log.d("", temppath);
        if (!storageDir.isDirectory())
            storageDir.mkdir();
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public boolean CamPermissioncheck() {
        if (checkselfpermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
            if (checkselfpermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Toast.makeText(getContext(), "Image...", Toast.LENGTH_SHORT).show();

            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            ContextWrapper cw = new ContextWrapper(getContext());
            File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);

            File path = new File(dir, timeStamp);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
