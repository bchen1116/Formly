package com.example.bryanchen.formations;

import android.content.Intent;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static int SPLASH_TIME_OUT = 1500;
    public int numLoadedFrags = 0;
    private List<String> formationNames = new ArrayList<>();

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

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.d("AUTH", user.getDisplayName() + ", " + user.getUid() + ", " + user.getEmail());


            db.collection("Users").document(user.getUid()).collection("Formation List").document("lol")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.get("list") != null) {
                                formationNames = (List<String>) documentSnapshot.get("list");
                            }
                        }
                    });





            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startPageIntent = new Intent(SplashScreen.this, StartPage.class);
                    startPageIntent.putExtra("numFrags", numLoadedFrags);
                    startPageIntent.putExtra("Formation Names", formationNames);
                    startActivity(startPageIntent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }
}
