package edu.ucsd.cse110.ZooSeeker;

import edu.ucsd.cse110.ZooSeeker.SearchListActivity;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.graphInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToIDMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;

import androidx.annotation.NonNull;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutePlanner {
    private List<String> route;
    private List<String> distance;
    private String gate;

    public DijkstraShortestPath path;
    public List<String> userPlan = new ArrayList<String>();
    //initialize idToNameMap

    public RoutePlanner(@NonNull List<ExhibitListItem> plan) {
        for(ExhibitListItem exhibit : plan){
            this.userPlan.add(nameToIDMap.get(exhibit.exhibitName));
        }
        route = new ArrayList<>();
        distance = new ArrayList<>();
        this.gate = findGate(vertexInfoMap);
        this.path = new DijkstraShortestPath(graphInfoMap);
        buildRoute(this.userPlan, gate);
    }

    public String findGate(Map<String, ZooData.VertexInfo> vertexInfoMap){
        ZooData.VertexInfo exhibitInfo;
        for (String key : vertexInfoMap.keySet()) {
            exhibitInfo = vertexInfoMap.get(key);
            //if this vertex is an exhibit, add it to animalExhibitList
            if (exhibitInfo.kind == ZooData.VertexInfo.Kind.GATE) {
                return exhibitInfo.id;
            }
        }
        return "";
    }

    public void buildRoute(List<String> userPlan, String current) {

        double lastDis = 0;
        while(userPlan.size() != 0){
            double min = Double.MAX_VALUE;
            String shortest = "";
            for(String id : userPlan){
                if(min > path.getPathWeight(current,id)){
                    shortest = id;
                    min = path.getPathWeight(current,id);
                }
            }
            route.add(shortest);

            distance.add(Double.toString(min + lastDis) + " feet");
            lastDis = min;
            userPlan.remove(shortest);
            current = shortest;
        }

    }

    public List<String> getRoute() {
        return this.route;
    }

    public List<String> getDistance(){
        return this.distance;
    }
}
