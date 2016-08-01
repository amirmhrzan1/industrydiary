package com.weareforge.qms.Objects;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by prajit on 3/18/16.
 */
public class DownloadedFilesMap {

    public static ArrayList<HashMap<String, Object>> alMap = new ArrayList<>();

    public static void reset() {
        alMap.clear();
    }

}
