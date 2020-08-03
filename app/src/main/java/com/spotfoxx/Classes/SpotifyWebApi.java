package com.spotfoxx.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;


import com.spotfoxx.Interfaces.SpotifyClient;
import com.spotfoxx.R;
import com.spotfoxx.SpotifyClasses.AlbumInfo;
import com.spotfoxx.SpotifyClasses.CurrentPlaybackInformation;
import com.spotfoxx.SpotifyClasses.Device;
import com.spotfoxx.SpotifyClasses.Devices;
import com.spotfoxx.SpotifyClasses.GetPlaylistObj;
import com.spotfoxx.SpotifyClasses.Item;
import com.spotfoxx.SpotifyClasses.Item__;
import com.spotfoxx.SpotifyClasses.PlayRequest;
import com.spotfoxx.SpotifyClasses.Playlist;
import com.spotfoxx.SpotifyClasses.PlaylistInfo;
import com.spotfoxx.SpotifyClasses.PlaylistItem;
import com.spotfoxx.SpotifyClasses.UserObj;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpotifyWebApi {

    // todo: cleanup not finished here
    private static boolean playlistAlreadyPresent = false;
    private static SpotifyClient client;
    private final static String TAG = Constants.SPOTIFY_WEB_API_TAG;

    // Set Spotify client object of SpotifyClient interface
    public static void setSpotifyWebAPIClient() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Constants.SPOTIFY_API_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        client = retrofit.create(SpotifyClient.class);
    }


    public static void getSpotifyUsername(Context context) {

        String mAccessToken = SpotifyControl.getSpotifyAccessToken();
        Call call = client.getUserObj("Bearer " + mAccessToken);

        call.enqueue(new Callback<UserObj>() {
            @Override
            public void onResponse(Call<UserObj> call, Response<UserObj> response) {
                if (response.code() <= 304) {
                    UserObj userObj = response.body();
                    String name = userObj.getDisplayName();
                    User.setSpotify_user_name(name);
                    FirebaseControl.update_user_value(User.getUuid(), Constants.user_name, name);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    preferences.edit().putString(context.getString(R.string.shared_pref_key_user_name), name).apply();
                } else {
                    response_handler(context, response);
                }
            }

            @Override
            public void onFailure(Call call, Throwable throwable) {
                Toast.makeText(context.getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void playSongFromPlaylist(Context context, PlayRequest request) {
        // This method adds a list of spotify songs to an already existing playlist
        String mAccessToken = SpotifyControl.getSpotifyAccessToken();

        String device_id = User.getSpotifyDeviceId();
        String playlist = request.getContextUri();
        if (!playlist.contains("spotify")) {
            playlist = "spotify:" + playlist;
            request.setContextUri(playlist);
        }

        if (device_id.equals("-")) {
            Call call = client.playSongFromPlaylist("Bearer " + mAccessToken, request);   //todo: hanlde null pointer
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    response_handler(context, response);
                }

                @Override
                public void onFailure(Call call, Throwable throwable) {
                    Toast.makeText(context.getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Call call = client.playSongFromPlaylist(device_id, "Bearer " + mAccessToken, request);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    response_handler(context, response);
                }

                @Override
                public void onFailure(Call call, Throwable throwable) {
                    Toast.makeText(context.getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void getDeviceID(Context context) {
        // This method gets all spotify device ids from user
        String mAccessToken = SpotifyControl.getSpotifyAccessToken();

        Call call = client.getDeviceID("Bearer " + mAccessToken);
        call.enqueue(new Callback<Devices>() {
            @Override
            public void onResponse(Call<Devices> call, Response<Devices> response) {
                if (response.code() > 304) {
                    response_handler(context, response);
                    User.setSpotifyDeviceId("-");
                } else {
                    Devices devices = response.body();
                    User.setSpotifyDeviceId("-");

                    for (Device device : devices.getDevices()) {
                        if (device.getType().equals("Smartphone")) {
                            User.setSpotifyDeviceId(device.getId());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable throwable) {
                Toast.makeText(context.getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //todo: improve what telling user when (ie more helpful instructions if possible)
    public static void response_handler(Context context, Response response) {
        if (response.code() == 404) {    // bad request
            Toast.makeText(context.getApplicationContext(), "404 - Not Found" + response, Toast.LENGTH_SHORT).show();
        } else if (response.code() == 400) {               // bad request - see message
            Toast.makeText(context.getApplicationContext(), response.message(), Toast.LENGTH_SHORT).show();
        } else if (response.code() == 401) {               // authentication issues
            Toast.makeText(context.getApplicationContext(), R.string.failed_authenticate, Toast.LENGTH_SHORT).show();
        } else if (response.code() == 403) {               // forbidden
            Toast.makeText(context.getApplicationContext(), response.message(), Toast.LENGTH_SHORT).show();
        } else if (response.code() == 429) {               // rate limit hit, ask user to retry
            Toast.makeText(context.getApplicationContext(), R.string.retry_later, Toast.LENGTH_SHORT).show();
        } else if (response.code() >= 500) {       // spotify seems to be unavailable
            Toast.makeText(context.getApplicationContext(), R.string.Spotify_unavailable, Toast.LENGTH_SHORT).show();
        }
    }
}

