package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToIDMap;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutePlanner {
    public List<String> route;

    Graph<String, IdentifiedWeightedEdge> graphInfoMap;
    public Map<String, ZooData.VertexInfo> vertexInfoMap;
    public Map<String, ZooData.EdgeInfo> edgeInfoMap;

    public List<String> userPlan;
    public String start = "entrance_exit_gate";
    //initialize idToNameMap



    public RoutePlanner(Graph<String, IdentifiedWeightedEdge> graphInfoMap, Map<String, ZooData.VertexInfo> vertexInfoMap,
                        Map<String, ZooData.EdgeInfo> edgeInfoMap, List<String> userPlan) {
        this.graphInfoMap = graphInfoMap;
        this.vertexInfoMap = vertexInfoMap;
        this.edgeInfoMap = edgeInfoMap;

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

            List<IdentifiedWeightedEdge> edgeList = shortestPath.getEdgeList();

            //route.add(nameToIDMap.get(start));
            for (IdentifiedWeightedEdge exhibit : edgeList) {  // exhibit is id, add all exhibit to route
                route.add(nameToIDMap.get(graphInfoMap.getEdgeSource(exhibit)));
            }
           // route.add(nameToIDMap.get(end));
            start = end;
            userPlan.remove(end);
        }
        route.add(nameToIDMap.get(end));
    }

    List<String> getRoute() {
        return route;
    }
}
