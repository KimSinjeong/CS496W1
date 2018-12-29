package com.example.q.cs496w1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment3 extends Fragment {
    float y1,y2;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fragment3, container, false);
        final ImageView btn1 = view.findViewById(R.id.imageView11);
        final ImageView btn2 = view.findViewById(R.id.imageView12);

        final RelativeLayout relativeLayout1 = view.findViewById(R.id.relative_im6);
        final RelativeLayout relativeLayout2 = view.findViewById(R.id.relative_im7);

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


}
