package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.SettingInjectorService;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todo.ui.shopsList.ShopsListPermissionsChecker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    public static final int LOCALIZATION_SETTING_REQUEST = 1;
    public static final int FRAGMENT_LOCALIZATION_SETTING_REQUEST = 2;
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


    // shops
    public void goToShopsList(View view) {
        if (isGpsEnabled()) {
            requestPermissionsAndRunShopsListActivity();
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Uprawnienia aplikacji")
                    .setMessage("Ta funkcjonalność wymaga włączenia lokalizacji. Czy chcesz aktywować lokalizację?")
                    .setPositiveButton("Włącz lokalizację", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requireGps();
                        }
                    })
                    .setNegativeButton("Nie, dziękuję", null)
                    .show();
        }
    }

    public void requireGps() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, LOCALIZATION_SETTING_REQUEST);
    }

    private boolean isGpsEnabled() {
        LocationManager service = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (service != null) {
            return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return false;
    }

    private void requestPermissionsAndRunShopsListActivity() {
        ShopsListPermissionsChecker permissionsChecker = new ShopsListPermissionsChecker();
        permissionsChecker.checkPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        final int shopsPermissionsRequestCode = getResources().getInteger(R.integer.shop_permissions_request_code);
        if (requestCode == shopsPermissionsRequestCode) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted
                runShopsListActivity();
            } else {
                boolean shouldShowRationale = false;

                // Permissions deny - show rationale
                for (String permission: permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        shouldShowRationale = true;
                        break;
                    }
                }

                if (shouldShowRationale) {
                        new AlertDialog.Builder(this)
                                .setTitle("Uprawnienia aplikacji")
                                .setMessage("Te uprawnienia są wymagane do działania listy sklepów. Jeśli chcesz korzystać z jej funkcjonalności, przyznaj uprawnienia do lokalizacji")
                                .setPositiveButton("Spróbuj ponownie", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissionsAndRunShopsListActivity();
                                    }
                                })
                                .setNegativeButton("Nie, dziękuję", null)
                                .show();
                }
            }
        }
        // other 'if' lines to check for other
        // permissions this app might request.
    }

    private void runShopsListActivity() {
        Intent shopsIntent = new Intent(this, ShopsActivity.class);
        startActivity(shopsIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case LOCALIZATION_SETTING_REQUEST:
                if (isGpsEnabled()) {
                    requestPermissionsAndRunShopsListActivity();
                }
                else {
                    new AlertDialog.Builder(this)
                            .setTitle("Uprawnienia aplikacji")
                            .setMessage("Włącznie lokalizacji jest wymagane do działania tej funkcji.")
                            .setPositiveButton("Spróbuj ponownie", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requireGps();
                                }
                            })
                            .setNegativeButton("Nie, dziękuję", null)
                            .show();
                }

                break;
        }
    }
}
