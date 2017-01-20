package com.example.stpl.cameraapp.main;

import java.util.ArrayList;

/**
 * Created by stpl on 1/19/2017.
 */

public interface MainView {
    void permissionAvailable();

    void permissionNotAvailable(ArrayList<String> permissionNeeded, ArrayList<String>
            permissionList);


}
