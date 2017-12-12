package com.example.bryanchen.formations;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static int SPLASH_TIME_OUT = 1500;
    public int numLoadedFrags = 0;

    private static final int RC_SIGN_IN = 123;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView imageView = (ImageView) findViewById(R.id.titlePage);
        imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.logo));

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo)
                        .build(),
                RC_SIGN_IN);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                db.collection("User1").document("Num Frags")
//                        .get()
//                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                Log.d("num frags", "" + documentSnapshot.get("num"));
//                            }
//                        });
//
//                Intent startPageIntent = new Intent(SplashScreen.this, StartPage.class);
//                startActivity(startPageIntent);
//                finish();
//            }
//        }, SPLASH_TIME_OUT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.d("AUTH", user.getDisplayName() + ", " + user.getUid() + ", " + user.getEmail());

            db.collection(user.getUid()).document("Num Frags")
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Long temp = (Long) documentSnapshot.get("num");
                        numLoadedFrags = temp.intValue();
                        Log.d("existing user frags", "" + documentSnapshot.get("num"));
                    }
                    else {
                        Map<String, Integer> numFrags = new HashMap<>();
                        numFrags.put("num", 0);
                        db.collection(user.getUid()).document("Num Frags")
                                .set(numFrags)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("new user", "ye boi");
                                    }
                                });
                    }
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startPageIntent = new Intent(SplashScreen.this, StartPage.class);
                    startPageIntent.putExtra("numFrags", numLoadedFrags);
                    startActivity(startPageIntent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }
}
