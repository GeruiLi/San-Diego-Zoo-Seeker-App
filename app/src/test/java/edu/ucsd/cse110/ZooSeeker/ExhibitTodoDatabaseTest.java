package edu.ucsd.cse110.ZooSeeker;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class ExhibitTodoDatabaseTest {
    private ExhibitListItemDao dao;
    private ExhibitTodoDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ExhibitTodoDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.exhibitListItemDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsert() {
        ExhibitListItem item1 = new ExhibitListItem("Panda", false, 0);
        ExhibitListItem item2 = new ExhibitListItem("Racoon", false, 1);

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testGet() {
        ExhibitListItem insertedItem = new ExhibitListItem("Panda", false, 0);
        long id = dao.insert(insertedItem);

        ExhibitListItem item = dao.get(id);
        assertEquals(id, item.id);
        assertEquals(insertedItem.exhibitName, item.exhibitName);
        assertEquals(insertedItem.exhibitName, item.exhibitName);
        assertEquals(insertedItem.order, item.order);
    }

    @Test
    public void testUpdate() {
        ExhibitListItem item = new ExhibitListItem("Panda", false, 0);
        long id = dao.insert(item);

        item = dao.get(id);
        item.exhibitName = "Racoon";
        int itemsUpdated = dao.update(item);
        assertEquals(1, itemsUpdated);

        item = dao.get(id);
        assertNotNull(item);
        assertEquals("Racoon", item.exhibitName);
    }

    @Test
    public void testDelete() {
        ExhibitListItem item = new ExhibitListItem("Panda", false, 0);
        long id = dao.insert(item);

        item = dao.get(id);
        int itemsDeleted = dao.delete(item);
        assertEquals(1, itemsDeleted);
        assertNull(dao.get(id));
    }
}
