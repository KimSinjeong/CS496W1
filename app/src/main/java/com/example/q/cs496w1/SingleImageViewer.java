package com.example.q.cs496w1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

public class SingleImageViewer extends AppCompatActivity{
    private Context mContext;
    private static int imgWidth;
    private static int imgHeight;
    /**
     * 이미지를 보여줄 뷰를 담고있을 레이아웃 객체
     */
    LinearLayout viewerContainer;

    /**
     * 이미지를 보여줄 뷰
     */
    ImageDisplayView displayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image_viewer);
        mContext = this;
        viewerContainer = findViewById(R.id.viewerContainer);

        //전송메세지
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String imgPath = extras.getString("filename");

        // 완성된 이미지 보여주기;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 1;
        Bitmap bm = BitmapFactory.decodeFile(imgPath, bfo);

        init(bm);
    }

    /**
     * 초기화
     */
    private void init(Bitmap sourceBitmap) {
        if (sourceBitmap != null) {
            displayView = new ImageDisplayView(this);

            //Glide.with(getApplicationContext()).load(sourceBitmap).into(displayView);
            displayView.setImageData(sourceBitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            viewerContainer.addView(displayView, params);
        }
    }


}
