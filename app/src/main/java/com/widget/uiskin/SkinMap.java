package com.widget.uiskin;
import java.util.HashMap;
import java.util.Map;

public class SkinMap {

    public final Map<Integer, Integer> skinMap = new HashMap<>();

    public SkinMap() {
        skinMap.put(R.drawable.ic_launcher,R.drawable.skin_ic_launcher);
        skinMap.put(R.drawable.background_round,R.drawable.skin_background_round);
    }

}
