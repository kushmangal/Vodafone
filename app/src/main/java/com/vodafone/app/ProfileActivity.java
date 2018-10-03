package com.vodafone.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ProfileActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor pref_editor;

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.number)
    TextView number;

    @Bind(R.id.data)
    TextView dataBalance;

    @Bind(R.id.balance)
    TextView balance;

    @Bind(R.id.plan_card)
    LinearLayout plan_card;

    @Bind(R.id.plan_title)
    TextView plan_title;

    @Bind(R.id.services_card)
    LinearLayout services_card;

    @Bind(R.id.services_title)
    TextView services_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        ButterKnife.bind(this);

        pref = getSharedPreferences("VF_Pref", Context.MODE_PRIVATE);
        pref_editor = pref.edit();
        fetchOffers();
    }

    void fetchOffers() {
        Retrofit retrofit = RetrofitWrapper.getRetrofitRequest(getApplicationContext(), pref);
        final API offers_api = retrofit.create(API.class);
        Call<API_RESPONSE> call = offers_api.fetchUser(pref.getString("uid", ""));
        call.enqueue(new Callback<API_RESPONSE>() {
            @Override
            public void onResponse(Call<API_RESPONSE> call, Response<API_RESPONSE> response) {

                API_RESPONSE data = response.body();
                if (data != null) {
                    if (data.getStatus() == 1) {
                            ProfileResponse user = data.getUser();
                            if(user!=null) {
                                name.setText(user.getName());
                                number.setText(user.getNumber());
                                balance.setText("₹"+String.valueOf(user.getBalance()));
                                dataBalance.setText(String.valueOf(user.getNetbalance())+" MB");
                                if(!user.getPlan_name().equals("")){
                                    plan_card.setVisibility(View.VISIBLE);
                                    plan_title.setText(user.getPlan_name());
                                }else {
                                    plan_card.setVisibility(View.GONE);
                                }

                                if(user.getServices()!=null && user.getServices().size()>0){
                                    String text="";
                                    for(int i=0;i<user.getServices().size();i++)
                                        text += user.getServices().get(i)+"\n";

                                    services_card.setVisibility(View.VISIBLE);
                                    services_title.setText(text);
                                }else {
                                    services_card.setVisibility(View.GONE);
                                }
                            }

                        } else {
                            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        }

                }
            }
            @Override
            public void onFailure(Call<API_RESPONSE> call, Throwable t) {
                try {
                    t.printStackTrace();
                } catch (Exception e) {
                }
            }
        });

    }

}
