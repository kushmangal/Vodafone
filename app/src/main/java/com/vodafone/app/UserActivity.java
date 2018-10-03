package com.vodafone.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserActivity extends AppCompatActivity {

    @Bind(R.id.uidET)
    EditText uidET;

    @Bind(R.id.uidBtn)
    Button uidBtn;

    SharedPreferences pref;
    SharedPreferences.Editor pref_editor;
    String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        pref = getSharedPreferences("VF_Pref", Context.MODE_PRIVATE);
        pref_editor = pref.edit();

        if(pref!=null && pref.getString("uid","")!="")
        {
            Intent in = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(in);
            finish();
        }

        uidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uid  = uidET.getText().toString();
                pref_editor.putString("uid",uid).apply();
                Intent in = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(in);
                finish();
            }
        });
    }
}
