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
public class PrintPathTest {

    @Rule
    public ActivityScenarioRule<SearchListActivity> scenarioRule
            = new ActivityScenarioRule<>(SearchListActivity.class);

    @Test
    public void test_print_brief() {
        ActivityScenario<SearchListActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            String start = "entrance_exit_gate";
            String goal = "intxn_front_treetops";

            GraphPath<String, IdentifiedWeightedEdge> path =
                    DijkstraShortestPath.findPathBetween(graphInfoMap, start, goal);

            String s = FindDirection.printBrief(path, "");

            String rlt = "\n- Walk 10 ft along Gate Path to Front Street / Treetops Way. \n";

            assertEquals(s, rlt);
        });

    }
}