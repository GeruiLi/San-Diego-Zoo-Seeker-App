package edu.ucsd.cse110.ZooSeeker;

import static org.junit.Assert.assertEquals;

import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static edu.ucsd.cse110.ZooSeeker.SearchListActivity.vertexInfoMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@RunWith(AndroidJUnit4.class)
public class FindGateTest {
    @Rule
    public ActivityScenarioRule<SearchListActivity> scenarioRule
            = new ActivityScenarioRule<>(SearchListActivity.class);

    @Test
    public void test_find_gate() {
        ActivityScenario<SearchListActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            String gate = RoutePlanner.findGate(vertexInfoMap);
            assertEquals(gate, "entrance_exit_gate");
        });
    }
}