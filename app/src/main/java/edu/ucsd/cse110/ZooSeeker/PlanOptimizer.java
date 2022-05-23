package edu.ucsd.cse110.ZooSeeker;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class PlanOptimizer {
    static List<String> userPlan = new ArrayList<String>();
    static String begin = "entrance_exit_gate";
    static LinkedHashMap<String, LinkedHashMap<String, Double>> adj = new LinkedHashMap<>();
    static Double tempDistanceCount = 0.0;
    static Double distance = Double.MAX_VALUE;
    static List<String> tempResult2 = new ArrayList<String>();
    static ArrayList<String> result1 = new ArrayList<String>();
    static LinkedHashMap<String, Double> exhibitDistance = new LinkedHashMap<>();;

    List<ExhibitListItem> exhibitListItems;  //user plan
    Graph<String, IdentifiedWeightedEdge> graphInfoMap;  // graph

    public PlanOptimizer(List<ExhibitListItem> exhibitListItems, Graph<String, IdentifiedWeightedEdge> graphInfoMap) {

        this.exhibitListItems = exhibitListItems;
        this.graphInfoMap = graphInfoMap;
    }

    public static void buildSampleGraph(){
       /* adj.put("gate", new HashMap(){{put("entrance",10.0);}}s);
        adj.put("entrance", new HashMap(){{put("gorillas",10.0);} {put("foxes",10.0);} {put("gators",10.0);}});
        adj.put("gorillas",new HashMap(){{put("lions",10.0);}}); */
        addEdge("gate","entrance", 10.0);
        addEdge("entrance","gorillas", 10.0);
        addEdge("entrance","foxes", 10.0);
        addEdge("entrance","gators", 10.0);
        addEdge("gorillas","lions", 10.0);
        addEdge("lions","elephant", 10.0);
        addEdge("gators", "lions",10.0);
    }


    public void buildGraph(){
        //for(ExhibitListItem exhibitListItem : exhibitListItems) {
        Set<IdentifiedWeightedEdge> edgesSet = graphInfoMap.edgeSet();
        //dentifiedWeightedEdge> edgesSet = graphInfoMap.edgesOf(exhibitListItems.get(0).getExhibitName()); //return set of neighbor
        Iterator<IdentifiedWeightedEdge> iterator = edgesSet.iterator();

        while(iterator.hasNext() ) {
            IdentifiedWeightedEdge edge = iterator.next();
            // if(graphInfoMap.getEdgeSource(edge) == exhibitListItem.getExhibitName())
            addEdge(graphInfoMap.getEdgeSource(edge), graphInfoMap.getEdgeTarget(edge), graphInfoMap.getEdgeWeight(edge));

        }
        // }
    }

    public static void addEdge(String source, String target, double weight){
        if(adj.containsKey(source))
            adj.get(source).put(target,  weight);
        else
            adj.put(source, new LinkedHashMap(){{put(target, weight);}});

        //addEdge(target,source,weight);
        if(adj.containsKey(target))
            adj.get(target).put(source,  weight);
        else
            adj.put(target, new LinkedHashMap(){{put(source, weight);}});
    }

    public void addPlanlist(){
        for(ExhibitListItem exhibitListItem : exhibitListItems){
            userPlan.add(exhibitListItem.getExhibitName());  //exhibitListItem.exhibitName)
        }
    }
    public  void addPlanlistManually(){
        //System.out.println("ss");
        //String str = exhibitListItems.get(0).getExhibitName();
        userPlan.add("foxes");
        userPlan.add("elephant");
    }

    public static void findPath(){
        int size = userPlan.size();
        Double smallestDistance = Double.MAX_VALUE;
        int indexTargetRemoved = -1;
        //reset static variable
        tempDistanceCount = 0.0;
        distance = Double.MAX_VALUE;

        //for loop to find the shortest node in list
        //remove the shortest node after used
        for(int i = 0; i < size; i++) {
            findDistance(begin, userPlan.get(i));

            if(smallestDistance > distance) {
                //update if needed
                smallestDistance = distance;
                indexTargetRemoved = i;
            }
            //when loop end, update userPlan and begin position
            if(i == size - 1) {
                begin = userPlan.get(indexTargetRemoved);
                userPlan.remove(indexTargetRemoved);
            }
        }
        // Test for total output include plan left, shortest node, its weight
        // and the nodes need to pass
        System.out.println("left plan" + userPlan);
        System.out.println("you should go to " + begin + " it's weight is " + distance);
        System.out.println("you will pass" + result1);
    }

    public static void findDistance(String begin, String end){
        //create Visited boolean hashmap for DSF
        LinkedHashMap<String, Boolean> Visited = new LinkedHashMap<>();
        List<String> nodesList = new ArrayList<>(adj.keySet());
        for(int i = 0; i < nodesList.size(); i++) {
            Visited.put(nodesList.get(i), false);
        }

        //Run DSF from begin node
        dfsRecursive(begin, Visited, end);
    }

    public static void dfsRecursive(String current, LinkedHashMap<String, Boolean> isVisited
            , String end) {
        //if find target, end function and if distance is small enough update static variable
        if(current.equals(end)) {
            System.out.println("REACH！");
            if(tempDistanceCount < distance) {
                distance = tempDistanceCount;
                result1 = new ArrayList<String>(tempResult2);
            }
            //save the distance for every node into hashmap exhitdistance;
            if(!exhibitDistance.containsKey(end) || exhibitDistance.get(end) > tempDistanceCount)
                exhibitDistance.put(end, tempDistanceCount);
            //Test for finding a road lead to end
            System.out.println("node we need passed" + tempResult2);
            System.out.println("Success! " + tempDistanceCount);
            return;
        }
        //if(current == )
        //set this node to visited
        isVisited.replace(current, false, true);

        //for loop to run another DSF and add info into temp node if not visited
        LinkedHashMap<String, Double> targets = adj.get(current);
        List<String> stringsList = new ArrayList<>(targets.keySet());
        for(int i = 0; i < stringsList.size(); i++) {
            String goal = stringsList.get(i);

            if (Boolean.compare(isVisited.get(goal), false) == 0) { //never visit this node
                System.out.println("my temp goal is " + goal + ", I am " + current
                        + " my final goal is " + end);
                //System.out.println(isVisited);
                // add count for distance and add passed node
                tempDistanceCount += targets.get(goal);
                tempResult2.add(goal);
                dfsRecursive(goal, isVisited, end);
                // reduce count for distance and remove the node added before
                tempDistanceCount -= targets.get(goal);
                tempResult2.remove(tempResult2.size() - 1);
            }
            // if this is the last action for this loop, set node to no visited
            if(i == stringsList.size() -1) {
                isVisited.replace(current, true, false);
            }
        }
    }

    public static List<String> getInitialDistanceToExhibitions(){
        List<String> distanceToExhibitions = new ArrayList<String>();
        int size = userPlan.size();
        //run findDistance for every exhibitions to get the distance
        for(int i = 0; i < size; i++) {
            findDistance(begin, userPlan.get(i));
            //update our list
            if(exhibitDistance.get(userPlan.get(i)) == 1.0)
                distanceToExhibitions.add(exhibitDistance.get(userPlan.get(i)) + " foot");
            else distanceToExhibitions.add(exhibitDistance.get(userPlan.get(i)) + " feet");
        }
        return distanceToExhibitions;
    }

    public static void main(String[] args) {
        //buildSampleGraph();
        //addPlanlist();
        // System.out.println(userPlan.get(0));
        // System.out.println(userPlan.get(1));
        //  findPath();
        //  findPath(); //second time find path
    }

    public List<String> optimize(){
        //buildSampleGraph();
        buildGraph();
        addPlanlist();
        List<String> initialdDistanceList = getInitialDistanceToExhibitions();
        //addPlanlistManually();
        //findPath();

        //return exhibitListItems.get(0).getExhibitName();
        return initialdDistanceList;
        //return "completed";
    }
}
