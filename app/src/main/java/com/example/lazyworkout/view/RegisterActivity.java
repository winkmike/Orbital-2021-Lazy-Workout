package com.example.lazyworkout.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lazyworkout.R;
import com.example.lazyworkout.model.User;
import com.example.lazyworkout.util.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private ImageView logo;
    private TextView txtSignup, txtPaT;
    private TextInputLayout txtInputFieldName, txtInputFieldEmail, txtInputFieldPassword;
    private TextInputEditText inputName, inputEmail, inputPassword;
    private Button registerBtn;
    private RelativeLayout registerView;

    private FirebaseAuth mAuth;
    private Database db = new Database();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        initViews();

        logo.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        txtSignup.setOnClickListener(this);
        txtPaT.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void initViews() {
        Log.d(TAG, "initViews: started");

        logo = findViewById(R.id.registerLogo);

        txtInputFieldName = findViewById(R.id.registerInputFieldName);
        txtInputFieldEmail = findViewById(R.id.registerInputFieldEmail);
        txtInputFieldPassword = findViewById(R.id.registerInputFieldPw);

        inputName = findViewById(R.id.registerInputName);
        inputEmail = findViewById(R.id.registerInputEmail);
        inputPassword = findViewById(R.id.registerInputPw);

        registerBtn = findViewById(R.id.registerCreateAccountBtn);

        txtSignup = findViewById(R.id.registerSignup);
        txtPaT = findViewById(R.id.registerPaT);

        registerView = findViewById(R.id.registerView);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.registerLogo):
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case (R.id.registerCreateAccountBtn):
                registerUser();
                break;
            case (R.id.registerSignup):
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case (R.id.registerPaT):
                Log.d("To be implemented", "privacy and term");
            default:
                break;
        }
    }

    private void registerUser() {

        if (!(validateUsername() && validateEmail() && validatePassword())) {
            inputName.setText("");
            inputEmail.setText("");
            inputPassword.setText("");
            return;
        } else {
            String name = inputName.getText().toString();
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                updateDispName(user, name);
                                User newUser = new User(user.getUid(), name);
                                db.createNewUser(newUser);
                                user.sendEmailVerification();
                                Log.d(TAG, "username: " + name);
                                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(RegisterActivity.this)
                                        .setTitle("Register Successful")
                                        .setMessage("Last step: Verify your email before logging in with your new account!")
                                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            }
                                        });
                                alert.show();

                            } else {
                                Log.w(TAG, task.getException().getMessage());

                                Snackbar snackbar = Snackbar.make(registerView, task.getException().getMessage(), Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Try Again", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                inputName.setText("");
                                                inputEmail.setText("");
                                                inputPassword.setText("");
                                            }
                                        });

                                View snackbarView = snackbar.getView();
                                TextView tv = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                                tv.setMaxLines(3);
                                snackbar.show();

                            }
                        }
                    });

        }
    }

    private void updateDispName(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    private boolean validateUsername() {
        String username = inputName.getText().toString().trim();

        if (username.isEmpty()) {
            txtInputFieldName.setError("Username is required");
            return false;
        } else {
            txtInputFieldName.setError(null);
            txtInputFieldName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty()) {
            txtInputFieldEmail.setError("Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtInputFieldEmail.setError("The email is invalid");
            return false;
        } else {
            txtInputFieldEmail.setError(null);
            txtInputFieldEmail.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = inputPassword.getText().toString().trim();

        if (password.isEmpty()) {
            txtInputFieldPassword.setError("Password is required");
            return false;
        } else {
            txtInputFieldPassword.setError(null);
            txtInputFieldPassword.setErrorEnabled(false);
            return true;
        }
    }


}