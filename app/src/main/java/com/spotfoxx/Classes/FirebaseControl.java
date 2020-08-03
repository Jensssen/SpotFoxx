package com.spotfoxx.Classes;

import android.content.Context;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class FirebaseControl {

    private String retrieved_value;

    // method retrieving variable for given user
    public String retrieveVariableValue(String uuid, String variable_name) {
        // method one time fetches specific variable for given user (uuid)

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference value_ref = myRef.child(Constants.user).child(uuid+ "/" + Constants.user_path + "/" + variable_name);

        // one time fetch value from firebase (async operation! execution time not guaranteed)
        value_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrieved_value = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DatabaseError raised: " + databaseError.getCode());
            }
        });

        return retrieved_value;
    }


    // method updates int value of a user in DB
    public static void update_user_value(String uuid, String value_name, int value) {
        Map<String, Object> new_values = new HashMap<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference().child(Constants.user).child(uuid).child(Constants.user_path);
        new_values.put(value_name, value);
        userRef.updateChildren(new_values);
    }
    // override
    public static void update_user_value(String uuid, String value_name, String value) {
        Map<String, Object> new_values = new HashMap<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference().child(Constants.user).child(uuid).child(Constants.user_path);
        new_values.put(value_name, value);
        userRef.updateChildren(new_values);
    }

    // override
    public static void update_user_value(String uuid, String value_name, long value) {
        Map<String, Object> new_values = new HashMap<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference().child(Constants.user).child(uuid).child(Constants.user_path);
        new_values.put(value_name, value);
        userRef.updateChildren(new_values);
    }

    // override
    public static void update_party_value(String uuid, String key, String value) {
        Map<String, Object> new_values = new HashMap<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference().child(Constants.user).child(uuid).child(Constants.party_path);
        new_values.put(key, value);
        userRef.updateChildren(new_values);
    }

    public static void like_marker(Context context, String marker_id, String user_name){
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

    public static void dislike_marker(Context context, String marker_id, String user_name){
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

    public static boolean add_marker_to_firebase(Context context, int geo_restricted, String name, String spotify_uri, double latitude, double longitude){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference().child(Constants.location_prefix).push();


        GeoFire geoFire = new GeoFire(userRef);
        GeoLocation geoLocation = new GeoLocation(latitude, longitude);
        geoFire.setLocation("", geoLocation);


        Map<String, Object> new_values = new HashMap<>();
        new_values.put("geo_restricted", geo_restricted);
        new_values.put("name", name);
        new_values.put("spotify_uri", spotify_uri);
        new_values.put("user_name", User.getSpotify_user_name());
        userRef.updateChildren(new_values);
        Toast.makeText(context, "Marker uploaded to DB", Toast.LENGTH_SHORT).show();
        return true;
    }



    public static void delete_party_value(String uuid, String value){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference().child(Constants.user).child(uuid);
        userRef.child(Constants.party + "/" + Constants.party_member + "/" + value).removeValue();
    }

    public static String add_user_song_list_prefix(String playlist_without_prefix) {
        StringBuilder playlist_with_prefix = new StringBuilder();
        String[] spotify_title_uri_array = playlist_without_prefix.split(Constants.BACKSTERS_URI_SEPARATOR);

        for (int i = 0; i < spotify_title_uri_array.length; i++) {
            if (spotify_title_uri_array[i].contains(Constants.SHORTENED_SPOTIFY_EPISODE_URI_PREFIX) == false) {
                if (i < spotify_title_uri_array.length - 1) {
                    playlist_with_prefix.append(Constants.SPOTIFY_TRACK_URI_PREFIX)
                            .append(spotify_title_uri_array[i])
                            .append(Constants.BACKSTERS_URI_SEPARATOR);
                } else {  // dont add separator on last iteration
                    playlist_with_prefix.append(Constants.SPOTIFY_TRACK_URI_PREFIX)
                            .append(spotify_title_uri_array[i]);
                }
            } else if (spotify_title_uri_array[i].contains(Constants.SHORTENED_SPOTIFY_EPISODE_URI_PREFIX) == true) {
                if (i < spotify_title_uri_array.length - 1) {
                    playlist_with_prefix.append(spotify_title_uri_array[i]
                            .replace(Constants.SHORTENED_SPOTIFY_EPISODE_URI_PREFIX, Constants.SPOTIFY_EPISODE_URI_PREFIX))
                            .append(Constants.BACKSTERS_URI_SEPARATOR);
                } else {  // dont add separator on last iteration
                    playlist_with_prefix.append(spotify_title_uri_array[i]
                            .replace(Constants.SHORTENED_SPOTIFY_EPISODE_URI_PREFIX, Constants.SPOTIFY_EPISODE_URI_PREFIX));
                }
            }
        }
        return playlist_with_prefix.toString();
    }
}
