package com.spotfoxx.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotfoxx.Adapter.MarkerListAdapter;
import com.spotfoxx.Adapter.PlaylistAdapter;
import com.spotfoxx.BuildConfig;
import com.spotfoxx.Classes.Constants;
import com.spotfoxx.Classes.FirebaseControl;
import com.spotfoxx.Classes.MyLocation;
import com.spotfoxx.Classes.Playlist_item;
import com.spotfoxx.Classes.SpotifyControl;
import com.spotfoxx.Classes.SpotifyWebApi;
import com.spotfoxx.Classes.User;
import com.spotfoxx.Interfaces.SpotifyClient;
import com.spotfoxx.R;
import com.spotfoxx.SpotifyClasses.GetPlaylistObj;
import com.spotfoxx.SpotifyClasses.Item;
import com.spotfoxx.SpotifyClasses.Offset;
import com.spotfoxx.SpotifyClasses.PlayRequest;
import com.spotfoxx.SpotifyClasses.PlaylistInfo;
import com.spotfoxx.SpotifyClasses.PlaylistItem;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // RecyclerView Edit Playlists
    private ArrayList<MyLocation> user_added_locations = new ArrayList<>();
    private MarkerListAdapter mAdapterEditPlaylists;

    // Buttons
    private Button btn_place_marker;
    private Button btn_edit_marker;
    private Button btn_update_map;

    // Dialog click on marker
    private TextView tv_playlist_title;
    private TextView tv_like_dislike;
    private Button btn_like;
    private Button btn_dislike;

    // Dialog place marker
    private Dialog dialog;
    private Button btn_dialog_place_marker;
    private Spinner drop_down_playlists;
    private ImageView img_playlists;
    private String selected_playlist_id = "null";
    private String selected_playlist_name;
    private int clicked_marker_id;

    // RecyclerView Setup
    private List<Playlist_item> playlist_items = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private PlaylistAdapter mAdapter;
    private long mLastClickTime = 0;

    // Locaitons and markers
    private BitmapDescriptor marker_icon;
    private Location current_device_location;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    // Firestore
    private FirebaseFirestore mDb;
    private static final double GEOFIRE_SEARCH_RADIUS = 2.5;
    private static final int MARKER_RADIUS = 50;
    private static final Map<Integer, Integer> MARKER_LIKE_INDICATION = new HashMap<>();
    private GeoLocation geoLocation;

    // Google maps
    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private Circle searchRadiusCircle;

    // Spotify web api
    private static SpotifyClient client;

    // ARRAYS: These arrays hold all marker related information and are essential to core functionalities
    private ArrayList<MyLocation> myLocations = new ArrayList<>(); // holds all location objs, inside of the search radius
    private ArrayList<String> marker_ids = new ArrayList<>(); // holds one id for each visible marker. This id is needed to map one marker to one myLocation obj
    private ArrayList<Marker> markers = new ArrayList<>(); // holds all placed markers

    private Toolbar toolbar;

    private FloatingActionButton fab_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // Set context
        User.setContext(getApplicationContext());

        // Init Firestore db
        mDb = FirebaseFirestore.getInstance();

        // Init List of already placed locations / playlists by User
        initListOfUserPlacedLocations();
        MARKER_LIKE_INDICATION.put(0, 0x000000FF);
        MARKER_LIKE_INDICATION.put(1, 0x110000FF);
        MARKER_LIKE_INDICATION.put(2, 0x220000FF);
        MARKER_LIKE_INDICATION.put(3, 0x330000FF);
        MARKER_LIKE_INDICATION.put(4, 0x440000FF);
        MARKER_LIKE_INDICATION.put(5, 0x550000FF);
        MARKER_LIKE_INDICATION.put(6, 0x660000FF);
        MARKER_LIKE_INDICATION.put(7, 0x770000FF);

        MARKER_LIKE_INDICATION.put(8, 0x880000FF);

        MARKER_LIKE_INDICATION.put(9, 0x990000FF);
        MARKER_LIKE_INDICATION.put(10, 0xAA0000FF);
        MARKER_LIKE_INDICATION.put(11, 0xBB0000FF);
        MARKER_LIKE_INDICATION.put(12, 0xCC0000FF);
        MARKER_LIKE_INDICATION.put(13, 0xDD0000FF);
        MARKER_LIKE_INDICATION.put(14, 0xEE0000FF);
        MARKER_LIKE_INDICATION.put(15, 0xFF0000FF);
        mMapView = (MapView) findViewById(R.id.map);


        dialog = new Dialog(MainActivity.this);

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);

        }

        // Set Spotify client object of SpotifyClient interface
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Constants.SPOTIFY_API_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        client = retrofit.create(SpotifyClient.class);

        gpsStatusCheck();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.link_sharing) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String shareMessage = getString(R.string.link_sharing_message) + Constants.GOOGLE_PLAY_URL + BuildConfig.APPLICATION_ID;
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        } else if (id==R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();
            finishAndRemoveTask();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        fab_btn = findViewById(R.id.fab);
        btn_place_marker = findViewById(R.id.btn_place_marker);
        btn_edit_marker = findViewById(R.id.btn_edit_markers);
        btn_update_map = findViewById(R.id.btn_update_map);

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_place_marker.getVisibility() == View.VISIBLE){
                    makeBtnsInvisible();
                }else{
                    btn_edit_marker.setVisibility(View.VISIBLE);
                    btn_place_marker.setVisibility(View.VISIBLE);
                    btn_update_map.setVisibility(View.VISIBLE);
                }
            }

        });
        btn_place_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (User.isNetworkAvailable(getApplicationContext())) {
                    if(gpsStatusCheck()){
                        // Check if user is close to a present marker (Min marker distance has to be fulfilled).
                        double min_distance = get_closest_marker_distance();
                        if (min_distance > (MARKER_RADIUS * 2)) {
                            getListOfPlaylist();
                        } else {
                            Toast.makeText(getApplicationContext(), "You are too close to a marker", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_toast, Toast.LENGTH_LONG).show();
                }
                makeBtnsInvisible();
            }
        });

        btn_edit_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (User.isNetworkAvailable(getApplicationContext())) {
                    showEditPlaylistDialog();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_toast, Toast.LENGTH_LONG).show();
                }
                makeBtnsInvisible();
            }
        });

        btn_update_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (User.isNetworkAvailable(getApplicationContext())) {
                    // ################## Prevent rapid button clicking ######################
                    // double-clicking prevention of radio button, using threshold of x ms
                    if (SystemClock.elapsedRealtime() - mLastClickTime < Constants.MAP_UPDATE_DELAY) {
                        Toast.makeText(getApplicationContext(), "Please wait ...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    setDeviceLocation();
                    Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_toast, Toast.LENGTH_LONG).show();
                }

                makeBtnsInvisible();
            }
        });
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        // Get myLocation object, depending on clicked marker tag
        String marker_key = (String) marker.getTag();
        clicked_marker_id = marker_ids.indexOf(marker_key);
        MyLocation myLocation = myLocations.get(clicked_marker_id);

        // Get Playlist uri and perform web api call
        String playlist_uri = myLocation.getSpotify_uri();

        // Check if user is close to a present marker (Min marker distance has to be fulfilled).
        Location tmp_location = new Location("");
        tmp_location.setLatitude(myLocation.getL().get(0));
        tmp_location.setLongitude(myLocation.getL().get(1));
        float distance = current_device_location.distanceTo(tmp_location);

        if (distance < MARKER_RADIUS) {
            requestPlaylistInfo(getApplicationContext(), playlist_uri);
            showMarkerDialog(marker);
        } else {
            Toast.makeText(getApplicationContext(), "You need to be close to the marker.", Toast.LENGTH_SHORT).show();
        }
        makeBtnsInvisible();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_LOCATION_PERMISSION
                );
            }
        } else {
            enableUserLocation();
            // After map has been loaded, load all myLocations from db
            setDeviceLocation();
        }

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                dialog.dismiss();
                makeBtnsInvisible();
            }
        });

    }

    private void enableUserLocation() {
        mGoogleMap.setMyLocationEnabled(true);
        // Setup location callback method
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    current_device_location = new Location(location);
                }
            }
        };
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000)
                    .setFastestInterval(8000);

            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
        }
    }


    private void init_all_locations_firebase() {
        // Executed only once, inits all marker in a predefined distance to the user
        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
        // Clear the myLocations array
        myLocations.clear();
        // Clear the markers array
        markers.clear();
        // Clear the marker ids
        marker_ids.clear();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationRef = database.getReference().child(Constants.location_prefix);

        GeoFire geoFire = new GeoFire(locationRef);
        if(current_device_location != null){
            geoLocation = new GeoLocation(current_device_location.getLatitude(), current_device_location.getLongitude());
        } else {
            geoLocation = new GeoLocation(0,0);
        }
        GeoQuery geoQuery = geoFire.queryAtLocation(geoLocation, GEOFIRE_SEARCH_RADIUS);

        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                MyLocation myLocation = dataSnapshot.getValue(MyLocation.class);

                myLocation.setLike_rate(getLikeRate(myLocation));

                addMapMarkers(myLocation, dataSnapshot.getKey());
                marker_ids.add(dataSnapshot.getKey());
                myLocations.add(myLocation);

                drawCircle(new LatLng(location.latitude, location.longitude), MARKER_RADIUS, Color.RED, MARKER_LIKE_INDICATION.get(8));
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
                Log.d("Aaa", "aa");
            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                Log.d("Aaa", "aa");
            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                Log.d("Aaa", "aa");
                String key = dataSnapshot.getKey();
                int list_idx = marker_ids.indexOf(key);
                MyLocation myLocation = dataSnapshot.getValue(MyLocation.class);

                myLocation.setLike_rate(getLikeRate(myLocation));

                // update like, dislike textview in dialog
                int like = 0;
                int dislike = 0;
                if (myLocation.getDislike() != null){
                    dislike = myLocation.getDislike().size();
                }
                if (myLocation.getLike() != null){
                    like = myLocation.getLike().size();
                }
                updateLikeDislikeDialog(like, dislike);

                myLocations.set(list_idx, myLocation);
            }

            @Override
            public void onGeoQueryReady() {
                Log.d("Aaa", "aa");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d("Aaa", "aa");
            }
        });

        drawSearchCircle(new LatLng(current_device_location.getLatitude(), current_device_location.getLongitude()), GEOFIRE_SEARCH_RADIUS * 1000);
    }


    private void addMapMarkers(MyLocation myLocation, String key) {
        if (mGoogleMap != null) {

            marker_icon = generateBitmapDescriptorFromRes(getApplicationContext(), R.drawable.ic_marker);

            LatLng latLng = new LatLng(myLocation.getL().get(0), myLocation.getL().get(1));

            Marker placed_marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(myLocation.getName()).snippet(getLikeDislikeRate(myLocation)).icon(marker_icon));
            placed_marker.setTag(key);
            markers.add(placed_marker);

            // Set a listener for marker click.
            mGoogleMap.setOnMarkerClickListener(this);
        }
    }

    private void setDeviceLocation() {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        }

        Task location = fusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    current_device_location = (Location) task.getResult();

                    init_all_locations_firebase();

                } else {
                    Toast.makeText(getApplicationContext(), "unable to get current Location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getListOfPlaylist() {
        // Performs a web api call, that returns the users playlists.

        String mAccessToken = SpotifyControl.getSpotifyAccessToken();

        Call call = client.getListOfPlaylists(Constants.MAX_NR_OF_PLAYLISTS_TO_SEARCH, "Bearer " + mAccessToken);
        call.enqueue(new Callback<GetPlaylistObj>() {
            @Override
            public void onResponse(Call<GetPlaylistObj> call, Response<GetPlaylistObj> response) {
                GetPlaylistObj playlistObj = response.body();
                ArrayList<String> playlist_names = new ArrayList<>();
                if(playlistObj != null){
                    List<Item> playlists = playlistObj.getItems();
                    if (playlistObj != null) {
                        for (Item item : playlists) {
                            playlist_names.add(item.getName());
                        }
                    }
                    showPlaceMarkerDialog(playlists, playlist_names);
                }
            }

            @Override
            public void onFailure(Call call, Throwable throwable) {
                Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditPlaylistDialog(){
        dialog.setContentView(R.layout.marker_edit_layout);
        mRecyclerView = (RecyclerView) dialog.findViewById(R.id.rv_user_placed_marker);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationRef = database.getReference().child(Constants.location_prefix);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());


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
                    mAdapterEditPlaylists = new MarkerListAdapter(user_added_locations);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapterEditPlaylists);

                    mAdapterEditPlaylists.setOnItemClickListener(new MarkerListAdapter.OnItemClickListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.show();
    }

    private void drawSearchCircle(LatLng latLng, double radius) {
        if (mGoogleMap != null) {
            searchRadiusCircle = mGoogleMap.addCircle(new CircleOptions().center(latLng).radius(radius).strokeColor(Color.WHITE).fillColor(0x220000FF).strokeWidth(5.0f));
        }
    }

    private void drawCircle(LatLng latLng, double radius, int color, int fillColor) {
        if (mGoogleMap != null) {
            mGoogleMap.addCircle(new CircleOptions().center(latLng).radius(radius).strokeColor(color).fillColor(fillColor).strokeWidth(5.0f));
        }
    }

    private void showPlaceMarkerDialog(List<Item> playlist_objs, ArrayList<String> playlist_names) {
        dialog.setContentView(R.layout.place_marker_dialog);
        btn_dialog_place_marker = (Button) dialog.findViewById(R.id.btn_dialog_place_marker);
        drop_down_playlists = (Spinner) dialog.findViewById(R.id.drop_down_playlists);
        img_playlists = (ImageView) dialog.findViewById(R.id.playlist_img);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, playlist_names);

        //set the spinners adapter to the previously created one.
        drop_down_playlists.setAdapter(adapter);
        drop_down_playlists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Set album image if it is not null, else place placeholder image
                if (playlist_objs.get(i).getImages().size() > 0) {
                    selected_playlist_id = playlist_objs.get(i).getId();
                    selected_playlist_name = playlist_objs.get(i).getName();
                    SpotifyAppRemote spotifyAppRemote = SpotifyControl.getmSpotifyAppRemote(getApplicationContext());

                    com.spotfoxx.SpotifyClasses.Image image = playlist_objs.get(i).getImages().get(0);

                    ImageUri imageUri = new ImageUri(image.getUrl());
                    spotifyAppRemote
                            .getImagesApi()
                            .getImage(imageUri, Image.Dimension.THUMBNAIL)
                            .setResultCallback(
                                    bitmap -> {
                                        img_playlists.setImageBitmap(bitmap);

                                    });
                } else {
                    img_playlists.setImageResource(R.drawable.ic_launcher_background);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btn_dialog_place_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playlist_objs != null) {

                    // Check if playlist has already been placed
                    if(!User.getAlreadyPlacedSpotifyPlaylistUris().contains(selected_playlist_id)){
                        Boolean marker_upload_successfull = FirebaseControl.add_marker_to_firebase(
                                getApplicationContext(),
                                selected_playlist_name,
                                selected_playlist_id,
                                current_device_location.getLatitude(),
                                current_device_location.getLongitude());

                        // Add uri to list of uris of the current app session (On app start it is automatically updated)
                        User.addUriToAlreadyPlacedSpotifyPlaylistUris(selected_playlist_id);
                    }else{
                        Toast.makeText(getApplicationContext(), "Selected Playlist has already been placed.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No Playlist Selected", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showMarkerDialog(Marker marker) {
        dialog.setContentView(R.layout.marker_click_layout);

        btn_like = (Button) dialog.findViewById(R.id.btn_like);
        btn_dislike = (Button) dialog.findViewById(R.id.btn_dislike);
        mRecyclerView = (RecyclerView) dialog.findViewById(R.id.rv_playlist);
        tv_playlist_title = (TextView) dialog.findViewById(R.id.playlist_title);

        String[] likes_dislikes = marker.getSnippet().split("/");
        String like = likes_dislikes[0];
        String dislike = likes_dislikes[1];

        updateLikeDislikeDialog(Integer.parseInt(like), Integer.parseInt(dislike));

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseControl.like_marker(getApplicationContext(), marker_ids.get(clicked_marker_id), User.getSpotify_user_name());

            }
        });

        btn_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseControl.dislike_marker(getApplicationContext(), marker_ids.get(clicked_marker_id), User.getSpotify_user_name());
            }
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestPlaylistInfo(Context context, String spotify_playlist_uri) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // Get all tracks from Backster:user_name playlist and visualize them
        // If Playlist is empty, show corresponding info text to user.
        // This method gets all spotify device ids from user
        String mAccessToken = SpotifyControl.getSpotifyAccessToken();
        Call call = client.getPlaylistInfo(spotify_playlist_uri, "Bearer " + mAccessToken);
        call.enqueue(new Callback<PlaylistInfo>() {
            @Override
            public void onResponse(Call<PlaylistInfo> call, Response<PlaylistInfo> response) {
                if (response.code() == 200) {
                    PlaylistInfo playlistInfo = response.body();
                    tv_playlist_title.setText(playlistInfo.getOwner().getDisplayName() + " - " + playlistInfo.getName());
                    List<String> radio_playlist_array = new ArrayList<>();
                    // Create a list of Track objects that could either be a track or an episode.
                    playlist_items.clear();
                    for (PlaylistItem item : playlistInfo.getTracks().getItems()) {
                        com.spotfoxx.Classes.Playlist_item playlist_item = new com.spotfoxx.Classes.Playlist_item(item.getTrack());
                        playlist_items.add(playlist_item);
                        radio_playlist_array.add(playlist_item.getTrack().getUri());
                    }

                    // Check if Backster:user_name playlist is empty
                    if (!playlist_items.isEmpty()) {
                        // expand Recycler List view
                        mRecyclerView.setHasFixedSize(true);
                        mAdapter = new PlaylistAdapter(playlist_items);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                        mAdapter.setOnItemClickListener(new PlaylistAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                // ################## Prevent rapid button clicking ######################
                                // double-clicking prevention of radio button, using threshold of x ms
                                if (SystemClock.elapsedRealtime() - mLastClickTime < Constants.GENERAL_BTN_PRESS_DELAY) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();


                                // ################## Check if current played_recently_playlist_cleaned already exists in spotify ######################
                                // check network connection
                                if (User.isNetworkAvailable(getApplicationContext())) {
                                    // check spotify is connected
                                    if (SpotifyControl.getmSpotifyAppRemote(getApplicationContext()) != null) {

                                        // Play specific song from playlist
                                        // Create playRequest object
                                        Offset offset = new Offset();
                                        offset.setPosition(position);
                                        PlayRequest request = new PlayRequest();
                                        request.setContextUri(Constants.SPOTIFY_PLAYLIST_URI_PREFIX + spotify_playlist_uri);
                                        request.setOffset(offset);
                                        request.setPositionMs(0);
                                        SpotifyWebApi.playSongFromPlaylist(context, request);

                                    } else {    // todo: check why spotifyappremote null triggerd here
                                        Toast.makeText(getApplicationContext()
                                                , R.string.no_spotify_connection
                                                , Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext()
                                            , R.string.no_internet_toast
                                            , Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                } else if (response.code() == 404) {
                    Toast.makeText(getApplicationContext(), "Error: 404, wrong request body", Toast.LENGTH_LONG).show();  // todo: handle properly, not like this
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Spotify currently unavailable", Toast.LENGTH_LONG).show();  // todo: handle properly, not like this

            }
        });
    }

    // ############################# UTILS #################################
    public boolean gpsStatusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        else{
            return true;
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, please enable it.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Log.d("Tag", "Tag");
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void makeBtnsInvisible() {
        btn_edit_marker.setVisibility(View.INVISIBLE);
        btn_place_marker.setVisibility(View.INVISIBLE);
        btn_update_map.setVisibility(View.INVISIBLE);
    }

    private void updateLikeDislikeDialog(int like, int dislike){

        tv_like_dislike = (TextView) dialog.findViewById(R.id.tv_like_dislike);
        if(tv_like_dislike != null) {
            tv_like_dislike.setText(String.format("Likes: %d | Dislikes: %d", like, dislike));
        }
    }

    private String getLikeDislikeRate(MyLocation myLocation) {
        int like = 0;
        if (myLocation.getLike() != null) {
            like = myLocation.getLike().size();
        }
        int dislike = 0;
        if (myLocation.getDislike() != null) {
            dislike = myLocation.getDislike().size();
        }
        return like + "/" + dislike;
    }

    private int getLikeRate(MyLocation myLocation) {
        // Set like rating
        int likes = 0;
        int dislikes = 0;
        if (myLocation.getLike() != null) {
            likes = myLocation.getLike().size();
        }
        if (myLocation.getDislike() != null) {
            dislikes = myLocation.getDislike().size();
        }
        return likes - dislikes;
    }

    private double get_closest_marker_distance() {
        // distance is returned in meters
        Float min_distance = 1000000.0F;
        for (MyLocation location : myLocations) {
            Location tmp_location = new Location("");
            tmp_location.setLatitude(location.getL().get(0));
            tmp_location.setLongitude(location.getL().get(1));

            float distance = current_device_location.distanceTo(tmp_location);
            if (distance < min_distance) {
                min_distance = distance;
            }
        }
        return (double) min_distance;
    }

    public static BitmapDescriptor generateBitmapDescriptorFromRes(
            Context context, int resId) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        drawable.setBounds(
                0,
                0,
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void initListOfUserPlacedLocations(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationRef = database.getReference().child(Constants.location_prefix);
        ArrayList<String> alreadyPlacedSpotifyPlaylistUris = new ArrayList<>();
        Query query = locationRef.orderByChild("user_name").equalTo(User.getSpotify_user_name());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // create an array list of user added locations
                user_added_locations = new ArrayList<>();
                for (DataSnapshot user_location : snapshot.getChildren()){
                    MyLocation location = user_location.getValue(MyLocation.class);

                    alreadyPlacedSpotifyPlaylistUris.add(location.getSpotify_uri());
                }
                User.setAlreadyPlacedSpotifyPlaylistUris(alreadyPlacedSpotifyPlaylistUris);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    // ############################# UTILS #################################
}
