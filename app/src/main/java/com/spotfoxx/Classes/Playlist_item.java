package com.spotfoxx.Classes;

import com.spotfoxx.SpotifyClasses.Episode;
import com.spotfoxx.SpotifyClasses.Track;

public class Playlist_item {

    private Track track;
    private Episode episode;

    public Playlist_item(Track track){
        this.track = track;
    }


    public Playlist_item(Episode episode){
        this.episode = episode;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }
}
