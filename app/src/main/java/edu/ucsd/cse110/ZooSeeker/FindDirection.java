package edu.ucsd.cse110.ZooSeeker;



import static edu.ucsd.cse110.ZooSeeker.DirectionActivity.currLocation;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.IDToNameMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToParentIDMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.graphInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.edgeInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.sortedID;


import android.location.Location;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Map;

public class FindDirection {

    public static String printPath(String start, String goal, boolean detailed){

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(graphInfoMap, start, goal);
        String rlt = "You are close to " + findNearestExhibitID(currLocation) + ". ";
        if(detailed == true){

            //future feature for real location
            //String rlt = "" + testLong + " " + testLati + " ";
            //rlt += "You are close to " + findNearestExhibitID() + " ";

            List<String> verList = path.getVertexList();
            List<IdentifiedWeightedEdge> edgeList = path.getEdgeList();

            for (int x = 0; x < edgeList.size(); x++) {
                String s = "Proceed on " + (int)graphInfoMap.getEdgeWeight(edgeList.get(x))
                        + " feet along " + edgeInfoMap.get(edgeList.get(x).getId()).street + " towards "
                        + IDToNameMap.get(verList.get(x+1)) +  ". ";
                rlt = rlt.concat(s);
            }
        }
        else{

            //String rlt = "" + testLong + " " + testLati + " ";
            //rlt += "You are close to " + findNearestExhibitID() + " ";

            List<IdentifiedWeightedEdge> edgeList = path.getEdgeList();
            List<String> verList = path.getVertexList();

            for (int x = 0; x < edgeList.size(); x++) {
                double weight = graphInfoMap.getEdgeWeight(edgeList.get(x));
                String street = edgeInfoMap.get(edgeList.get(x).getId()).street;
                if(x < edgeList.size() - 1 && street.equals(edgeInfoMap.get(edgeList.get(x + 1).getId()).street)){
                    weight = weight + graphInfoMap.getEdgeWeight(edgeList.get(x + 1));
                    x++;
                }
                String s = "Walk " + (int)weight
                        + " ft along " + street + " to "
                        + IDToNameMap.get(verList.get(x+1)) +  ". ";
                rlt = rlt.concat(s);
            }
        }
        return rlt;
    }

    public static String printDistance(String start, String goal){
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(graphInfoMap, start, goal);

        double weight = 0;

        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            weight += graphInfoMap.getEdgeWeight(e);
        }

        String rlt = IDToNameMap.get(goal) + ", " + (int)weight + " feet away.";

        return rlt;
    }

    public static String findNearestLocationName(Location location) {
        String result = "";
        double min = Integer.MAX_VALUE;

        // Todo: 监视（call这个function，如果result！= cur 弹提示框问点不点，
        // TOdo: 按了replan就改变cur to result） + 提示框 + replan button

        //loop to get the shortest one from all the node
        for( String key : vertexInfoMap.keySet() ){
            ZooData.VertexInfo exhibitInfo = vertexInfoMap.get(key);
            Location endPoint = new Location(exhibitInfo.id);
            endPoint.setLatitude(exhibitInfo.lat);
            endPoint.setLongitude(exhibitInfo.lng);

            //test
            {
                //    "lat": 32.74505139995802,
                //    "lng": -117.15794384136309
                //TEST flamingo

                Location testPoint = new Location("TEST");
                testPoint.setLatitude(32.746302644092815);
                testPoint.setLongitude(-117.15794384136309);

                /* "lat": 32.746302644092815,
                "lng": -117.16659525430192*/

                //double distance=testPoint.distanceTo(endPoint);
            }

            //calculate shortest one and record, return it
            double distance=location.distanceTo(endPoint);
            if(distance < min){
                min = distance;
                System.out.println(result);
                result = exhibitInfo.name;
            }
        }

        return result;
    }

    public static String findNearestExhibitID(Location location) {
        String result = "";
        double min = Integer.MAX_VALUE;

        // Todo: 监视（call这个function，如果result！= cur 弹提示框问点不点，
        // TOdo: 按了replan就改变cur to result） + 提示框 + replan button

        //loop through selected IDs to find the nearest exhibit
        for( String id : sortedID ){
            //ZooData.VertexInfo exhibitInfo = vertexInfoMap.get(key);
            ZooData.VertexInfo exhibitInfo = vertexInfoMap.get(id);
            Location endPoint = new Location(exhibitInfo.id);
            endPoint.setLatitude(exhibitInfo.lat);
            endPoint.setLongitude(exhibitInfo.lng);

            //test
            {
                //    "lat": 32.74505139995802,
                //    "lng": -117.15794384136309
                //TEST flamingo

                Location testPoint = new Location("TEST");
                testPoint.setLatitude(32.746302644092815);
                testPoint.setLongitude(-117.15794384136309);

                /* "lat": 32.746302644092815,
                "lng": -117.16659525430192*/

                //double distance=testPoint.distanceTo(endPoint);
            }

            //calculate shortest one and record, return it
            double distance=location.distanceTo(endPoint);
            if(distance < min){
                min = distance;
                System.out.println(result);
                result = exhibitInfo.name;
            }
        }

        result = nameToParentIDMap.get(result);

        return result;
    }
}