package com.spotfoxx.Classes;

import android.content.Context;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class FirebaseControl {


    public static void like_marker(Context context, String marker_id, String user_name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference likeRef = database.getReference().child(Constants.location_prefix).child(marker_id).child("like");
        DatabaseReference dislikeRef = database.getReference().child(Constants.location_prefix).child(marker_id).child("dislike");

        // upload like
        Map<String, Object> new_values = new HashMap<>();
        new_values.put(user_name, "1");
        likeRef.updateChildren(new_values);

        // remove dislike
        dislikeRef.child(user_name).removeValue();

        Toast.makeText(context, "Marker has been liked", Toast.LENGTH_SHORT).show();
    }

    public static void dislike_marker(Context context, String marker_id, String user_name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dislikeRef = database.getReference().child(Constants.location_prefix).child(marker_id).child("dislike");
        DatabaseReference likeRef = database.getReference().child(Constants.location_prefix).child(marker_id).child("like");

        // upload dislike
        Map<String, Object> new_values = new HashMap<>();
        new_values.put(user_name, "1");

        dislikeRef.updateChildren(new_values);

        // remove like
        likeRef.child(user_name).removeValue();

        Toast.makeText(context, "Marker has been disliked", Toast.LENGTH_SHORT).show();
    }

    public static boolean add_marker_to_firebase(Context context, String name, String spotify_uri, double latitude, double longitude) {

        // Upload new location
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationRef = database.getReference().child(Constants.location_prefix).push();

        GeoFire geoFire = new GeoFire(locationRef);
        GeoLocation geoLocation = new GeoLocation(latitude, longitude);
        geoFire.setLocation("", geoLocation);

        Map<String, Object> new_location_values = new HashMap<>();
        new_location_values.put("name", name);
        new_location_values.put("spotify_uri", spotify_uri);
        new_location_values.put("user_name", User.getSpotify_user_name());
        locationRef.updateChildren(new_location_values);


        // Add playlist to users playlists

//        DatabaseReference userRef = database.getReference().child(Constants.user).child(User.getUuid()).child(Constants.playlist_marker);
//
//        Map<String, Object> new_user_values = new HashMap<>();
//        new_user_values.put("key", locationRef.getKey());
//        userRef.updateChildren(new_user_values);

        Toast.makeText(context, "Marker uploaded to DB", Toast.LENGTH_SHORT).show();
        return true;
    }

    public static void delete_location(String marker_id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationRef = database.getReference().child(Constants.location_prefix);
        locationRef.child(marker_id).removeValue();
    }



}
