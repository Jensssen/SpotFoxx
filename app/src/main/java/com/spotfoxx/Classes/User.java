package com.spotfoxx.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import java.util.ArrayList;


public class User {
    // store infos about user and make accessible in the app (not needed to be uploaded into db)
    private static String uuid;

    private static Context context;
    private static String spotify_user_name = "null";

    private static String spotifyDeviceId;
    private static ArrayList<String> alreadyPlacedSpotifyPlaylistUris = new ArrayList<>();


    public static ArrayList<String> getAlreadyPlacedSpotifyPlaylistUris() {
        return alreadyPlacedSpotifyPlaylistUris;
    }

    public static void setAlreadyPlacedSpotifyPlaylistUris(ArrayList<String> alreadyPlacedSpotifyPlaylistUris) {
        User.alreadyPlacedSpotifyPlaylistUris = alreadyPlacedSpotifyPlaylistUris;
    }

    public static void addUriToAlreadyPlacedSpotifyPlaylistUris(String uri) {
        User.alreadyPlacedSpotifyPlaylistUris.add(uri);
    }

    public static void removeUriFromAlreadyPlacedspotifyPlaylistUris(String uri) {
        if (User.alreadyPlacedSpotifyPlaylistUris.contains(uri)) {
            User.alreadyPlacedSpotifyPlaylistUris.remove(uri);
        }
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        User.context = context;
    }

    public static String getSpotifyDeviceId() {
        return spotifyDeviceId;
    }

    public static void setSpotifyDeviceId(String userSpotifyDeviceId) {
        User.spotifyDeviceId = userSpotifyDeviceId;
    }


    // Check internet connection available
    public static boolean isNetworkAvailable(Context context) {

        if (context == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
        if (uuid != null) {
            return uuid;
            //return "eoGDmXofEyQj1GuYj8h1jFxkjdB3";
        } else {
            throw new AssertionError("UUID cannot be null");
        }
    }

    public static void setUuid(String uuid) {
        if (uuid != null) {
            User.uuid = uuid;
        }
    }

    public static void setSpotify_user_name(String userId) {
        spotify_user_name = userId;
    }

    public static String getSpotify_user_name() {
        return spotify_user_name;
    }
}