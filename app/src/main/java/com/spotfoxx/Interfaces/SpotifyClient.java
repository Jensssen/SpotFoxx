package com.spotfoxx.Interfaces;


import com.spotfoxx.SpotifyClasses.AlbumInfo;
import com.spotfoxx.SpotifyClasses.CurrentPlaybackInformation;
import com.spotfoxx.SpotifyClasses.Devices;
import com.spotfoxx.SpotifyClasses.Episodes;
import com.spotfoxx.SpotifyClasses.GetPlaylistObj;
import com.spotfoxx.SpotifyClasses.PlayRequest;
import com.spotfoxx.SpotifyClasses.PlaylistInfo;
import com.spotfoxx.SpotifyClasses.Songs;
import com.spotfoxx.SpotifyClasses.Track;
import com.spotfoxx.SpotifyClasses.UserObj;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpotifyClient {

    @GET("v1/me")
    Call<UserObj> getUserObj(@Header("Authorization") String authToken);

    @GET("v1/me/playlists")
    Call<GetPlaylistObj> getListOfPlaylists(@Query("limit") String limit_value, @Header("Authorization") String authToken);

    @GET("v1/tracks")
    Call<Songs> getSeveralTracks(@Query("ids") String ids, @Header("Authorization") String authToken);

    @GET("v1/episodes")
    Call<Episodes> getSeveralEpisodes(@Query("ids") String ids, @Header("Authorization") String authToken);

    @GET("v1/tracks/{id}")
    Call<Track> getOneTrack(@Path("id") String track_id, @Header("Authorization") String authToken);

    @PUT("v1/me/player/play")
    Call<Void> playSongFromPlaylist(@Header("Authorization") String authToken, @Body PlayRequest body);

    @PUT("v1/me/player/play")
    Call<Void> playSongFromPlaylist(@Query("device_id") String device_id, @Header("Authorization") String authToken, @Body PlayRequest body);

    @GET("v1/me/player/devices")
    Call<Devices> getDeviceID(@Header("Authorization") String authToken);

    @GET("v1/me/player")
    Call<CurrentPlaybackInformation> getInformationAboutUsersCurrentPlayback(@Query("additional_types") String additional_types, @Header("Authorization") String authToken);

    @GET("v1/playlists/{playlist_id}")
    Call<PlaylistInfo> getPlaylistInfo(@Path("playlist_id") String playlist_id, @Header("Authorization") String authToken);

    @GET("v1/albums/{album_id}")
    Call<AlbumInfo> getAlbumInfo(@Path("album_id") String album_id, @Header("Authorization") String authToken);

}
