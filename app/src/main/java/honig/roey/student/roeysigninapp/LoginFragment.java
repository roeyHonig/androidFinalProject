package honig.roey.student.roeysigninapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment{
    private MainActivity parentActivity;
    private static final int RC_SIGN_IN = 1; // Arbitary code
    private static final String TAG = "LoginFragment";
    private SignInButton btnGoogleSign; // SignInButton is a type of widget (Class) Google created, it is their Sign In Button
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText etEmailLogin;
    private EditText etPasswoedLogin;
    private Button btEmailPasswordSignIn;
    private int arg1Value = 0;

    // Getter
    public int getArg1Value() {
        return arg1Value;
    }

    public LoginFragment() {
        // Required empty public constructor
    }


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.login_screen, container, false);
        parentActivity = (MainActivity) getActivity();
        etEmailLogin = view.findViewById(R.id.etEmailLogin);
        etPasswoedLogin = view.findViewById(R.id.etPasswoedLogin);
        btEmailPasswordSignIn = view.findViewById(R.id.btEmailPasswordSignIn);

        btEmailPasswordSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arg1Value = 2;
                signIntoTheApp(etEmailLogin.getText().toString(),etPasswoedLogin.getText().toString());
            }
        });


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        btnGoogleSign = view.findViewById(R.id.btnGoogleSign);
        btnGoogleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arg1Value = 1;
                signIn();
                //Toast.makeText(getContext(),"you've pressed SignIn",Toast.LENGTH_LONG).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    Intent intent = new Intent(getContext(), NavDrawer.class);
                    // this extra is for letting the NavDrawer activity know it was redirected from a LoginActivity, meaning there is a user
                    intent.putExtra("arg1",arg1Value);
                    startActivity(intent);
                }
            }
        };


        MainActivity parentActivity = (MainActivity) getActivity();

        // Setup Button to switch to signup screen
        TextView btGoToSignupScreen = view.findViewById(R.id.btGoToSignupScreen);
        btGoToSignupScreen.setText(Html.fromHtml("Not a Member Yet?, go ahead and <u>signup</u>"));
        btGoToSignupScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.removeAuthStateListener(mAuthListener);
                parentActivity.switchToFragment(parentActivity.getSignUpFragment());
            }
        });

        return  view;
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    private void signIn() {
        @SuppressLint("RestrictedApi") Intent signInIntent =mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            @SuppressLint("RestrictedApi") Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                arg1Value =0; // reset
                Log.w(TAG, "Google sign in failed", e);
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            arg1Value = 1;
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Toast.makeText(getContext(),"hi "+user.getDisplayName(), Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                    }
                });

    }

    private void signIntoTheApp(String eml, String pas) {

        mAuth.signInWithEmailAndPassword(eml, pas)
                .addOnCompleteListener(parentActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(parentActivity, "Authentication failed: "+task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            arg1Value = 0; // reset
                        }

                    }
                });

    }

}

