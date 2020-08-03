package com.spotfoxx.Classes;

public final class Constants {
    // Firebase Keys:
    public static final String location_prefix = "location";
    public static final String user = "u";
    public static final String party = "party";
    public static final String marker = "marker";
    public static final String party_member = "party_member";
    public static final String author = "a";
    public static final String broadcast_state = "b";
    public static final String playbackPosition = "pn";
    public static final String playing = "p";
    public static final String spotify_title_uri = "s";
    public static final String timePlayerStarted = "t";
    public static final String title = "te";
    public static final String lobby_playlist_uri = "pt";
    public static final String user_name = "ue";
    public static final String uuid = "ud";
    public static final String party_playlist_uri = "pi";
    public static final String party_track_position = "ptn";
    public static final String party_seek_to_ms = "x";
    public static final String party_start_sync_atom_time = "y";
    public static final String party_sync_status = "z";
    public static final String party_volume = "v";
    public static final String user_path = Constants.user;
    public static final String party_path = "/" + Constants.party + "/" + Constants.party_member;
    public static final String broadcast_state_path = user_path + "/" + Constants.broadcast_state;
    public static final String playbackPosition_path = user_path + "/" + Constants.playbackPosition;
    public static final String userSongList_path = user_path + "/" + Constants.lobby_playlist_uri;
    public static final String uuid_path = user_path + "/" + Constants.uuid;
    public static final String user_name_path = user_path + "/" + Constants.user_name;
    public static final String party_seek_to_ms_path = user_path + "/" + Constants.party_seek_to_ms;
    public static final String party_start_sync_atom_time_path = user_path + "/" + Constants.party_start_sync_atom_time;
    public static final String party_sync_status_path = user_path + "/" + Constants.party_sync_status;

    // Spotify Strings:
    public static final String BACKSTERS_URI_SEPARATOR = ":,:";
    public static final String SPOTIFY_TRACK_URI_PREFIX = "spotify:track:";
    public static final String SPOTIFY_EPISODE_URI_PREFIX = "spotify:episode:";
    public static final String SPOTIFY_PLAYLIST_URI_PREFIX = "spotify:playlist:";
    public static final String SHORTENED_SPOTIFY_EPISODE_URI_PREFIX = "e:";
    public static final String BACKSTER_PLAYLIST_URI_PREFIX = "spotify:lobby_playlist_uri:";

    // Spotify API:
    public static final int SPOTIFY_REQUEST_CODE = 1337;
    public static final int SPOTIFY_SDK_API_TIMEOUT_SEC = 1;
    public static final String SPOTIFY_CLIENT_ID = "bfb4f2936e0f465cb3dcedd2bb044287";
    public static final String SPOTIFY_REDIRECT_URI = "spotfoxx-login://callback";
    public static final String SPOTIFY_PACKAGE_NAME = "com.spotify.music";
    public static final String REFERRER_UTM = "adjust_campaign=com.backster&adjust_tracker=ndjczk&utm_source=adjust_preinstall";
    public static final String SPOTIFY_API_URL = "https://api.spotify.com/";

    // Firebase API:
    public static final int FIREBASE_REQUEST_CODE = 555;

    // Backster logic:
    public static final int GENERAL_BTN_PRESS_DELAY = 1500;
    public static final int MAP_UPDATE_DELAY = 15000;
    public static final int SYNC_BTN_PRESS_DELAY = 5000;
    public static final int SLEEP_TIME_PLAYLIST_START_MS = 1000;
    public static final int BOTTOM_SHEET_COLOR_TRANSITIONTIME_MS = 400;
    public static final long RELATIVE_SEEK_TO_MS_SAMLL = 50;
    public static final long RELATIVE_SEEK_TO_MS_BIG = 200;
    public static final String MAX_NR_OF_PLAYLISTS_TO_SEARCH = "50";
    public static final String BACKSTER_PLAYLIST_TITLE = "Backster: ";
    public static final int PARTY_SEEK_TO_MS_FALLBACK = 2;
    public static final long PARTY_START_SYNC_ATOM_TIME_SHIFT_MS= 10000;

    // Backster Activities:
    public static final String LOGIN_ACTIVITY_TAG = "LoginActivity";
    public static final String FRIENDS_FRAGMENT_TAG = "FriendsActivity";
    public static final String PUBLIC_FRAGMENT_TAG = "PublicActivity";
    public static final String RADIO_ACTIVITY_TAG = "RadioActivity";
    public static final String SPOTIFY_POLL_SERVICE_TAG = "SpotifyPollService";
    public static final String DJ_ACTIVITY_TAG = "DjActivity";

    // Backster Classes:
    public static final String SPOTIFY_WEB_API_TAG = "SpotifyWebApi";


    // Location Service
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    // Other:
    public static final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=";
    public static final String APP_NAME = "Backster";
    public static final String PREFERENCES_NAME = "User_Preferences";
    public static final String DJ_UUID_KEY = "dj_uuid";
    public static final String DJ_USER_NAME_KEY = "dj_user_name";


}
