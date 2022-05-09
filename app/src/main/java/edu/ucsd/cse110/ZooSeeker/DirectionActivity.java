package edu.ucsd.cse110.ZooSeeker;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.nameToIDMap;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jgrapht.Graph;

import java.util.List;
import java.util.Map;

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

        Context context = getApplication().getApplicationContext();
        db = ExhibitTodoDatabase.getSingleton(context);
        dao = db.exhibitListItemDao();

        exhibitListItems = dao.getAll();

        this.index = exhibitListItems.size();
        this.current = 0;

        this.graphInfoMap = ZooData.loadZooGraphJSON(this,"sample_zoo_graph.json");
        this.edgeInfoMap = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.json");

        this.cur = "entrance_exit_gate";
        this.nxt = "entrance_plaza";

        IdentifiedWeightedEdge startEdge = graphInfoMap.getEdge(cur,nxt);
        double weight = graphInfoMap.getEdgeWeight(startEdge);

        String firstDirection = "Walk "
                + String.valueOf((int)weight) + " feet along " +
                edgeInfoMap.get(startEdge.getId()).street + ".";

        TextView directionText = findViewById(R.id.direction_inf);
        directionText.setText(firstDirection);

        TextView distanceText = findViewById(R.id.distance_inf);
        String next = "entrance_plaza, " + String.valueOf((int)weight) + " ft";
        distanceText.setText(next);
        this.cur = this.nxt;
    }

    public void NextClicked(View view) {
        if(current <= index){
            this.nxt = nameToIDMap.get(exhibitListItems.get(current).exhibitName);
            IdentifiedWeightedEdge edge = graphInfoMap.getEdge(cur,nxt);
            double weight = graphInfoMap.getEdgeWeight(edge);
            String direction = "Walk "
                    + String.valueOf((int)weight) + " feet along " +
                    edgeInfoMap.get(edge.getId()).street + ".";

            TextView directionText = findViewById(R.id.direction_inf);
            directionText.setText(direction);

            TextView distanceText = findViewById(R.id.distance_inf);

            String next = exhibitListItems.get(current).exhibitName + " ," + String.valueOf((int)weight) + " ft";
            distanceText.setText(next);
            this.cur = this.nxt;
            current = current + 1;
        }
        else{
            for (ExhibitListItem item: exhibitListItems){
                dao.delete(item);
            }
            finish();
        }
    }
}
