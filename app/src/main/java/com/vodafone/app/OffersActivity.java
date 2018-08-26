package com.vodafone.app;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.diagonallayout.DiagonalLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OffersActivity extends AppCompatActivity {

    @Bind(R.id.offers_recycler)
    RecyclerView offers_recycler;

    @Bind(R.id.diagonalLayout)
    DiagonalLayout diagonalLayout;

    @Bind(R.id.cat_name)
    TextView cat_name;


    OffersAdapter adapter;
    SharedPreferences pref;
    SharedPreferences.Editor pref_editor;
    ArrayList<Offers_Response> offers;
    String target_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        ButterKnife.bind(this);

        target_id = getIntent().getStringExtra("target_id");
        pref = getSharedPreferences("VF_Pref", Context.MODE_PRIVATE);
        pref_editor = pref.edit();
        offers_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        adapter = new OffersAdapter();
        offers_recycler.setAdapter(adapter);
        fetchOffers();


    }

    void fetchOffers() {
        Retrofit retrofit = RetrofitWrapper.getRetrofitRequest(getApplicationContext(), pref);
        final API offers_api = retrofit.create(API.class);
        Call<API_RESPONSE> call = offers_api.fetchOffers(pref.getString("uid", ""), target_id);
        call.enqueue(new Callback<API_RESPONSE>() {
            @Override
            public void onResponse(Call<API_RESPONSE> call, Response<API_RESPONSE> response) {

                    API_RESPONSE data = response.body();
                    if (data != null) {
                        if (data.getStatus() == 1) {
                            if(data.getShow_title()==1) {
                                cat_name.setText(String.valueOf(target_id.charAt(0)).toUpperCase() + target_id.substring(1, target_id.length()));
                                diagonalLayout.setVisibility(View.VISIBLE);
                            }
                            else
                                diagonalLayout.setVisibility(View.GONE);

                            offers = data.getOffers();
                            offers_recycler.getAdapter().notifyDataSetChanged();
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

    public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.Holder> {

        public class Holder extends RecyclerView.ViewHolder {



            @Bind(R.id.offer_image)
            ImageView offer_image;

            @Bind(R.id.offer_title)
            TextView offer_title;

            @Bind(R.id.offer_desc)
            TextView offer_desc;

            public Holder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.offers_item, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final Offers_Response data = offers.get(position);
            if (data != null) {
                if (data.getImage_url() != null) {
                    holder.offer_image.setVisibility(View.VISIBLE);
                    Picasso.with(getApplicationContext()).load(data.getImage_url()).into(holder.offer_image);
                } else {
                    holder.offer_image.setVisibility(View.GONE);
                }
                holder.offer_title.setText(data.getTitle());
                holder.offer_desc.setText(data.getDescription());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(data.getQuestion()!=null){
                            final Dialog dialog = new Dialog(OffersActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.question_dialog);
                            final RadioGroup rg = (RadioGroup)dialog.findViewById(R.id.radio_group);
                            TextView question = (TextView)dialog.findViewById(R.id.questionTV);
                            Button btn = (Button)dialog.findViewById(R.id.redeem_btn);

                            if(data.getQuestion().getQuestion()!=null)
                            question.setText(data.getQuestion().getQuestion());

                            for (int i = 0; i < data.getQuestion().getOptions().size(); i++) {
                                RadioButton rdbtn = new RadioButton(OffersActivity.this);
                                rdbtn.setId(i);
                                rdbtn.setText(data.getQuestion().getOptions().get(i).getDisplay_name());
                                rg.addView(rdbtn);
                            }
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    rg.getCheckedRadioButtonId()
                                }
                            });
                            dialog.show();

                        }
                    }
                });

            }
        }


        @Override
        public int getItemCount() {
            if (offers == null)
                return 0;
            return offers.size();

        }
    }
}

