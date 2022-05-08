package edu.ucsd.cse110.ZooSeeker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class PlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        ExhibitListItemDao dao;
        ExhibitTodoDatabase db;

        Context context = getApplication().getApplicationContext();
        db= ExhibitTodoDatabase.getSingleton(context);
        dao = db.exhibitListItemDao();

        List<ExhibitListItem> exhibitListItems = dao.getAll();

        TextView textView = findViewById(R.id.exhibitItems1);

        textView.setText(exhibitListItems.get(0).getExhibitName());


    }
}