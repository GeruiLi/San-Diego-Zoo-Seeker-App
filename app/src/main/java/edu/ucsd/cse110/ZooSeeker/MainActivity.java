package edu.ucsd.cse110.ZooSeeker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final PermissionChecker permissionChecker = new PermissionChecker(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, SearchListActivity.class);
        startActivity(intent);
        if (permissionChecker.ensurePermissions()) return;
    }
}