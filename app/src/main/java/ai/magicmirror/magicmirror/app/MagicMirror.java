package ai.magicmirror.magicmirror.app;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MagicMirror extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
