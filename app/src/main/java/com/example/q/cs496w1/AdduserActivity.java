package com.example.q.cs496w1;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AdduserActivity extends AppCompatActivity {
    TextView cancel, add;
    EditText etname, etphone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adduser);

        cancel = (TextView) findViewById(R.id.cancel);
        add = (TextView) findViewById(R.id.add);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                etname = (EditText) findViewById(R.id.addname);
                etphone = (EditText) findViewById(R.id.addphone);
                Log.d("name", etname.getText().toString());
                Log.d("phone", etphone.getText().toString());
                resultIntent.putExtra("name", etname.getText().toString());
                resultIntent.putExtra("phone", etphone.getText().toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

    }
}
