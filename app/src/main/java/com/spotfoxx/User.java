package com.spotfoxx;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class User {
    // store infos about user and make accessible in the app (not needed to be uploaded into db)
    private static String uuid;
    private static int current_broadcast_state;
    private static String current_party_playlist_uri;
    public static void setCurrent_party_track_position(int current_party_track_position) {
        User.current_party_track_position = current_party_track_position;
    }
    private static Context context;
    private static int current_party_track_position;
    private static final int MAX_NUMBER_OF_SONGS = 10;
    private static SharedPreferences user_shared_preferences;
    private static String spotify_user_name = "null";
    private static String userPlaylistId; // Playlist URI of the Backster:user playlist
    private static Boolean user_listen_live_state = false;
    private static final String SPOTIFY_URI_SEPARATOR = ":,:";

    private static String spotifyDeviceId;
    private static long party_start_sync_atom_time;


    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        User.context = context;
    }

    public static String getCurrent_party_playlist_uri() {
        return current_party_playlist_uri;
    }

    public static void  setCurrent_party_playlist_uri(String current_party_playlist_uri) {
        User.current_party_playlist_uri = current_party_playlist_uri;
    }

    public static String getSpotifyDeviceId() {
        return spotifyDeviceId;
    }

    public static void setSpotifyDeviceId(String userSpotifyDeviceId) {
        User.spotifyDeviceId = userSpotifyDeviceId;
    }


    public static Boolean getUser_listen_live_state() {
        return user_listen_live_state;
    }

    public static void setUser_listen_live_state(Boolean user_listen_live_state) {
        User.user_listen_live_state = user_listen_live_state;
    }

    public static String getSpotifyUriSeparator() {
        return SPOTIFY_URI_SEPARATOR;
    }

    public static String getUserPlaylistId() {
        return userPlaylistId;
    }

    public static void setUserPlaylistId(String userPlaylistId) {
        User.userPlaylistId = userPlaylistId;
    }

    public static SharedPreferences getUser_shared_preferences() {
        return user_shared_preferences;
    }

    public static void setUser_shared_preferences(SharedPreferences user_shared_preferences) {
        User.user_shared_preferences = user_shared_preferences;
    }

    // Check internet connection available
    public static boolean isNetworkAvailable(Context context) {

        if (context == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static String getUuid() {
        if (uuid != null){
            return uuid;
            //return "eoGDmXofEyQj1GuYj8h1jFxkjdB3";
        }
        else{
            throw new AssertionError("UUID cannot be null");
        }
    }

    public static void setUuid(String uuid) {
        if (uuid != null) {
            User.uuid = uuid;
        }
    }

    public static int getCurrent_broadcast_state() {
        return current_broadcast_state;
    }
    public static void setCurrent_broadcast_state(int current_broadcast_state) {
        User.current_broadcast_state = current_broadcast_state;
    }

    public static void remove_all_songs_from_user_song_list(){
        user_shared_preferences.edit().remove("user_song_list").commit();
    }

    public static void setSpotify_user_name(String userId){
        spotify_user_name = userId;
    }

    public static String getSpotify_user_name(){
        return spotify_user_name;
    }

    public static long getParty_start_sync_atom_time() {
        return party_start_sync_atom_time;
    }

    public static void setParty_start_sync_atom_time(long party_start_sync_atom_time) {
        User.party_start_sync_atom_time = party_start_sync_atom_time;
    }
}