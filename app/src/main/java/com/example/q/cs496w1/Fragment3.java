package com.example.q.cs496w1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment3 extends Fragment {
    float y1 = 424.0f,y2 = 424.0f;
    byte prevd = 0;
    final int BT_REQUEST_CODE = 1;
    final int ACT_REQUEST_CODE = 1;

    boolean effectSound = false;
    TimerTask sender;
    Timer timer;

    private FloatingActionButton BTbtn;

    // 효과음내기 관련 변수
    private MediaPlayer connect, disconnect, driving;
    private SoundPool exhaust;
    boolean issending = false;
    private int sound_exhaust;

    private void initSound()
    {
        connect = MediaPlayer.create(getContext(), R.raw.calldrop);
        connect.setLooping(false);

        exhaust = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0 );
        sound_exhaust = exhaust.load( getContext(), R.raw.exhaust_loop, 1 );

        disconnect = MediaPlayer.create(getContext(), R.raw.disconnect);
        disconnect.setLooping(false);

        driving = MediaPlayer.create(getContext(), R.raw.driving);
        driving.setLooping(true);

        connect.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                exhaustSound();
            }
        });
    }

    public void exhaustSound()
    {
        exhaust.play( sound_exhaust, 1f, 1f, 0, -1, 1f );
    }

    public static Fragment3 newInstance() {
        Bundle args = new Bundle();
        Fragment3 fragment = new Fragment3();
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initSound();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(issending) {
            if(effectSound==false){
                effectSound=true;
                connect.start();
            }
            BTbtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.BTactive)));
            BTbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 블루투스 커넥션 해제
                    try {
                        disconnect.start();
                        ConnectBluetoothActivity.outputStream.close();
                        exhaust.stop(sound_exhaust);
                        sound_exhaust = exhaust.load( getContext(), R.raw.exhaust_loop, 1 );
                        driving.stop();
                        driving.release();
                        driving = MediaPlayer.create(getContext(), R.raw.driving);
                        driving.setLooping(true);
                        timer.cancel();
                        timer = null;
                        BTbtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.BTinactive)));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    issending = false;
                    effectSound = false;

                    // 다시 클릭하면 블루투스 연결 Activity 실행
                    BTbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 새 창에서 ConnectBluetoothActivity 실행
                            Intent intent = new Intent(getActivity(), ConnectBluetoothActivity.class);
                            int btPermission = ContextCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.BLUETOOTH);

                            if (btPermission == PackageManager.PERMISSION_GRANTED) {
                                startActivityForResult(intent, ACT_REQUEST_CODE);
                            } else
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, BT_REQUEST_CODE);
                        }
                    });
                }
            });

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fragment3, container, false);
        final ImageView btn1 = view.findViewById(R.id.imageView11);
        final ImageView btn2 = view.findViewById(R.id.imageView12);

        final RelativeLayout relativeLayout1 = view.findViewById(R.id.relative_im6);
        final RelativeLayout relativeLayout2 = view.findViewById(R.id.relative_im7);

        BTbtn = view.findViewById(R.id.BT);

        // "블루투스 연결" 버튼 클릭 이벤트
        BTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새 창에서 ConnectBluetoothActivity 실행
                Intent intent = new Intent(getActivity(), ConnectBluetoothActivity.class);
                int btPermission = ContextCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.BLUETOOTH);

                if (btPermission == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(intent, ACT_REQUEST_CODE);
                } else
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, BT_REQUEST_CODE);
            }
        });

        // 컨트롤러 터치를 하면 왼쪽 button이 이동하는 이벤트
        relativeLayout1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final float y_size = btn1.getHeight()/2;
                final float compare_center = relativeLayout1.getMinimumHeight() + relativeLayout1.getHeight()/2;
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{}
                    case MotionEvent.ACTION_MOVE:{
                        float value = event.getY() - y_size;
                        if(value > 735)
                            btn1.setY(735);
                        else if(value < 0)
                            btn1.setY(0);
                        else
                            btn1.setY(value);
                        break;
                    }
                    default:{
                        btn1.setY(compare_center - y_size);
                    }
                }
                y1 = btn1.getY();
                Log.d("y1", String.valueOf(y1));
                return true;
            }
        }
        );
        // 컨트롤러 터치를 하면 오른쪽 button이 이동하는 이벤트
        relativeLayout2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final float y_size = btn2.getHeight()/2;
                final float compare_center = relativeLayout2.getMinimumHeight() + relativeLayout2.getHeight()/2;
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{}
                    case MotionEvent.ACTION_MOVE:{
                        float value = event.getY() - y_size;
                        if(value > 735)
                            btn2.setY(735);
                        else if(value < 0)
                            btn2.setY(0);
                        else
                            btn2.setY(value);
                        break;
                    }
                    default:{
                        btn2.setY(compare_center - y_size);
                    }
                }
                y2 = btn2.getY();
                Log.d("y2", String.valueOf(y2));
                return true;
                }
            }
        );

        return view;
    }//end of onCreateView

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data){
        if(resultCode == getActivity().RESULT_OK){
            switch (requestCode) {
                case ACT_REQUEST_CODE:
                    issending = true;
                    sender = new TimerTask() {
                        @Override
                        public void run() {
                            float tmpy1 = y1-424.0f;
                            float tmpy2 = y2-424.0f;

                            tmpy1 = tmpy1 < 0.0f ? -tmpy1/424.0f*7.0f : -tmpy1/311.0f*8.0f;
                            tmpy2 = tmpy2 < 0.0f ? -tmpy2/424.0f*7.0f : -tmpy2/311.0f*8.0f;

                            byte bty1 = (byte)((0x0f & (int)tmpy1) << 4);
                            byte bty2 = (byte)(0x0f & (int)tmpy2);

                            byte detector = (byte)(bty1 | bty2);

                            if (prevd != 0 && detector == 0) {
                                driving.stop();
                                driving.release();
                                driving = MediaPlayer.create(getContext(), R.raw.driving);
                                driving.setLooping(true);
                                exhaust.resume(sound_exhaust);
                            }
                            else if (prevd == 0 && detector != 0) {
                                driving.start();
                                exhaust.pause(sound_exhaust);
                            }

                            prevd = detector;

                            try {
                                //ConnectBluetoothActivity.outputStream.write(data.getStringExtra("result").getBytes());
                                ConnectBluetoothActivity.outputStream.write(new byte[]{detector});
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    timer = new Timer();
                    timer.schedule(sender, 0, 100);
                    break;
            }
        }
    }
}
