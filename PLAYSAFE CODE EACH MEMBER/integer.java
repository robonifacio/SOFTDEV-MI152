package apc.edu.ph.playsafe;

import android.app.Application;

import com.firebase.client.Firebase;

public class integer extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
