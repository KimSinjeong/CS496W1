package com.example.q.cs496w1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SingleImageViewer extends AppCompatActivity{
    private Context mContext;
    private static int imgWidth;
    private static int imgHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image_viewer);
        mContext = this;

        //전송메세지
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String imgPath = extras.getString("filename");

        // 완성된 이미지 보여주기;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 1;
        ImageView iv = (ImageView)findViewById(R.id.imageView10);
        Bitmap bm = BitmapFactory.decodeFile(imgPath, bfo);
        iv.setImageBitmap(bm);
    }
}
