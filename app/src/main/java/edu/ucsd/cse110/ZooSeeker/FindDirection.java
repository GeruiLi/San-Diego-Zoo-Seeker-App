package edu.ucsd.cse110.ZooSeeker;



import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.IDToNameMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.curLocation;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.testLati;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.testLong;
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

    public static String printPath(String start, String goal){
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(graphInfoMap, start, goal);

        //future feature for real location
        //String rlt = "You are close to " + start + ". ";
        String rlt = "" + testLong + " " + testLati + " ";
        rlt += "You are close to " + findNearestExhibitID() + " ";

        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            String s = "Walk " + (int)graphInfoMap.getEdgeWeight(e)
                    + " feet along " + edgeInfoMap.get(e.getId()).street + ". ";
            rlt = rlt.concat(s);
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
    
    public static String findNearestExhibitID() {
        String result = "";
        //String hardcode = "entrance_exit_gate";

        // Todo: 监视（call这个function，如果result！= cur 弹提示框问点不点，
        // TOdo: 按了replan就改变cur to result） + 提示框 + replan button

        Location tempLo = null;

        double max = -1;

        for( String key : vertexInfoMap.keySet() ){
            ZooData.VertexInfo exhibitInfo = vertexInfoMap.get(key);

            Location endPoint=new Location(exhibitInfo.id);
            endPoint.setLatitude(exhibitInfo.lat);
            endPoint.setLongitude(exhibitInfo.lng);

            double distance=curLocation.distanceTo(endPoint);
            if(distance > max){
                max = distance;
                System.out.println(result);
                result = exhibitInfo.name;
            }
        }

        return result;

    }




}
