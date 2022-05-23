package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToIDMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.sortedID;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Map;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.graphInfoMap;

public class DirectionActivity extends AppCompatActivity {
    private int index;
    private int current;
    private Graph<String, IdentifiedWeightedEdge> graphInfoMap;
    private Map<String, ZooData.EdgeInfo> edgeInfoMap;
    private List<ExhibitListItem> exhibitListItems;
    private String cur;
    private String nxt;

    private ExhibitListItemDao dao;
    private ExhibitTodoDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        if(sortedID != null){
            current = 0;
            index = sortedID.size();
            this.cur = ZooData.findGate(vertexInfoMap);
            this.nxt = sortedID.get(current);
            current++;

            String firstDirection = FindDirection.printPath(cur ,nxt);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(firstDirection);

            TextView distanceText = findViewById(R.id.distance_inf);
            String next = FindDirection.printDistance(cur ,nxt);
            distanceText.setText(next);
            this.cur = this.nxt;
        }

    }

    public void NextClicked(View view) {
        if(sortedID != null && current <= index){
            this.nxt = sortedID.get(current);

            String direction = FindDirection.printPath(cur ,nxt);

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(direction);

            TextView distanceText = findViewById(R.id.distance_inf);

            String next = FindDirection.printDistance(cur,nxt);

            distanceText.setText(next);
            this.cur = this.nxt;
            current = current + 1;
        }
        else{
            finish();
        }
    }
}
