package edu.ucsd.cse110.ZooSeeker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //todo delete it
    //private final PermissionChecker permissionChecker = new PermissionChecker(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // enter search list UI
        Intent intent = new Intent(this, SearchListActivity.class);
        startActivity(intent);

        //todo delete it
        //if (permissionChecker.ensurePermissions()) return;
    }
}