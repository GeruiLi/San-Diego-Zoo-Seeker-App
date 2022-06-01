package edu.ucsd.cse110.ZooSeeker;

import static org.junit.Assert.assertEquals;

import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.graphInfoMap;
import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RunWith(AndroidJUnit4.class)
public class FindDirectionTest {

    @Rule
    public ActivityScenarioRule<SearchListActivity> scenarioRule
            = new ActivityScenarioRule<>(SearchListActivity.class) ;

    @Test
    public void test_print_detailed(){
        ActivityScenario<SearchListActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            String start = "entrance_exit_gate";
            String goal = "intxn_front_treetops";

            GraphPath<String, IdentifiedWeightedEdge> path =
                    DijkstraShortestPath.findPathBetween(graphInfoMap, start, goal);

            String s = FindDirection.printDetail(path, "");

            String rlt = "\n- Proceed on 10 feet along Gate Path towards Front Street / Treetops Way. \n";

            assertEquals(s, rlt);
        });
    }
}
