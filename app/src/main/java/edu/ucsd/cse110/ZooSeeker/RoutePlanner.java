package edu.ucsd.cse110.ZooSeeker;

import edu.ucsd.cse110.ZooSeeker.SearchListActivity;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.graphInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToIDMap;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutePlanner {
    public List<String> route;

    //Graph<String, IdentifiedWeightedEdge> graphInfoMap;
    //public Map<String, ZooData.VertexInfo> vertexInfoMap;
    //public Map<String, ZooData.EdgeInfo> edgeInfoMap;

    public List<String> userPlan = new ArrayList<String>();
    public String start = "entrance_exit_gate";
    //initialize idToNameMap



    public RoutePlanner(List<String> userPlan) {
        for(String exhibit : userPlan){
            this.userPlan.add(nameToIDMap.get(exhibit));
        }

        buildRoute();
    }

    public void buildRoute() {
        String end = "";
        double distance = Double.MAX_VALUE;
        GraphPath<String, IdentifiedWeightedEdge> path = null;
        GraphPath<String, IdentifiedWeightedEdge> shortestPath = null;

        while (!userPlan.isEmpty()) {
            for (String exhibit : userPlan) {
                path = DijkstraShortestPath.findPathBetween(graphInfoMap, start, exhibit);
                if (path.getWeight() < distance) {
                    distance = path.getWeight();
                    shortestPath = path;
                    end = exhibit;
                }
            }

            for (IdentifiedWeightedEdge e : shortestPath.getEdgeList()) {
                route.add(graphInfoMap.getEdgeSource(e));
            }

            start = end;
            userPlan.remove(end);
        }
        route.add(end);
    }

    public List<String> getRoute() {
        return route;
    }
}
