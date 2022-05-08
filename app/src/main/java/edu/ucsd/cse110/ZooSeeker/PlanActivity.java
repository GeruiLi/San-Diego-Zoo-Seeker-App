package edu.ucsd.cse110.ZooSeeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.jgrapht.Graph;

import java.util.Collections;
import java.util.List;

public class PlanActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView recyclerView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        ExhibitListItemDao dao;
        ExhibitTodoDatabase db;

        Context context = getApplication().getApplicationContext();
        db = ExhibitTodoDatabase.getSingleton(context);
        dao = db.exhibitListItemDao();

        List<ExhibitListItem> exhibitListItems = dao.getAll();
        List<String> distance = Collections.emptyList();

        PlanListAdapter adapter = new PlanListAdapter();
        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.plan_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setPlanListItems(exhibitListItems);
        adapter.setDistance(distance);
    }

    public void DirectionClicked(View view) {
    }

    public void ReturnClicked(View view) {
        finish();
    }
}