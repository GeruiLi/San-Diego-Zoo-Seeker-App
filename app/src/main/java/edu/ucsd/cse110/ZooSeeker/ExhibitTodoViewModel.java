package edu.ucsd.cse110.ZooSeeker;


import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ExhibitTodoViewModel extends AndroidViewModel {
    private LiveData<List<ExhibitListItem>> exhibitListItems;
    private final ExhibitListItemDao exhibitListItemDao;


    public ExhibitTodoViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        ExhibitTodoDatabase db = ExhibitTodoDatabase.getSingleton(context);
        exhibitListItemDao = db.exhibitListItemDao();
    }

    public LiveData<List<ExhibitListItem>> getTodoListItems() {
        if (exhibitListItems == null) {
            loadUsers();
        }

        return exhibitListItems;
    }

    private void loadUsers() {
        exhibitListItems = exhibitListItemDao.getAllLive();
    }

    public void toggleSelected(ExhibitListItem exhibitListItem) {
        exhibitListItem.selected = !exhibitListItem.selected;
        exhibitListItemDao.update(exhibitListItem);
    }

    public void updateText(ExhibitListItem exhibitListItem, String newText) {
        exhibitListItem.exhibitName = newText;
        exhibitListItemDao.update(exhibitListItem);
    }

    public void createTodo(String text) {
        int endOfListOrder = exhibitListItemDao.getOrderForAppend2();
        //int endOfListOrder = todoListItemDao.getOrderForAppend();
        ExhibitListItem newItem = new ExhibitListItem(text,false, endOfListOrder);
        exhibitListItemDao.insert(newItem);
    }

    public void setDeleted(ExhibitListItem exhibitListItem){
        exhibitListItemDao.delete(exhibitListItem);
    }
}