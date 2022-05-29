package edu.ucsd.cse110.ZooSeeker;



import static edu.ucsd.cse110.ZooSeeker.DirectionActivity.currLocation;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.IDToNameMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.curLocation;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToParentIDMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.testLati;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.testLong;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.graphInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.edgeInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.sortedID;


import android.location.Location;

import androidx.annotation.NonNull;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Map;

public class FindDirection {

    //Print the path from start point to end point
    //@param String start point
    //@param String end point
    //@return String Total Direction information
    public static String printPath(String start, String goal){
        GraphPath<String, IdentifiedWeightedEdge> path =
                DijkstraShortestPath.findPathBetween(graphInfoMap, start, goal);

        //Construct Path information
        String rlt = "You are close to " +
                findNearestExhibitID(currLocation) + ". ";
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            String s = "Walk " + (int)graphInfoMap.getEdgeWeight(e)
                    + " feet along " + edgeInfoMap.get(e.getId()).street + ". ";
            rlt = rlt.concat(s);
        }
        return rlt;
    }

    //Print the total distance from start point to end point
    //@param String start point
    //@param String end point
    //@return String Total Direction's Distance Information
    public static String printDistance(String start, String goal){
        GraphPath<String, IdentifiedWeightedEdge> path =
                DijkstraShortestPath.findPathBetween(graphInfoMap, start, goal);

        //get the total weight and construct and return it
        double weight = getTotalWeight(path);
        String rlt = IDToNameMap.get(goal) + ", " + (int)weight + " feet away.";
        return rlt;
    }

    //find the total point from a graph path
    //@param GraphPath<String, IdentifiedWeightedEdge> a graphPath include path information
    //@return Double Total Distance passed in the path
    private static double getTotalWeight(GraphPath<String, IdentifiedWeightedEdge> path) {
        double weight = 0;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            weight += graphInfoMap.getEdgeWeight(e);
        }
        return weight;
    }

    //find the nearest exhibit ID from a location
    //@param Location a location include lat and lng with its id
    //@return String the nearest exhibit id from this location
    public static String findNearestExhibitID( Location location) {
        String result = "";

        //loop to get the shortest one from all the node
        for( String key : vertexInfoMap.keySet() ){
            ZooData.VertexInfo exhibitInfo = vertexInfoMap.get(key);
            Location endPoint = setLocation(exhibitInfo.id, exhibitInfo.lat, exhibitInfo.lng);
            //test
            {
                //    "lat": 32.74505139995802,
                //    "lng": -117.15794384136309
                //TEST flamingo
                Location testPoint = setLocation("TEST", 32.746302644092815, -117.15794384136309);
                /* "lat": 32.746302644092815,
                "lng": -117.16659525430192*/
                //double distance=testPoint.distanceTo(endPoint);
            }
            //calculate shortest one and record, return it
            result = calculateShortestExhibitId(location, result, exhibitInfo, endPoint);
        }

        return result;
    }

    //set the location's id, lat and lng
    //@param String id of location
    //@param double latitude
    //@param double longitude
    //@return Location with new id, latitude and longitude
    @NonNull
    private static Location setLocation(String id, double lat, double lng) {
        //update  location with params
        Location endPoint = new Location(id);
        endPoint.setLatitude(lat);
        endPoint.setLongitude(lng);
        return endPoint;
    }

    //find the shortest exhibit's id from location
    //@param Location a location
    //@param String result to be returned
    //@param ZooData.vertexInfo exhibit's info includes all the exhibits
    //@param Location an endPoint for location
    //@return String shortest exhibit's id
    private static String calculateShortestExhibitId(Location location, String result,
                                               ZooData.VertexInfo exhibitInfo, Location endPoint) {
        //use loop to get the shortest exhibit's id from all exhibits
        double min = Integer.MAX_VALUE;
        if(location.distanceTo(endPoint) < min){
            min = location.distanceTo(endPoint);
            System.out.println(result);
            result = exhibitInfo.name;
        }
        result = nameToParentIDMap.get(result);
        return result;
    }
}
