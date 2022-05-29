package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.FindDirection.findNearestExhibitID;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.*;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.jgrapht.Graph;

import java.util.List;
import java.util.Map;

public class DirectionActivity extends AppCompatActivity {
    public static Location currLocation;

    public List<ExhibitListItem> toBeVisiting;   //exhibits to be visiting
    public List<String> visited;
    private boolean isResume;
    private int index;
    private int currentIndex;
    private Graph<String, IdentifiedWeightedEdge> graphInfoMap;
    private Map<String, ZooData.EdgeInfo> edgeInfoMap;
    private String cur;
    private String nxt;
    private String gate;

    //refactor
    private String currentLocationID;
    private String focus;
    private int focusIndex;

    private ExhibitListItemDao dao;
    private ExhibitTodoDatabase db;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        //find gate id
        gate = ZooData.findGate(vertexInfoMap);

        //initialize currLocation
        {
            currLocation = new Location("entrance_exit_gate");
            currLocation.setLatitude(32.73561);
            currLocation.setLongitude(-117.14936);
        }

        //initialize tobeVisiting
       /* for(ExhibitListItem e : exhibitListItems){
            toBeVisiting.add(e);
        } */

        //retain progress
        {
            Bundle extras = getIntent().getExtras();
            if (extras != null)
                isResume = extras.getBoolean("isResume");
            else
                isResume = false;
        }

        //when the sortedID is created
        if(sortedID != null){
            //retai progress
            //if(!isResume) PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();

            index = sortedID.size();
            //load(); //if not resume,set current = 0, set cur = gate_id

            currentLocationID = gate;   //set current location to gate
            focusIndex = 0;
            focus = sortedID.get(focusIndex);   //set focus to first exhibit in sortedID

            //this.nxt = sortedID.get(currentIndex);  //current is 0, get first element in sortedID
            //currentIndex++;

            //store path info from cur to nxt into firstDirection
            String firstDirection = FindDirection.printPath(currentLocationID, focus);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(firstDirection);

            TextView distanceText = findViewById(R.id.distance_inf);
            String next = FindDirection.printDistance(currentLocationID, focus);
            distanceText.setText(next);
            //this.cur = this.nxt;
        }

    }

    public void NextClicked(View view) {
       /* if(sortedID != null && currentIndex < index){
            //save();  //save cur and current for retain progress

            this.nxt = sortedID.get(currentIndex);

            String direction = FindDirection.printPath(cur ,nxt);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(direction);

            TextView distanceText = findViewById(R.id.distance_inf);

            String next = FindDirection.printDistance(cur,nxt);

            distanceText.setText(next);
            this.cur = this.nxt;
            currentIndex = currentIndex + 1;
        }
        else if(sortedID != null && currentIndex == index){
            save();

            this.nxt = gate;
            String direction = FindDirection.printPath(cur ,nxt);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(direction);

            TextView distanceText = findViewById(R.id.distance_inf);

            String next = FindDirection.printDistance(cur,nxt);

            distanceText.setText(next);
            this.cur = this.nxt;
            currentIndex = currentIndex + 1;
        }
        else{
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
            finish();
        }
        */
        //refactor onNextClicked-----------------------------
        if(focusIndex < sortedID.size() - 1) {
            focusIndex++;
            focus = sortedID.get(focusIndex);

            String direction = FindDirection.printPath(currentLocationID, focus);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(direction);

            TextView distanceText = findViewById(R.id.distance_inf);

            String next = FindDirection.printDistance(currentLocationID, focus);

            distanceText.setText(next);
        }
        else {
            finish();
        }
    }

    public void stepBackClicked(View view) {
     /*   if(currentIndex > 1){
            currentIndex = currentIndex - 2;
            this.nxt = sortedID.get(currentIndex);
            if(currentIndex > 0){
                this.cur = sortedID.get(currentIndex - 1);
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
            currentIndex = currentIndex + 1;

        } */
        if(focusIndex > 0) {
            focusIndex--;
            focus = sortedID.get(focusIndex);

            String direction = FindDirection.printPath(currentLocationID, focus);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(direction);

            TextView distanceText = findViewById(R.id.distance_inf);

            String next = FindDirection.printDistance(currentLocationID, focus);

            distanceText.setText(next);
        }
    }

    public void mockClicked(View view) {
        // TODO: could define this layout in an XML and inflate it, instead of defining in code...
        var inputType = EditorInfo.TYPE_CLASS_NUMBER
                | EditorInfo.TYPE_NUMBER_FLAG_SIGNED
                | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;

        final EditText latInput = new EditText(this);
        latInput.setInputType(inputType);
        latInput.setHint("Latitude");
        //32.74812588554637 Gorillas
        //32.746302644092815 Crocodiles
        latInput.setText("32.74812588554637");

        final EditText lngInput = new EditText(this);
        lngInput.setInputType(inputType);
        lngInput.setHint("Longitude");
        //-117.17565073656901 Gorillas
        //-117.16659525430192 Crocodiles
        lngInput.setText("-117.17565073656901");

        final LinearLayout layout = new LinearLayout(this);
        layout.setDividerPadding(8);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(latInput);
        layout.addView(lngInput);

        mockLocation(latInput, lngInput, layout);
    }

    private void mockLocation(EditText latInput, EditText lngInput, LinearLayout layout) {
        var builder = new AlertDialog.Builder(this)
                .setTitle("Inject a Mock Location")
                .setView(layout)
                .setPositiveButton("Submit", (dialog, which) -> {
                    var lat = Double.parseDouble(latInput.getText().toString());
                    var lng = Double.parseDouble(lngInput.getText().toString());
                    updateCurrentLocation(lat, lng);

                    cur = findNearestExhibitID(currLocation);
                    currentIndex = sortedID.indexOf(cur);
                    Boolean curIsInToBeVisiting = false;
                    //if cur is not in visited and in tobeVISITING,add
                    for(ExhibitListItem e : toBeVisiting){
                        if(nameToParentIDMap.get(e.exhibitName) == cur) {
                            curIsInToBeVisiting = true;
                            break;
                        }
                    }
                    if(!visited.contains(cur) && curIsInToBeVisiting )visited.add(cur);

                    // todo: if condition change
                    if( findNearestExhibitID(currLocation) != ""
                    ) {
                    final LinearLayout layout1 = new LinearLayout(this);
                    layout1.setDividerPadding(8);
                    layout1.setOrientation(LinearLayout.VERTICAL);
                        replan(layout1);
                    }

                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                });
        builder.show();
    }

    private void replan( LinearLayout layout2) {
        var builder2 = new AlertDialog.Builder(this)
                .setTitle("You are in the wrong track("
                        + findNearestExhibitID(currLocation)+"), you can do a replan")
                .setView(layout2)
                //Todo : replan sth
                .setPositiveButton("replan", (dialog2, which2) -> {

                    /*
                    String all = "{";
                    for (String e : distance) {
                        all += e + ", ";
                    }
                    Log.d("TEST", all + "}\n");

                     */
                    /*
                    all = "{";
                    for (String e : nameToParentIDMap.keySet()) {
                        all += "(" + e + ", " + nameToParentIDMap.get(e) + ") ; ";
                    }
                    Log.d("TEST", all + "}\n");

                    "lat": 32.74812588554637,
                    "lng": -117.17565073656901
                    */


                    //create a new routeplanner to contain the new route

                    for (ExhibitListItem exhibit : exhibitListItems){
                        if(visited != null && !visited.contains(nameToParentIDMap.get(exhibit.exhibitName)))
                            toBeVisiting.add(exhibit);
                    }
                    RoutePlanner newRoute = new RoutePlanner(toBeVisiting, true);
                    sortedID = newRoute.getRoute();
                    //   cur = findNearestExhibitID(currLocation);
                    //   current = sortedID.indexOf(cur);
                    //   nxt = sortedI
                    //   System.out.println(cur + " print here");

                    //show new directions UI UPDATE
                    TextView directionText = findViewById(R.id.direction_inf);
                    directionText.setText(FindDirection.printPath(cur ,nxt));
                    TextView distanceText = findViewById(R.id.distance_inf);
                    distanceText.setText(FindDirection.printDistance(cur, nxt));



                    /*
                    all = "{";
                    for (String e : distance) {
                        all += e + ", ";
                    }
                    Log.d("TEST", all + "}\n");

                     */
                })
                .setNegativeButton("Cancel", (dialog2, which2) -> {
                    dialog2.cancel();
                });
        builder2.show();
    }

    public void updateCurrentLocation(double lat, double lng) {
        currLocation.setLatitude(lat);
        currLocation.setLongitude(lng);
    }

    public void load() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);//this.getPreferences(MODE_PRIVATE);

        //Future connect to real location
        //Current version is hardcode
        String realLocation = gate;

        currentIndex = preferences.getInt("current",0);
        cur = preferences.getString("cur", realLocation);
    }

    public void save() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("current", currentIndex);
        editor.putString("cur", cur);

        editor.apply();
    }
}
