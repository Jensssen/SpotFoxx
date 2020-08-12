package com.spotfoxx.Classes;

import java.util.Map;

public class UserPlaylistMarker {

    private Map<String, Object> plm;

    public UserPlaylistMarker(Map<String, Object> plm) {
        this.plm = plm;
    }

    public UserPlaylistMarker() {
    }

    public Map<String, Object> getPlm() {
        return plm;
    }

    public void setPlm(Map<String, Object> plm) {
        this.plm = plm;
    }
}
