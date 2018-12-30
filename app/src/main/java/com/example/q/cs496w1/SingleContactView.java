package com.example.q.cs496w1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SingleContactView extends LinearLayout {
    TextView Name;
    TextView Phone;
    ImageView Thumbnail;

    public SingleContactView(Context context) {
        super(context);

        init(context);
    }

    public SingleContactView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.single_contact, this, true);

        Name = (TextView) findViewById(R.id.name);
        Phone = (TextView) findViewById(R.id.phone);
        Thumbnail = (ImageView) findViewById(R.id.thumbnail);
    }

    public void setName(String name) { Name.setText(name); }

    public void setPhone(String mobile) {
        Phone.setText(mobile);
    }

    public void setImage(int resId) {
        Thumbnail.setImageResource(resId);
    }
}
