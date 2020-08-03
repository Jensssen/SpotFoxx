package com.spotfoxx.SpotifyClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Actions {
    @SerializedName("disallows")
    @Expose
    private Disallows disallows;

    public Disallows getDisallows() {
        return disallows;
    }

    public void setDisallows(Disallows disallows) {
        this.disallows = disallows;
    }

}
