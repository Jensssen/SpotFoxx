package com.spotfoxx.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Iterator;


public class User {
    // store infos about user and make accessible in the app (not needed to be uploaded into db)
    private static String uuid;

    private static Context context;
    private static String spotify_user_name = "null";

    private static String spotifyDeviceId;

    // ARRAYS: These arrays hold all marker related information and are essential to core functionalities
    private static ArrayList<String> alreadyPlacedSpotifyPlaylistUris = new ArrayList<>(); // Holds list of spotify uirs (ONLY THOSE, THAT ARE OWNED BY THE USER and in the exact order of firebase database)
    private static ArrayList<MyLocation> myLocations = new ArrayList<>(); // holds all location objs, inside of the search radius (those of the user and of all other users.)
    private static ArrayList<String> marker_ids = new ArrayList<>(); // holds one id for each visible marker. This id is needed to map one marker to one myLocation obj
    private static ArrayList<Marker> markers = new ArrayList<>(); // holds all placed markers
    private static ArrayList<Circle> circles = new ArrayList<>(); // holds all placed markers

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

    public static ArrayList<MyLocation> getMyLocations() {
        return myLocations;
    }

    public static void clearMyLocations() {
        User.myLocations.clear();
    }

    public static void setMyLocations(ArrayList<MyLocation> myLocations) {
        User.myLocations = myLocations;
    }

    public static void updateMyLocationByIndex(int index, MyLocation location) {

        User.myLocations.set(index, location);

    }

    public static void addLocationToMyLocations(MyLocation location) {
        User.myLocations.add(location);
    }

    public static void removeLocationFromMyLocations(String user_name, String spotify_uri) {
        Iterator<MyLocation> iter = User.myLocations.iterator();
        int index = 0;
        while (iter.hasNext()) {
            MyLocation location = iter.next();
            if (location.getUser_name().equals(user_name) & location.getSpotify_uri().equals(spotify_uri)) {
                //User.myLocations.remove(location);
                break;
            }
            index += 1;
        }
        User.myLocations.remove(index);
    }

    public static ArrayList<String> getMarker_ids() {
        return marker_ids;
    }

    public static void setMarker_ids(ArrayList<String> marker_ids) {
        User.marker_ids = marker_ids;
    }

    public static void addMarkerIdToMarkerIds(String id) {
        if(!User.marker_ids.contains(id)){
            User.marker_ids.add(id);
        }
    }

    public static void removeMarkerIdFromMarkerIds(int index) {
        User.marker_ids.remove(index);
    }

    public static void clearMarkerIds() {
        User.marker_ids.clear();
    }

    public static ArrayList<Marker> getMarkers() {
        return markers;
    }

    public static void setMarkers(ArrayList<Marker> markers) {
        User.markers = markers;
    }

    public static void addMarkerToMarkers(Marker marker) {
        User.markers.add(marker);
    }

    public static void removeMarkerFromMarkers(int index) {
            User.markers.remove(index);
    }

    public static void clearMarkers() {
        User.markers.clear();
    }

    public static ArrayList<Circle> getCircles() {
        return circles;
    }

    public static void setCircles(ArrayList<Circle> circles) {
        User.circles = circles;
    }

    public static void addCircleToCircles(Circle circle) {
        User.circles.add(circle);
    }

    public static void removeCircleFromCircles(int index) {
        User.circles.remove(index);
    }

    public static void clearCircles() {
        User.circles.clear();
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