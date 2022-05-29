package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.DirectionActivity.currLocation;
import static edu.ucsd.cse110.ZooSeeker.FindDirection.findNearestExhibitID;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.graphInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToParentIDMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoutePlanner {
    private List<String> route;
    private List<String> distance;
    private String start;

    public DijkstraShortestPath path;
    public List<String> userPlan = new ArrayList<String>();
    public RoutePlanner(List<ExhibitListItem> plan, Boolean rePlan) {
        //add exhibit in plan into userplan
        for(ExhibitListItem exhibit : plan){
            this.userPlan.add(nameToParentIDMap.get(exhibit.exhibitName));
        }
        route = new ArrayList<>();
        distance = new ArrayList<>();

        //build start and path
        if(!rePlan) this.start = findGate(vertexInfoMap);
        else this.start = findNearestExhibitID(currLocation);

        this.path = new DijkstraShortestPath(graphInfoMap);
        buildRoute(this.userPlan, start);
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
        //Create variables
        String shortest = "";
        double lastDis = 0;
        double min = Double.MAX_VALUE;

        //build route and distance
        while(userPlan.size() != 0){
            for(String id : userPlan){
                if(min > path.getPathWeight(current,id)){
                    shortest = id;
                    min = path.getPathWeight(current,id);
                }
            }

            //add shortest to route,its distance to distance
            route.add(shortest);
            distance.add(Double.toString(min + lastDis) + " feet");
            //loop update
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
