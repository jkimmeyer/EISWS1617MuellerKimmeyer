package eis1617.muellerkimmeyer.app.helper;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


import static com.google.android.gms.internal.zzs.TAG;

/*
 *  EISWS1617
 *
 *  Proof of Concept - Android App (Benutzer Client)
 *
 *  Autor: Moritz Müller, Johannes Kimmeyer
 *
 *  FCM Doku: https://firebase.google.com/docs/cloud-messaging/android/client
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        /* Wenn der Benutzer zum Beispiel die Daten der App löscht
         * und beim nächsten Start einen neuen Token bekommt,
         * soll noch nichts passieren, da er dann auch noch
         * nicht eingeloggt ist. Der neue Token wird erst
         * nach dem Login an die Datenbank geschickt.
         */
    }

}
