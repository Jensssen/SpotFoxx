package com.spotfoxx.SpotifyClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Disallows {
    @SerializedName("pausing")
    @Expose
    private Boolean pausing;

    public Boolean getPausing() {
        return pausing;
    }

    public void setPausing(Boolean pausing) {
        this.pausing = pausing;
    }
}
