package com.example.q.cs496w1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

        viewerContainer.post(new Runnable() {
            @Override
            public void run() {
                viewerContainer.getHeight(); //height is ready
            }
        });

        final ViewTreeObserver observer= viewerContainer.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        imgWidth = viewerContainer.getMeasuredWidth();
                        Log.d("Log", "Height: " + viewerContainer.getWidth());
                    }
                });

        //전송메세지
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String imgPath = extras.getString("filename");

        // 완성된 이미지 보여주기;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 1;
        Bitmap bm = BitmapFactory.decodeFile(imgPath, bfo);
        imgWidth = 1080;
        imgHeight = 1584;
        Log.d("onCreaete", "Height: " + imgHeight);
        Log.d("onCreate", "Height: " + imgWidth);
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (width > height) {
            // landscape
            double ratio = (double) width / imgWidth;
            width = imgWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / imgHeight;
            height = imgHeight;
            width = (int)(width / ratio);
        } else {
            // square
            height = imgHeight;
            width = imgWidth;
        }
        Bitmap resized = Bitmap.createScaledBitmap(bm,width,height,false);
        init(resized);

    }


    /**
     * 초기화
     */
    private void init(Bitmap sourceBitmap) {
        if (sourceBitmap != null) {
            displayView = new ImageDisplayView(this);

            displayView.setImageData(sourceBitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            viewerContainer.addView(displayView, params);
        }
    }

}
