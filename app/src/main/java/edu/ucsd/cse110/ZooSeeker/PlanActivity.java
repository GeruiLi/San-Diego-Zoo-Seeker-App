package edu.ucsd.cse110.ZooSeeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.IDToNameMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.distance;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.selectedExhibitList;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.sortedID;


public class PlanActivity extends AppCompatActivity {

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    public RecyclerView recyclerView;
    private List<String> exhibitNames;
    private PlanListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String all = "{";
        for (String e : selectedExhibitList) {
            all += e + ", ";
        }
        Log.d("TEST", all + "}\n");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        adapter = new PlanListAdapter();
        adapter.setHasStableIds(true);

        exhibitNames = new ArrayList<>();

        recyclerView = findViewById(R.id.plan_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        for(String s : sortedID){
            exhibitNames.add(IDToNameMap.get(s));
        }

        adapter.setPlanListItems(exhibitNames);
        adapter.setDistance(distance);
    }

    public void DirectionClicked(View view) {
        Intent intent = new Intent(this, DirectionActivity.class);
        intent.putExtra("isResume",false);
        finish();
        startActivity(intent);
    }

    public void ReturnClicked(View view) {
        finish();
    }

    public void deleteAllClicked(View view) {
        Utilities.deleteExhibitPlan();
        setContentView(R.layout.activity_plan);
        selectedExhibitList.clear();
    }

}