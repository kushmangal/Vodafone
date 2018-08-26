package com.vodafone.app;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.narayanacharya.waveview.WaveView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIContext;
import ai.api.model.AIEvent;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Entity;
import ai.api.model.EntityEntry;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        RecognitionListener {

    @Bind(R.id.waveView)
    WaveView sine;

    @Bind(R.id.button)
    Button offers;

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private FloatingActionButton toggleButton;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    SharedPreferences pref;
    SharedPreferences.Editor pref_editor;
    List<Animator> animators = new ArrayList<>();
    private AnimatorSet mAnimatorSet;
    static TextToSpeech obj;
    String uid = "";
    static Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toggleButton = (FloatingActionButton) findViewById(R.id.toggleButton1);
        obj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                obj.setLanguage(Locale.ENGLISH);
            }
        });
        mContext = getApplicationContext();
        pref = getSharedPreferences("VF_Pref", Context.MODE_PRIVATE);
        pref_editor = pref.edit();

        if (pref != null)
            uid = pref.getString("uid", "");
        Toast.makeText(getApplicationContext(), uid, Toast.LENGTH_SHORT).show();

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions
                        (MainActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                REQUEST_RECORD_PERMISSION);
            }
        });

        offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), OffersActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    inititlizeSpeech();
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    private void inititlizeSpeech() {
        if (speech!=null)
            speech.destroy();
        speech=null;
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }


    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        toggleButton.setVisibility(View.GONE);
        sine.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        sine.setVisibility(View.GONE);
        toggleButton.setVisibility(View.VISIBLE);
        speech.stopListening();
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        speech.stopListening();
        sine.setVisibility(View.GONE);
        toggleButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";


        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(String.valueOf(matches.get(0))+" "+uid);
        new MyTask().execute(aiRequest);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    private static class MyTask extends AsyncTask<AIRequest, Void, AIResponse> {
        final AIConfiguration config = new AIConfiguration("c46d686cc3174bd1a9736149f4ed3866",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        final AIDataService aiDataService = new AIDataService(mContext, config);

        @Override
        protected AIResponse doInBackground(AIRequest... requests) {
            final AIRequest request = requests[0];
            try {
                final AIResponse response = aiDataService.request(request);
                return response;
            } catch (Exception e) {
                Log.d("check", "doInBackground: ");
            }
            return null;
        }

        @Override
        protected void onPostExecute(AIResponse aiResponse) {
            String target_id = "";
            int target_page = -1;

            if (aiResponse != null) {
                if (aiResponse.getResult() != null && aiResponse.getResult().getFulfillment() != null && aiResponse.getResult().getFulfillment().getSpeech() != null) {
                    obj.speak(aiResponse.getResult().getFulfillment().getSpeech(), TextToSpeech.QUEUE_FLUSH, null);
                    if (aiResponse.getResult() != null && aiResponse.getResult().getFulfillment() != null && aiResponse.getResult().getFulfillment().getData() != null) {

                        if (aiResponse.getResult().getFulfillment().getData().get("target_id") != null)
                            target_id = aiResponse.getResult().getFulfillment().getData().get("target_id").getAsString();

                        if (aiResponse.getResult().getFulfillment().getData().get("target_page") != null)
                            target_page = (int) Double.parseDouble(aiResponse.getResult().getFulfillment().getData().get("target_page").getAsString());
                        Intent in;
                        switch (target_page) {
                            case 1:
                                in = new Intent(mContext, OffersActivity.class);
                                in.putExtra("target_id", target_id);
                                in.putExtra("target_page", target_page);
                                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(in);
                                break;

                            case 2:
                                in = new Intent(mContext, ProfileActivity.class);
                                in.putExtra("target_id", target_id);
                                in.putExtra("target_page", target_page);
                                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(in);
                                break;

                        }
                    }
                    // process aiResponse here
                }
            }
        }
    }
}