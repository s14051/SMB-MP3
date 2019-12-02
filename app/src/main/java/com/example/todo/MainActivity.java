package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fa = FirebaseAuth.getInstance();

        if (fa.getCurrentUser() == null){
            openCredentialsWindow();
        }
        else {
            Toast.makeText(MainActivity.this, "Zalogowano użytkownika " + fa.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openCredentialsWindow() {
        final View credentialsView = this.getLayoutInflater().inflate(R.layout.credentials, null);
        final EditText emailEditText = credentialsView.findViewById(R.id.emailEditText);
        final EditText passwordEditText = credentialsView.findViewById(R.id.passwordEditText);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Witaj")
                .setMessage("Zaloguj/zarejestruj się")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String email = emailEditText.getText().toString();
                        final String password = passwordEditText.getText().toString();

                        if (email.length() == 0 || password.length() == 0){
                            authorize();
                        }
                        else {
                            fa.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(MainActivity.this, "Zalogowano pomyślnie", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(MainActivity.this, "Rozpoczęcie procedury rejestracji nowego użytkownika...", Toast.LENGTH_SHORT).show();

                                                fa.createUserWithEmailAndPassword(email, password)
                                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(MainActivity.this, "Utworzono konto i zalogowano", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else {
                                                                Toast.makeText(MainActivity.this, "Tworzenie konta nie powiodło się", Toast.LENGTH_SHORT).show();
                                                            }

                                                            authorize();
                                                        }
                                                    });
                                            }
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        authorize();
                    }
                })
                .setView(credentialsView)
                .show();
    }

    private void authorize() {
        if (fa.getCurrentUser() == null) {
            openCredentialsWindow();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferencesHelper.useSavedPreferencesForFont(this, findViewById(R.id.goToListButton), findViewById(R.id.goToPreferencesButton));
    }

    public void goToList(View view) {
        Intent list = new Intent(this, ListActivity.class);
        startActivity(list);
    }

    public void goToPreferences(View view) {
        Intent preferences = new Intent(this, PreferencesActivity.class);
        startActivity(preferences);
    }

    public void logout(View view) {
        fa.signOut();
        openCredentialsWindow();
    }
}
