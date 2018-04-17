package honig.roey.student.roeysigninapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private WelcomeFragment welcomeFragment = new WelcomeFragment(); // Fragment
    private LoginFragment loginFragment = new LoginFragment();       // Fragment

    Handler handler = new Handler();
    Runnable switchToLogin = new Runnable() {
        @Override
        public void run() {
            switchToFragment(loginFragment);
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchToFragment(welcomeFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(switchToLogin, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if unremoved while activity onPause (like when the user is scrolling all open Apps)
        // a featel exception occuers when trying to switch to the login Fragment
        handler.removeCallbacks(switchToLogin);
    }

    private void switchToFragment(android.support.v4.app.Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragContainer,fragment).commit();
    }


}

