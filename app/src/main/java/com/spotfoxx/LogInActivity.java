package com.spotfoxx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spotfoxx.Classes.AlertDialogCreator;
import com.spotfoxx.Classes.Constants;
import com.spotfoxx.Classes.SpotifyControl;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.spotfoxx.Classes.SpotifyWebApi;
import com.spotfoxx.Classes.User;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;


public class LogInActivity extends AppCompatActivity {
    // Firebase Setup
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference db_reference = database.getReference();

    Map<String, Object> new_values = new HashMap<>();

    List<AuthUI.IdpConfig> providers;
    private String user_name;
    AlertDialogCreator dialog;
    Intent login_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        dialog = new AlertDialogCreator();
        login_intent = getIntent();

        if (User.isNetworkAvailable(this)==true) {

            if (checkPackageIsInstalled(Constants.SPOTIFY_PACKAGE_NAME)==false) {
                dialog.createNoSpotifyAlert(this, Constants.SPOTIFY_PACKAGE_NAME, Constants.REFERRER_UTM).show();
            } else {
                // Set shared preferences object in User class, in order to access it from different parts of the project
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES_NAME, MODE_PRIVATE);
                User.setUser_shared_preferences(sharedPreferences);
                FirebaseUser db_user = FirebaseAuth.getInstance().getCurrentUser();

                // If user has recently signed in, no new sign for firebase in required.
                if (db_user!=null) {
                    User.setUuid(db_user.getUid());
                    init_default_values_in_db();                    // Init user db on app start
                } else {
                    // Perform a fresh firebase login.
                    providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());
                    showSignInOptions();
                }

                // Initialize spotifyAppRemote for SpotifyPollService
                SpotifyControl.setmSpotifyAppRemote(getApplicationContext());

                // Send auth request to spotify
                final AuthorizationRequest request = new AuthorizationRequest.Builder(Constants.SPOTIFY_CLIENT_ID, AuthorizationResponse.Type.TOKEN, Constants.SPOTIFY_REDIRECT_URI)
                        .setScopes(new String[]{"playlist-read","playlist-modify-public", "playlist-modify-private", "user-read-playback-state", "user-modify-playback-state"})
                        .build();

                //"playlist-read", "playlist-modify-public, playlist-modify-private", "user-read-playback-state", "user-modify-playback-state"
                AuthorizationClient.openLoginActivity(this, Constants.SPOTIFY_REQUEST_CODE, request);
            }
        } else {
            dialog.createNoWifiAlert(this, login_intent).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handles answer, that is received either from firebase or spotify login request.
        if (requestCode==Constants.FIREBASE_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            FirebaseAuth.getInstance().signInAnonymously();
            if (resultCode==RESULT_OK) {
                // ensure DB is setup correctly
                FirebaseUser db_user = FirebaseAuth.getInstance().getCurrentUser();
                String uuid = db_user.getUid();

                // CHeck if uuid is null -> This might happen if the internet is really slow
                if (uuid==null) {
                    // Redo login if uuid is null
                    dialog.createNoWifiAlert(this, login_intent).show();
                }
                // Set User UUID - set by firebase
                User.setUuid(uuid);

                // Init user db on app start.
                init_default_values_in_db();

            } else if (response!=null) {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_LONG).show();

                // Missing network registered during login
                if (response.getError().getErrorCode()==ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_toast, Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                // Sign in failed or user pressed back button
                finish();
            }
        }

        // Check if result comes from the correct activity
        if (requestCode==Constants.SPOTIFY_REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Initialize user song list in User class from shared preferences
                    SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

                    // Handle successful response
                    SpotifyControl.setSpotifyAccessToken(response.getAccessToken());
                    SpotifyWebApi.setSpotifyWebAPIClient(); // Initialize the web api client with Retrofit

                    // Check if user name is null (first run) if that is true, update db and shared pref
                    user_name = defaultSharedPreferences.getString(getString(R.string.shared_pref_key_user_name), "null");
                    User.setSpotify_user_name(user_name); // Set spotify user name for general usage inside of the app!
                    if (user_name.equals("null")) {
                        SpotifyWebApi.getSpotifyUsername(getApplicationContext());
                    }
                    SpotifyWebApi.getDeviceID(getApplicationContext());



                    // continue to main screen
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    break;

                // Auth flow returned an error
                case ERROR:
                    if (response!=null) {
                        Toast.makeText(this, "" + response.getError(), Toast.LENGTH_LONG).show();
                        break;
                    }

                    // Most likely auth flow was cancelled
                default:
                    // continue to main screen
                    Toast.makeText(getApplicationContext(), "User authentication failed", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    // todo: Handle other cases
            }
        }
    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .enableAnonymousUsersAutoUpgrade()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.mipmap.ic_launcher)
                        .build(), Constants.FIREBASE_REQUEST_CODE
        );
    }

    private void init_default_values_in_db() {
        // Initialize user song list in User class from shared preferences
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Get user name from shared pref. null at first run, will be set later
        // todo: use response.isNewUser()  to decide whether to query spotify for user name or not
        user_name = defaultSharedPreferences.getString(getString(R.string.shared_pref_key_user_name), "null");

        // Get backster playlist id from shared pref. At first run it is null but it will be set later
        String backster_playlist_id = defaultSharedPreferences.getString(getString(R.string.shared_pref_key_user_playlist_id), "null");

        new_values.put(Constants.uuid, User.getUuid());
        new_values.put(Constants.user_name, user_name);
        new_values.put(Constants.party_playlist_uri, "-");
        new_values.put(Constants.lobby_playlist_uri, backster_playlist_id);
        DatabaseReference userRef = db_reference.child(Constants.user).child(User.getUuid());
        userRef.child(Constants.user).setValue(new_values);
    }

    private boolean checkPackageIsInstalled(String PackageName) {
        PackageManager pm = getPackageManager();
        boolean isPackageInstalled;
        try {
            pm.getPackageInfo(PackageName, 0);
            isPackageInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isPackageInstalled = false;
        }
        return isPackageInstalled;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Constants.LOGIN_ACTIVITY_TAG, "onDestroy");
    }
}