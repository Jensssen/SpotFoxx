package com.spotfoxx.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spotfoxx.Adapter.MarkerListAdapter;
import com.spotfoxx.Adapter.PlaylistAdapter;
import com.spotfoxx.Classes.Constants;
import com.spotfoxx.Classes.MyLocation;
import com.spotfoxx.Classes.User;
import com.spotfoxx.R;

import java.util.ArrayList;

public class PlacedMarkerSettingsActivity extends AppCompatActivity {
    // RecyclerView Setup
    private ArrayList<MyLocation> user_added_locations = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MarkerListAdapter mAdapter;

    private LinearLayoutManager mLayoutManager;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placed_marker_settings);

        pull_all_user_marker_keys();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_markers);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());

    }


    private void pull_all_user_marker_keys(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationRef = database.getReference().child(Constants.location_prefix);


        Query query = locationRef.orderByChild("user_name").equalTo(User.getSpotify_user_name());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // create an array list of user added locations
                user_added_locations = new ArrayList<>();
                for (DataSnapshot user_location : snapshot.getChildren()){
                    MyLocation location = user_location.getValue(MyLocation.class);
                    location.setKey(user_location.getKey());
                    user_added_locations.add(location);
                }

                if (!user_added_locations.isEmpty()) {
                    // expand Recycler List view
                    mRecyclerView.setHasFixedSize(true);
                    mAdapter = new MarkerListAdapter(user_added_locations);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new MarkerListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            // ################## Prevent rapid button clicking ######################
                            // double-clicking prevention of radio button, using threshold of x ms
                            if (SystemClock.elapsedRealtime() - mLastClickTime < Constants.GENERAL_BTN_PRESS_DELAY) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            MyLocation myLocation = user_added_locations.get(position);

                            // open Spotify playlist on Click
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("spotify:playlist:"+ myLocation.getSpotify_uri()));
                            intent.putExtra(Intent.EXTRA_REFERRER,
                                    Uri.parse("android-app://" + getApplicationContext().getPackageName()));
                            startActivity(intent);
                        }
                    });
                }

                Log.d("aa", "bb");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}


