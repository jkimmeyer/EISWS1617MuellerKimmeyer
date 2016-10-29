package de.eis.muellerkimmeyer.aquaapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by moritz on 29.10.2016.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /*
    * QUELLENANGABE
    * sendRegistrationToServer übernommen aus https://www.youtube.com/watch?v=LiKCEa5_Cs8
    * Autor: Filip Vujovic, Abruf am: 29.10.2016
    * Änderungen: Header "content-type" hinzugefügt
    */

    private void sendRegistrationToServer(String refreshedToken){

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token",refreshedToken)
                .build();

        Request request = new Request.Builder()
                .url("http://eis1617.lupus.uberspace.de/nodejs/")
                .addHeader("content-type", "application/json")
                .post(body)
                .build();

        try{
            client.newCall(request).execute();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

}
