package de.eis.muellerkimmeyer.aquaapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


import static com.google.android.gms.internal.zzs.TAG;

/*
 *  EISWS1617
 *
 *  Proof of Concept - Android App (Benutzer Client)
 *
 *  Autor: Moritz Müller
 *
 *  FCM Doku: https://firebase.google.com/docs/cloud-messaging/android/client
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    ServerRequest request;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken){

        request = new ServerRequest();
        String urlParameters = "{\"Token\": \""+ refreshedToken +"\"}";
        request.doAsyncRequest("post", "http://eis1617.lupus.uberspace.de/nodejs/users", urlParameters);

    }

}
