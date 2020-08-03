package com.spotfoxx.SpotifyClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Songs {
    @SerializedName("tracks")
    @Expose
    private List<Track> tracks = null;

    public List<Track> getSongs() {
        return tracks;
    }

    public void setSongs(List<Track> tracks) {
        this.tracks = tracks;
    }
}