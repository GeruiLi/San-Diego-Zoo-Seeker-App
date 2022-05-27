package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.curLocation;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.testLong;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.testLati;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.fusedLocationClient;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToIDMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.sortedID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Map;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.graphInfoMap;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class DirectionActivity extends AppCompatActivity {
    private boolean isResume;
    private int index;
    private int current;
    private Graph<String, IdentifiedWeightedEdge> graphInfoMap;
    private Map<String, ZooData.EdgeInfo> edgeInfoMap;
    private List<ExhibitListItem> exhibitListItems;
    private String cur;
    private String nxt;
    private String gate;

    private ExhibitListItemDao dao;
    private ExhibitTodoDatabase db;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
            isResume = extras.getBoolean("isResume");
        else
            isResume = false;

        gate = ZooData.findGate(vertexInfoMap);

        if(sortedID != null){
            if(!isResume) PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();

            index = sortedID.size();
            load();
            this.nxt = sortedID.get(current);
            current++;

            String firstDirection = FindDirection.printPath(cur, nxt);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(firstDirection);

            TextView distanceText = findViewById(R.id.distance_inf);
            String next = FindDirection.printDistance(cur, nxt);
            distanceText.setText(next);
            this.cur = this.nxt;
        }

    }

    public void NextClicked(View view) {
        if(sortedID != null && current < index){
            save();

            this.nxt = sortedID.get(current);

            String direction = FindDirection.printPath(cur ,nxt);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(direction);

            TextView distanceText = findViewById(R.id.distance_inf);

            String next = FindDirection.printDistance(cur,nxt);

            distanceText.setText(next);
            this.cur = this.nxt;
            current = current + 1;
        }
        else if(sortedID != null && current == index){
            save();

            this.nxt = gate;
            String direction = FindDirection.printPath(cur ,nxt);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(direction);

            TextView distanceText = findViewById(R.id.distance_inf);

            String next = FindDirection.printDistance(cur,nxt);

            distanceText.setText(next);
            this.cur = this.nxt;
            current = current + 1;
        }
        else{
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
            finish();
        }
    }

    public void stepBackClicked(View view) {
        if(current > 1){
            current = current - 2;
            this.nxt = sortedID.get(current);
            if(current > 0){
                this.cur = sortedID.get(current - 1);
            }
            else this.cur = gate;

            save();

            String direction = FindDirection.printPath(cur,nxt);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(direction);

            TextView distanceText = findViewById(R.id.distance_inf);

            String next = FindDirection.printDistance(cur,nxt);

            distanceText.setText(next);
            this.cur = this.nxt;
            current = current + 1;

        }
    }

    public void load() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);//this.getPreferences(MODE_PRIVATE);

        //Future connect to real location
        //Current version is hardcode
        String realLocation = gate;

        current = preferences.getInt("current",0);
        cur = preferences.getString("cur", realLocation);
    }

    public void save() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("current", current);
        editor.putString("cur", cur);

        editor.apply();
    }
}
