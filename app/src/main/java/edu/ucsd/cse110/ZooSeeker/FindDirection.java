package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.IDToNameMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.graphInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.edgeInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.sortedID;


import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Map;

public class FindDirection {

    public static String printPath(String start, String goal){
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(graphInfoMap, start, goal);

        String rlt = "    ";

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

        String rlt = IDToNameMap.get(goal) + ", " + (int)weight + " away.";

        return rlt;
    }

}
