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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectionActivity extends AppCompatActivity {
    public static Location currLocation;

    //exhibits to be visit
    public List<ExhibitListItem> toBeVisiting;
    public List<String> visited;
    private boolean isResume;
    private int index;
    private int currentIndex;
    private Graph<String, IdentifiedWeightedEdge> graphInfoMap;
    private Map<String, ZooData.EdgeInfo> edgeInfoMap;
    private String cur;
    private String nxt;
    private String gate;

    /*
    new vars we are going to use after refactoring
    */

    //current nearest exhibit's ID
    private String currentLocationID;
    //current focus exhibit in plan
    private String focus;
    //index for finding current focus exhibit
    private int focusIndex;
    //contain exhibit of new plan after replan
    List<String> rePlan = new ArrayList<>();

    private ExhibitListItemDao dao;
    private ExhibitTodoDatabase db;
    private TextView directionText;
    private TextView distanceText;
    private String nextExhibitDistance;
    private String directionToNextExhibit;


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
            //retain progress
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

            directionText = findViewById(R.id.direction_inf);
            directionText.setText(firstDirection);

            distanceText = findViewById(R.id.distance_inf);
            nextExhibitDistance = FindDirection.printDistance(currentLocationID, focus);
            distanceText.setText(nextExhibitDistance);
            //this.cur = this.nxt;
        }

    }

    public void NextClicked(View view) {
       /* if(sortedID != null && currentIndex < index){
            //save();  //save cur and current for retain progress

            this.nxt = sortedID.get(currentIndex);

            String directionToNextExhibit = FindDirection.printPath(cur ,nxt);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(directionToNextExhibit);

            TextView distanceText = findViewById(R.id.distance_inf);

            String nextExhibitDistance = FindDirection.printDistance(cur,nxt);

            distanceText.setText(nextExhibitDistance);
            this.cur = this.nxt;
            currentIndex = currentIndex + 1;
        }
        else if(sortedID != null && currentIndex == index){
            save();

            this.nxt = gate;
            String directionToNextExhibit = FindDirection.printPath(cur ,nxt);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(directionToNextExhibit);

            TextView distanceText = findViewById(R.id.distance_inf);

            String nextExhibitDistance = FindDirection.printDistance(cur,nxt);

            distanceText.setText(nextExhibitDistance);
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

            //update instructions to the next exhibit (distance, directions) on UI
            updateDirectionInfo();
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

            String directionToNextExhibit = FindDirection.printPath(cur,nxt);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(directionToNextExhibit);

            TextView distanceText = findViewById(R.id.distance_inf);

            String nextExhibitDistance = FindDirection.printDistance(cur,nxt);

            distanceText.setText(nextExhibitDistance);
            this.cur = this.nxt;
            currentIndex = currentIndex + 1;

        } */
        if(focusIndex > 0) {
            focusIndex--;
            focus = sortedID.get(focusIndex);

            //update instructions to the next exhibit (distance, directions) on UI
            updateDirectionInfo();
        }
    }

    //Adapted from DylanLukes' example code
    public void mockClicked(View view) {
        var inputType = EditorInfo.TYPE_CLASS_NUMBER
                | EditorInfo.TYPE_NUMBER_FLAG_SIGNED
                | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;

        final EditText latInput = new EditText(this);
        latInput.setInputType(inputType);
        latInput.setHint("Latitude");
        //32.74812588554637 Gorillas
        //32.746302644092815 Crocodiles
        //32.72211788245888 Koi fish
        //32.73796433208615 Treetops Way / Orangutan Trail
        latInput.setText("32.73796433208615");

        final EditText lngInput = new EditText(this);
        lngInput.setInputType(inputType);
        lngInput.setHint("Longitude");
        //-117.17565073656901 Gorillas
        //-117.16659525430192 Crocodiles
        //-117.15794384136309 Koi fish
        //-117.15781396193616 Treetops Way / Orangutan Trail
        lngInput.setText("-117.15781396193616");

        final LinearLayout layout = new LinearLayout(this);
        layout.setDividerPadding(8);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(latInput);
        layout.addView(lngInput);

        mockLocation(latInput, lngInput, layout);
    }

    private void mockLocation(EditText latInput, EditText lngInput, LinearLayout layout) {
        //Alert Builder - the pop up window
        var mockWindowBuilder = new AlertDialog.Builder(this)
                .setTitle("Inject a Mock Location")
                .setView(layout)
                .setPositiveButton("Submit", (dialog, which) -> {
                    var lat = Double.parseDouble(latInput.getText().toString());
                    var lng = Double.parseDouble(lngInput.getText().toString());
                    updateCurrentLocation(lat, lng);

                    //update currentLocationID (current exhibit ID) according to currLocation (lat, lng)
                    currentLocationID = findNearestExhibitID(currLocation);

                    //declare and initialize currentLayout for prompt messages
                    final LinearLayout currentLayout = new LinearLayout(this);
                    currentLayout.setDividerPadding(8);
                    currentLayout.setOrientation(LinearLayout.VERTICAL);

                    //detect if current user location is on focus location
                    //trigger: plan unchanged; prompt user you are already on the site
                    if (focus.equals(currentLocationID)) {
                        onsitePrompt(currentLayout);
                    }

                    //detect current User location is on a later planned exhibit
                    //trigger: prompt "it seems like you are on a planned exhibit already, replan?"
                    //replan: base on the current visiting exhibit (C), how to get to others on our original plan (A, B, D)
                    //no: move focus to nextExhibitDistance visiting exhibit (D)
                    //get index of current location, set focus index to current location index +1
                    //set focus to sortedID.get(focusIndex)
                    //currentLocationID  sortedID
                    //|focus|, [Current]
                    //|A| -> B -> [C] -> D
                    //get focusIndex, get subarray of sortedID which all elements have index greater than
                    //focusIndex, and exclude current location

                    //psudo code
                    //get focusIndex
                    //add all elements with index greater than focus index to a list (maybe called tobeVisiting)
                    //exclude currentLocation from the list
                    /*
                    for(int i = focusIndex; i < sortedID.size(); i++){
                        //sortedID[i] is id
                        //convert this id to its corresponding exhibitlistitem
                        //add this exhibititem to toBeVisiting
                        //to do...

                        //for loop TODO refactor
                        for (ExhibitListItem e : exhibitListItems) {
                            if ( nameToParentIDMap.get( e.getExhibitName() ).equals(sortedID.get(i)) ) {
                                toBeVisiting.add(e);
                            }
                        }
                    }

                    //exclude current location from toBeVisiting
                    //to do...
                    /*for(ExhibitListItem e : toBeVisiting){
                        if(nameToParentIDMap.get(e.getExhibitName()) .equals(currentLocationID))
                            toBeVisiting.remove(e);
                    } */

                    /*
                    //pseudo
                    //call routePlanner and return new plan to list rePlan
                    //overwrite
                    RoutePlanner routePlanner = new RoutePlanner(toBeVisiting, true);
                    rePlan = routePlanner.getRoute();

                    //ABC + replan(DFE)

                    for(int i = 0; i < rePlan.size(); i++) {
                        sortedID.set(focusIndex, rePlan.get(i));
                    }

                    focus = sortedID.get(focusIndex);

                    */




                    /*

                    //find the user's current nearest exhibit
                    cur = findNearestExhibitID(currLocation);
                    currentIndex = sortedID.indexOf(cur);
                    Boolean curIsInToBeVisiting = false;
                    //if cur is not in visited and in tobeVISITING,add
                    for(ExhibitListItem e : toBeVisiting){
                        if(nameToParentIDMap.get(e.exhibitName) == currentLocationID) {
                            curIsInToBeVisiting = true;
                            break;
                        }
                    }
                    if(!visited.contains(currentLocationID) && curIsInToBeVisiting )visited.add(currentLocationID);

                    // todo: if condition change
                    if( findNearestExhibitID(currLocation) != ""
                    ) {
                    final LinearLayout currentLayout = new LinearLayout(this);
                    currentLayout.setDividerPadding(8);
                    currentLayout.setOrientation(LinearLayout.VERTICAL);
                        replan(currentLayout);
                    }


                     */

                    /*
                    Section for update displayed directional message
                     */
                    updateDirectionInfo();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                });
        mockWindowBuilder.show();
    }

    private void replan( LinearLayout linearLayout) {
        var builder2 = new AlertDialog.Builder(this)
                .setTitle("You are in the wrong track("
                        + findNearestExhibitID(currLocation)+"), you can do a replan")
                .setView(linearLayout)
                //Todo : replan sth
                .setPositiveButton("replan", (dialog2, which2) -> {

                    /*
                    String all = "{";
                    for (String e : distance) {
                        all += e + ", ";
                    }
                    Log.d("TEST", all + "}\n");

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

    private void onsitePrompt(LinearLayout linearLayout) {
        var onsitePromptBuilder = new AlertDialog.Builder(this)
                .setTitle("Onsite!")
                .setView(linearLayout)
                .setMessage("You are at the exhibit that you have selected to visit, " +
                        "click NEXT button to move on to the nextExhibitDistance exhibit in plan!")
                .setPositiveButton("Okay!", (dialog, which) -> {

                });
        onsitePromptBuilder.show();
    }

    public void updateCurrentLocation(double lat, double lng) {
        //set location's lat and lng by params
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

    private void updateDirectionInfo() {

        directionToNextExhibit = FindDirection.printPath(currentLocationID, focus);
        directionText.setText(directionToNextExhibit);
        nextExhibitDistance = FindDirection.printDistance(currentLocationID, focus);
        distanceText.setText(nextExhibitDistance);

    }
}
