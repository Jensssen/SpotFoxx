package com.spotfoxx.Classes;

import android.content.Context;
import android.widget.Toast;

import com.spotfoxx.R;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;


public class SpotifyControl {

    private static SpotifyAppRemote mSpotifyAppRemote;
    private static String SpotifyAccessToken = "";
    private static final int MAX_RECONNECT_ATTEMPTS = 3;
    private static int reconnect_attempts = 0;


    public static String getSpotifyAccessToken() {
        return SpotifyAccessToken;
    }

    public static void setSpotifyAccessToken(String spotifyAccessToken) {
        SpotifyAccessToken = spotifyAccessToken;
    }

    public static void setmSpotifyAppRemote(final Context context) {
        ConnectionParams connectionParams = new ConnectionParams.Builder(Constants.SPOTIFY_CLIENT_ID)
                .setRedirectUri(Constants.SPOTIFY_REDIRECT_URI)
                .showAuthView(true)
                .build();
        // Connect to Spotify API and subscribe to spotify player (eg. for position in song)
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);   // disconnect from spotify
        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                    }

                    public void onFailure(Throwable throwable) {
                        SpotifyControl.try_to_reconnect(context);
                    }
                });
    }

    public static void try_to_reconnect(Context context){

        if(reconnect_attempts < MAX_RECONNECT_ATTEMPTS){
            ConnectionParams connectionParams = new ConnectionParams.Builder(Constants.SPOTIFY_CLIENT_ID)
                    .setRedirectUri(Constants.SPOTIFY_REDIRECT_URI)
                    .showAuthView(true)
                    .build();
            // Connect to Spotify API and subscribe to spotify player (eg. for position in song)
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);   // disconnect from spotify
            SpotifyAppRemote.connect(context, connectionParams,
                    new Connector.ConnectionListener() {
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;
                            reconnect_attempts = 0;
                        }

                        public void onFailure(Throwable throwable) {
                            reconnect_attempts += 1;
                        }
                    });
        } else{
            Toast.makeText(context, "Max number of reconnects reached", Toast.LENGTH_LONG).show();
        }
    }

    public static SpotifyAppRemote getmSpotifyAppRemote(Context context) {
        if (mSpotifyAppRemote != null) {
            if (mSpotifyAppRemote.isConnected() == false) {
                // If SpotifyAppRemote is not null but not connected, retry connection attempt
                setmSpotifyAppRemote(context);
                Toast no_spotify_connection_toast = Toast.makeText(context
                        , R.string.no_spotify_connection
                        , Toast.LENGTH_SHORT);
                no_spotify_connection_toast.show();
            }
        } else {
            // If mSpotifyAppRemote is null, retry spotify connection attempt
            setmSpotifyAppRemote(context);
            Toast mSpotifyAppRemote_is_null = Toast.makeText(context
                    , R.string.no_spotify_null
                    , Toast.LENGTH_SHORT);
            mSpotifyAppRemote_is_null.show();
        }
        return mSpotifyAppRemote;
    }
}