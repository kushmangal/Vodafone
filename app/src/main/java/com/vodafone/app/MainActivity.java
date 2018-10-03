package com.vodafone.app;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.narayanacharya.waveview.WaveView;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        RecognitionListener {

    @Bind(R.id.waveView)
    WaveView sine;

    static TextView recieved;
    @Bind(R.id.user_image)
    ImageView userImage;
    @Bind(R.id.text)
    TextView text;
    @Bind(R.id.text2)
    TextView text2;
    @Bind(R.id.text3)
    TextView text3;
    @Bind(R.id.text4)
    TextView text4;
    @Bind(R.id.text5)
    TextView text5;
    @Bind(R.id.text6)
    TextView text6;
    static RelativeLayout mainLayout;
    @Bind(R.id.sent)
    TextView sent;
    @Bind(R.id.record)
    MaterialIconView record;
    private SpeechRecognizer speech = null;
    private static final int REQUEST_RECORD_PERMISSION = 100;
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
        recieved = findViewById(R.id.recieved);
        mainLayout = findViewById(R.id.main_layout);
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

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions
                        (MainActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                REQUEST_RECORD_PERMISSION);
            }
        });

        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(text3.getText().toString());
            }
        });
        text4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(text4.getText().toString());
            }
        });
        text5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(text5.getText().toString());
            }
        });
        text6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(text6.getText().toString());
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
        sent.setText("");
        recieved.setText("");
        sent.setVisibility(View.GONE);
        recieved.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        if (speech != null)
            speech.destroy();
        speech = null;
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
        record.setVisibility(View.GONE);
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
        record.setVisibility(View.VISIBLE);
        speech.stopListening();
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        if (speech != null)
            speech.destroy();
        record.setVisibility(View.VISIBLE);
        sine.setVisibility(View.GONE);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
    }

    @Override
    public void onPartialResults(Bundle arg0) {
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        search(matches.get(0));

    }

    private void search(String s) {
        if (speech != null) {
            speech.destroy();
        }
        record.setVisibility(View.VISIBLE);
        sine.setVisibility(View.GONE);
        if (s != null && s.length() > 0) {
            sent.setVisibility(View.VISIBLE);
            sent.setText(s);
            mainLayout.setVisibility(View.GONE);
        }
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(String.valueOf(s) + " " + uid);
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
                    recieved.setVisibility(View.VISIBLE);
                    recieved.setText(aiResponse.getResult().getFulfillment().getSpeech());
                    mainLayout.setVisibility(View.GONE);
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