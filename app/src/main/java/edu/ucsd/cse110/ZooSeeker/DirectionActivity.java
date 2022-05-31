package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.FindDirection.*;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.*;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DirectionActivity extends AppCompatActivity {
    public static Location currLocation;

    //exhibits to be visit
    public List<ExhibitListItem> toBeVisiting;
    public List<String> visited;
    private boolean isResume;
    private int index;
    private int currentIndex;
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
    //List of remaining plan to be visited
    List<String> remainingPlan;
    //List of exhibits in plan that have been visited
    List<String> visitedExhibits;

    private ExhibitListItemDao dao;
    private ExhibitTodoDatabase db;
    private TextView directionText;
    private TextView distanceText;
    private String nextExhibitDistance;
    private String directionToNextExhibit;

    private boolean detailed;


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

        detailed = true;

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
            String firstDirection = FindDirection.printPath(currentLocationID, focus, detailed);

            directionText = findViewById(R.id.direction_inf);
            directionText.setText(firstDirection);

            distanceText = findViewById(R.id.distance_inf);
            nextExhibitDistance = FindDirection.printDistance(currentLocationID, focus);
            distanceText.setText(nextExhibitDistance);
            //this.cur = this.nxt;

            //get a deep copy of sortedID - Non of the exhibits in plan has been visited
            remainingPlan = sortedID.stream()
                    .collect(Collectors.toList());

            //add exit gate to the sorted plan
            sortedID.add(ZooData.findGate(vertexInfoMap));

            //Non of the exhibits in plan has been visited
            visitedExhibits = new ArrayList<>();

            //make direction scrollable
            directionText.setMovementMethod(new ScrollingMovementMethod());
        }

    }

    public void NextClicked(View view) {

        //refactor onNextClicked-----------------------------
        if(focusIndex < sortedID.size() - 1) {
            focusIndex++;

            //whenever next is clicked, the remainingPlan get rid of this exhibit -> mark as visited
            //remainingPlan.remove(focus);
            //and visited gets updated with this info to keep track of the user's history
            //visitedExhibits.add(focus);

            //update instructions to the next exhibit (distance, directions) on UI
            updateDirectionInfo();
        }
        else {

            finish();

        }

    }

    public void stepBackClicked(View view) {

        if(focusIndex > 0) {
            focusIndex--;

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
        //32.74213959255212 Treetops Way / Hippo Trail
        latInput.setText("32.74213959255212");

        final EditText lngInput = new EditText(this);
        lngInput.setInputType(inputType);
        lngInput.setHint("Longitude");
        //-117.17565073656901 Gorillas
        //-117.16659525430192 Crocodiles
        //-117.15794384136309 Koi fish
        //-117.15781396193616 Treetops Way / Orangutan Trail
        //-117.16066409380507 Treetops Way / Hippo Trail
        lngInput.setText("-117.16066409380507");

        final LinearLayout layout = new LinearLayout(this);
        layout.setDividerPadding(8);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(latInput);
        layout.addView(lngInput);

        mockLocation(latInput, lngInput, layout);

    }

    private boolean onLaterSelectedExhibit(String focus, String currentLocation) {
        //conver currentLocation to nearestExhibitInPlan
        String currentVisitingExhibit = nearestExhibitInPlan(currentLocation);

        //filter out current visiting exhibit
        List<String> laterExhibits = remainingPlan
                .stream()
                .filter(exhibit -> !(exhibit.equals(currentVisitingExhibit)) )
                .collect(Collectors.toList());

        //check if focus is on on a later selected exhibit in plan (excluding currentLocation)
        return laterExhibits.contains(focus);

    }

    private void markAsVisited() {
        //如果当前location与remaining plan里的exhibit重合，那么就算用户visited this exhibit in plan
        //find the ID of current location
        String currentLocation = findNearestLocationID(currLocation);

        //find out if this location is part of the remaining plan
        if ( remainingPlan.contains(currentLocation) ) {
            //mark as visited
            remainingPlan.remove(currentLocation);
            visitedExhibits.add(currentLocation);
        }
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
                    currentLocationID = findNearestLocationID(currLocation);

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
                    if (onLaterSelectedExhibit(focus, currentLocationID)) {
                        replanPrompt(currentLayout);
                    }

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

    /*
    private void replan( LinearLayout linearLayout) {
        var builder2 = new AlertDialog.Builder(this)
                .setTitle("You are in the wrong track("
                        + findNearestExhibitID(currLocation)+"), you can do a replan")
                .setView(linearLayout)
                //Todo : replan sth
                .setPositiveButton("replan", (dialog2, which2) -> {




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


                })
                .setNegativeButton("Cancel", (dialog2, which2) -> {
                    dialog2.cancel();
                });
        builder2.show();
    }
    */

    private String nearestExhibitInPlan(String currentLocationID) {
        String cloestExhibitInPlan = "";
        double minDistance = Double.MAX_VALUE;
        GraphPath<String, IdentifiedWeightedEdge> path;

        for(String exhibitID : sortedID) {
            //find path of closetExhibitID to currentLocationID and check if the distance is less than minDistance
            //if yes, update minDistance

            path = DijkstraShortestPath.findPathBetween(graphInfoMap, currentLocationID, exhibitID);

            if(path.getWeight() < minDistance) {
                minDistance = path.getWeight();
                cloestExhibitInPlan = exhibitID;
            }
        }

        return cloestExhibitInPlan;
    }

    private int reorientFocusIndex(String currentLocationID, List<String> sortedPlan) {
        //if the currentLocationID is found at index X in sortedPlan, return index
        for (int i = 0; i < sortedPlan.size(); i++) {
            if (sortedPlan.get(i).equals(currentLocationID)) {
                return i;
            }
        }

        return -1;
    }

    //replan can only work for later selected exhibit in current plan
    private List<String> adjustedPlan() {
        List<String> newSortedPlan = new ArrayList<String>();

        //find where the user is near to
        String currentExhibit = nearestExhibitInPlan(currentLocationID);
        //update focusIndex
        focusIndex = reorientFocusIndex(currentExhibit, sortedID);
        //piazza @831
        visitedExhibits = new ArrayList<>();
        for (int i = 0; i < focusIndex; i++) {
            visitedExhibits.add(sortedID.get(i));
        }

        newSortedPlan.addAll(visitedExhibits);

        //remainingPlan = sortedID - visitedExhibits
        for (String visited : visitedExhibits) {
            remainingPlan.remove(visited);
        }

        //add remaining plan to newSortedPlan
        RoutePlanner newRoute = new RoutePlanner(remainingPlan, true);
        List<String> replannedRemainingPlan = newRoute.getRoute();
        newSortedPlan.addAll(replannedRemainingPlan);
        //catch back the remainingPlan from newRoute
        remainingPlan = replannedRemainingPlan;

        //update focus index and focus
        //focus should be the first destination of replannedRemainingPlan
        focus = replannedRemainingPlan.get(0);
        focusIndex = reorientFocusIndex(focus, newSortedPlan);

        //add exit gate to the sorted plan
        newSortedPlan.add(ZooData.findGate(vertexInfoMap));

        return newSortedPlan;
    }

    /*
    private List<String> adjustedPlan() {
        List<String> newSortedPlan = new ArrayList<String>();

        //replace visitedExhibits with a list of all exhibits before focusIndex
        //add them into the newSortedPlan
        visitedExhibits = sortedID.subList(0, focusIndex);
        newSortedPlan.addAll(visitedExhibits);

        //get rid of visitedExhibits from remainingPlan
        for (String visited : visitedExhibits) {
            remainingPlan.remove(visited);
        }

        //replan the remainingPlan, append them to the end of PLAN
        RoutePlanner newRoute = new RoutePlanner(remainingPlan, true);
        newSortedPlan.addAll(newRoute.getRoute());

        //find out which location is user closest to
        currentLocationID = findNearestLocationID(currLocation);

        //first destination (focus) becomes the current destination (closest exhibit location in plan)
        String currentFocusLocation = nearestExhibitInPlan(currentLocationID);
        focusIndex = reorientFocusIndex(currentFocusLocation, newSortedPlan);

        return newSortedPlan;
    }
    */

    private void onsitePrompt(LinearLayout linearLayout) {
        var onsitePromptBuilder = new AlertDialog.Builder(this)
                .setTitle("Onsite!")
                .setView(linearLayout)
                .setMessage("You are at the exhibit that you have selected to visit, " +
                        "click NEXT button to move on to the exhibit in plan!")
                .setPositiveButton("Okay!", (dialog, which) -> {

                });
        onsitePromptBuilder.show();
    }

    private void replanPrompt(LinearLayout linearLayout) {
        var replanPromptBuilder = new AlertDialog.Builder(this)
                .setTitle("Replan?")
                .setView(linearLayout)
                .setMessage("It seems like you are currently near " +
                        findNearestLocationName(currLocation) +
                        ", which is closer to a later selected exhibit on the original plan" +
                        ", do you want a replan?")
                .setPositiveButton("Replan", (dialog, which) -> {
                    //Log.d("TEST", nearestExhibitInPlan());
                    sortedID = adjustedPlan();
                    updateDirectionInfo();
                }).setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                });
        replanPromptBuilder.show();
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

        focus = sortedID.get(focusIndex);
        directionToNextExhibit = FindDirection.printPath(currentLocationID, focus, detailed);
        directionText.setText(directionToNextExhibit);
        nextExhibitDistance = FindDirection.printDistance(currentLocationID, focus);
        distanceText.setText(nextExhibitDistance);

    }

    public void styleClicked(View view) {
        this.detailed = !this.detailed;
        updateDirectionInfo();
    }

    //focus index --
    //focus = focus index
    //sortedID.remove(focusIndex + 1)
    //replan
    public void skipClicked(View view) {
        String skippedExhibit = "";

        //edge case: skip the first in plan
        if (focusIndex == 0) {
            skippedExhibit = sortedID.get(focusIndex);
            sortedID.remove(skippedExhibit);
            remainingPlan.remove(skippedExhibit);

            //edge case: only one exhibit in plan, and user skipped it
            if (sortedID.size() == 0) {
                finish();
                return;
            }

            updateDirectionInfo();

            return;
        }

        focusIndex--;
        skippedExhibit = sortedID.get(focusIndex + 1);
        remainingPlan.remove(skippedExhibit);
        sortedID.remove(skippedExhibit);
        sortedID = adjustedPlan();
        updateDirectionInfo();

    }
}
