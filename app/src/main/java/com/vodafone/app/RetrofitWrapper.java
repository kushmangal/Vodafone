package com.vodafone.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitWrapper {
    public RetrofitWrapper(){
    }

    public static Retrofit getRetrofitRequest(@NonNull final Context mContext, @NonNull final SharedPreferences shared_preferences) {

        final String TAG = "RetrofitWrapper";

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override public void log(String message) {
                if(message.contains("\"auth_token\":"))
                {
                    try {
                        JSONObject obj = new JSONObject(message);
                        if(obj!=null && obj.get("auth_token")!=null)
                        {
                            String  tokenMessage = obj.get("auth_token").toString();
                            if(!tokenMessage.equals(""))
                            {
                                JSONObject tokenObject = new JSONObject(tokenMessage);
                                if(tokenObject!=null && tokenObject.get("token")!=null)
                                {
                                    String  token = tokenObject.get("token").toString();
                                    if(!token.equals(""))
                                    {
                                        if (token != null) {
                                            if (BuildConfig.DEBUG)
                                                Log.d(TAG, "auth_token: " + token);
                                        }

                                        if(shared_preferences!=null)
                                        {
                                            refreshToken(shared_preferences,token);
                                        }

                                    }
                                }
                            }
                        }

                    } catch (Throwable t) {
                        Log.e(TAG, "Could not parse refresh token:");
                    }
                }
                Log.d(TAG, "response: " + message);
            }
        });

        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(logInterceptor);

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", shared_preferences.getString("auth_token", ""))
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                Response response = chain.proceed(request);
                if (request != null) {
                    if (BuildConfig.DEBUG) {
                        Log.d("dc_token",shared_preferences.getString("auth_token", ""));
                        Log.d(TAG, "request: " + response.toString());
                    }
                }

                if (response.code() > 0) {
                    switch (response.code()) {
                        case 400: // Bad Request
                            break;
                        case 401: // Forbidden
                        case 403: // Forbidden
                            break;
                        case 404: // Not Found
                            break;
                        case 408: // Request Timeout
                            break;
                        case 429: // Too Many Requests
                            break;
                        case 500: // Internal Server Error
                            break;
                        case 302: // Invalid token
                            promptLogin(mContext,shared_preferences);
                            break;
                        case 200: // success
                            break;
                    }
                }

                return response;
            }
        });
        OkHttpClient okClient = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getResources().getString(R.string.apiUrl))
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }


    private static void promptLogin(final Context mContext,final SharedPreferences sharedPreferences)
    {
        try
        {
            SharedPreferences.Editor shared_preferences_editor  = sharedPreferences.edit();
            shared_preferences_editor.putString("uid","0");
            shared_preferences_editor.putBoolean("login_status",false);
            shared_preferences_editor.commit();

            Intent intent_login = new Intent(mContext, UserActivity.class);
            intent_login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(intent_login);

        }catch (Exception e)
        {

        }

    }


    private static void refreshToken(final SharedPreferences sharedPreferences,final String token)
    {
        try
        {
            SharedPreferences.Editor shared_preferences_editor  = sharedPreferences.edit();
            shared_preferences_editor.putString("token", token);
            shared_preferences_editor.commit();
        }catch (Exception e)
        {

        }
    }
}

