package edu.ucsd.cse110.ZooSeeker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SearchListActivity extends AppCompatActivity {
    public static final String SELECTED_TOTAL = "# of Exhibits Selected :";

    //view model
    private ExhibitTodoViewModel exhibitTodoViewModel;

    //views
    public ListView searchedListView;
    public RecyclerView exhibitRecyclerView;
    public static TextView exhibitCountTextView;

    // Define array adapter for searchedListView
    public ArrayAdapter<String> pullDownMenuArrayAdapter;

    // Define array Lists for ListView data
    public ArrayList<String> animalExhibitList;

    // Data structure, adapters, database modules related to the exhibit list
    public static List<ExhibitListItem> exhibitListItems;
    public static ExhibitListItemDao exhibitListItemDao;
    public ExhibitListAdapter exhibitListAdapter;

    // ZooData maps and graphs
    public static Map<String, ZooData.VertexInfo> vertexInfoMap;
    public static Map<String, ZooData.EdgeInfo> edgeInfoMap;
    public static Graph<String, IdentifiedWeightedEdge> graphInfoMap;

    // Map that maps exhibit id to exhibit name <name, id>
    public static Map<String, String> nameToIDMap;
    public static Map<String, String> IDToNameMap;

    public static List<String> sortedID;
    public static List<String> distance;

    // String that store the selectedExhibit
    private String selectedExhibit;

    // ArrayList<String> that store all selected exhibits
    private List<String> selectedExhibitList;

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<List<String>> future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        /*
        Section for vertex/edge/graph info
         */

        //not going to use the graph yet for the searching function
        //Graph<String, IdentifiedWeightedEdge> graphInfoMap = ZooData.loadZooGraphJSON(this,"sample_zoo_graph.json");
        graphInfoMap = ZooData.loadZooGraphJSON(this,"sample_zoo_graph.json");
        //Map<id, VertexInfo>
        vertexInfoMap = ZooData.loadVertexInfoJSON(this,"sample_node_info.json");
        edgeInfoMap = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.json");

        //initialize idToNameMap
        nameToIDMap = new HashMap<>();
        IDToNameMap = new HashMap<>();
        sortedID = Collections.emptyList();
        distance = Collections.emptyList();
        selectedExhibitList = new ArrayList<>();

        /*
        Section for search bar
         */

        //initialize ListView for search bar scroll-down menu
        searchedListView = findViewById(R.id.searchedListView);

        //Initialize animalExhibitList and exhibitIdList
        animalExhibitList = new ArrayList<>();

        //textview for showing total count of selected exhibits
        exhibitCountTextView = (TextView) findViewById(R.id.exhibitListIndicator);

        //Filter out all the exhibits into the animalExhibitList
        ZooData.VertexInfo exhibitInfo;
        //interate through the vertex map using the keys (id) of vertexInfoMap
        for (String key : vertexInfoMap.keySet()) {
            exhibitInfo = vertexInfoMap.get(key);
            //if this vertex is an exhibit, add it to animalExhibitList
            nameToIDMap.put(exhibitInfo.name, exhibitInfo.id);
            IDToNameMap.put(exhibitInfo.id, exhibitInfo.name);
            if (exhibitInfo.kind == ZooData.VertexInfo.Kind.EXHIBIT) {
                animalExhibitList.add(exhibitInfo.name);
            }
        }

        //Set adapter to searchedListView
        pullDownMenuArrayAdapter
                = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                animalExhibitList);
        searchedListView.setAdapter(pullDownMenuArrayAdapter);

        //onclick listener for search bar menu entries
        searchedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedExhibit = (String) parent.getItemAtPosition(position);

                //Get data from Dao and update total selected count
                exhibitListItems = exhibitListItemDao.getAll();

                for (ExhibitListItem item : exhibitListItems) {
                    selectedExhibitList.add(item.exhibitName);
                }

                if (!selectedExhibitList.contains(selectedExhibit)) {
                    exhibitTodoViewModel.createTodo(selectedExhibit);
                }
            }
        });

        /*
        Section for exhibit list
         */

        exhibitTodoViewModel = new ViewModelProvider(this)
                .get(ExhibitTodoViewModel.class);

        exhibitListItemDao =
                ExhibitTodoDatabase.getSingleton(this).exhibitListItemDao();
        exhibitListItems = exhibitListItemDao.getAll();

        exhibitListAdapter = new ExhibitListAdapter();
        exhibitListAdapter.setHasStableIds(true);
        exhibitListAdapter.setOnCheckBoxClickedHandler(exhibitTodoViewModel::toggleSelected);
        exhibitListAdapter.setOnDeleteBtnClickedHandler(exhibitTodoViewModel::setDeleted);
        exhibitListAdapter.setOnTextChangedHandler(exhibitTodoViewModel::updateText);
        exhibitTodoViewModel
                .getTodoListItems()
                .observe(this, exhibitListAdapter::setExhibitListItems);

        exhibitRecyclerView = findViewById(R.id.exhibitItems);
        exhibitRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exhibitRecyclerView.setAdapter(exhibitListAdapter);

        /*
        Section for plan button
         */

        findViewById(R.id.plan_btn).setOnClickListener(this::onLaunchPlanClicked);
    }

    /*
    Title: Android SearchView with Example
    Author: Rishabh007
    Date: 18 Feb, 2021
    Type: Android
    Availability: https://www.geeksforgeeks.org/android-searchview-with-example/
    */
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
                            pullDownMenuArrayAdapter.getFilter().filter(query);
                        }
                        else {
                            //The query being searched is not found
                            Toast
                                    .makeText(SearchListActivity.this,
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
                        pullDownMenuArrayAdapter.getFilter().filter(newQueryText);
                        return false;
                    }

                }
        );

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Get data from Dao and update total selected count
        exhibitListItems = exhibitListItemDao.getAll();

        exhibitCountTextView.setText(SELECTED_TOTAL + " " + exhibitListItems.size());
    }

    public void onLaunchPlanClicked(View view) {
        RoutePlanner planner = new RoutePlanner(exhibitListItems);
        sortedID = planner.getRoute();
        distance = planner.getDistance();

        Intent intent = new Intent(this, PlanActivity.class);
        startActivity(intent);
    }

    public void onLaunchResumeClicked(View view) {
        if(exhibitListItems.isEmpty()) {
            // display error message
            Utilities.showAlert(this,"You have no plan in progress");
        }
        else {
            RoutePlanner planner = new RoutePlanner(exhibitListItems);
            sortedID = planner.getRoute();
            distance = planner.getDistance();

            Intent intent = new Intent(this, DirectionActivity.class);
            intent.putExtra("isResume", true);
            startActivity(intent);
        }
    }
}
