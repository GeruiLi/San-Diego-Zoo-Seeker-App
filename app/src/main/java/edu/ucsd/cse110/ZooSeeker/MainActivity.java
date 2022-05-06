package edu.ucsd.cse110.ZooSeeker;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView searchedListView;

    // Define array adapter for ListView
    ArrayAdapter<String> arrayAdapter;

    // Define array List for List View data
    ArrayList<String> animalExhibitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //not going to use the graph yet for the searching function
        //Graph<String, IdentifiedWeightedEdge> graphInfoMap = ZooData.loadZooGraphJSON(this,"sample_zoo_graph.json");
        //Map<id, VertexInfo>
        Map<String, ZooData.VertexInfo> vertexInfoMap = ZooData.loadVertexInfoJSON(this,"sample_node_info.json");
        Map<String, ZooData.EdgeInfo> edgeInfoMap = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.json");

        //initialise ListView
        searchedListView = findViewById(R.id.searchedListView);

        //Initialize animalExhibitList
        animalExhibitList = new ArrayList<>();
        //Filter out all the exhibits into the animalExhibitList
        ZooData.VertexInfo exhibitInfo;
        //interate through the vertex map using the keys (id) of vertexInfoMap
        for (String key : vertexInfoMap.keySet()) {
            exhibitInfo = vertexInfoMap.get(key);
            //if this vertex is an exhibit, add it to animalExhibitList
            if (exhibitInfo.kind == ZooData.VertexInfo.Kind.EXHIBIT) {
                animalExhibitList.add(exhibitInfo.name);
            }
        }

        //Set adapter to ListView
        arrayAdapter
                = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        animalExhibitList);
        searchedListView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Populate the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        //Initialize the menu item search bar
        MenuItem searchViewItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) searchViewItem.getActionView();

        //attach query listener to the search view
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    //Override onQueryTextSubmit method
                    //called each time when a query is searched

                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        //If the searched query is contained in the list
                        //then filter the adapter using the filter method
                        //and use the query as its argument
                        if (animalExhibitList.contains(query)) {
                            arrayAdapter.getFilter().filter(query);
                        }
                        else {
                            //The query being searched is not found
                            Toast
                                    .makeText(MainActivity.this,
                                            "Searched Item Not Found",
                                            Toast.LENGTH_LONG)
                                    .show();
                        }
                        return false;
                    }

                    //This method is overridden to filter the adapter
                    //while the user is typing in search bar
                    @Override
                    public boolean onQueryTextChange(String newQueryText) {
                        arrayAdapter.getFilter().filter(newQueryText);
                        return false;
                    }

                }
        );

        return super.onCreateOptionsMenu(menu);
    }
}