package honig.roey.student.roeysigninapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private MainActivity parentActivity;
    private String fName;
    private String lName;
    private String eml;
    private String pas;
    private FirebaseAuth mAuth;



    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        parentActivity = (MainActivity) getActivity();
        mAuth = FirebaseAuth.getInstance();


        // Setup widgets
        EditText etFirstName = view.findViewById(R.id.etFirstName);
        EditText etLastName = view.findViewById(R.id.etLastName);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);
        EditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        // Setup the button for signingUo
        Button btSignUp = view.findViewById(R.id.btSignUp);
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fName = etFirstName.getText().toString();
                lName = etLastName.getText().toString();
                eml = etEmail.getText().toString();
                pas = etPassword.getText().toString();

                createNewUser(eml,pas);

            }
        });

        // Setup Button to return to login screen
        Button returnToLogin = view.findViewById(R.id.returnToLogin);
        returnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentActivity.switchToFragment(parentActivity.getLoginFragment());
            }
        });

        return  view;

    }

    private void createNewUser(String eml, String pas) {

        mAuth.createUserWithEmailAndPassword(eml, pas)
                .addOnCompleteListener(parentActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updaeDetails(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(parentActivity, "Authentication failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void updaeDetails(FirebaseUser user) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fName+" "+lName)
                .setPhotoUri(Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/Creative-Tail-People-man.svg/128px-Creative-Tail-People-man.svg.png"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Safe To SignInto the App
                            //signIntoTheApp(eml,pas);

                            Intent intent = new Intent(getContext(), NavDrawer.class);
                            // this extra is for letting the NavDrawer activity know it was redirected from a SignupActivity, meaning there is a user
                            intent.putExtra("arg1",true);
                            startActivity(intent);

                        }
                    }
                });

    }

    /*

    private void signIntoTheApp(String eml, String pas) {

        mAuth.signInWithEmailAndPassword(eml, pas)
                .addOnCompleteListener(parentActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();


                            Intent intent = new Intent(getContext(), NavDrawer.class);
                            // this extra is for letting the NavDrawer activity know it was redirected from a SignupActivity, meaning there is a user
                            intent.putExtra("arg1",true);
                            startActivity(intent);


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(parentActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
    */


}
