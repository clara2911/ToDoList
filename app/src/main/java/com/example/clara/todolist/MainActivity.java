package com.example.clara.todolist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.clara.todolist.R.id.todolistItems;

/*
TO DO:
- vragen: is dat oranje streepje 'should be @String resource erg?
- Vragen: waar kijken jullie naar bij code-mooiheid?
- Vragen: moeten de class variables 'private' ervoor of niet?
- Vragen: wat is dat '@Override'?
- Vragen: moeten alle methods public?
 */

public class MainActivity extends AppCompatActivity {
    private DBManager dbManager;
    EditText inputText;
    ArrayList<String> itemsArray;
    ArrayList<Long> itemsidArray;
    ListView lvItems;
    ContextMenu menu1;
    TodoCursorAdapter todoAdapter;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.deleteDatabase("TODOLISTDATABASE.db");

        itemsArray = new ArrayList<>();
        itemsidArray = new ArrayList<>();
        inputText = (EditText) findViewById(R.id.inputText);
        menu1 = (ContextMenu) findViewById(R.id.menu1);



        // initialize db
        dbManager = new DBManager(this);
        dbManager.open();
        makeItemsAdapter();
        registerForContextMenu(lvItems);
        String itemname = cursor.getString(cursor.getColumnIndex("todosubject"));
        itemsArray.add(itemname);
        Log.d("idnext", itemname);
        while(cursor.moveToNext()) {
            String itemname2 = cursor.getString(cursor.getColumnIndex("todosubject"));
            long itemid = cursor.getInt(cursor.getColumnIndex("_id"));
            itemsArray.add(itemname2);
            itemsidArray.add(itemid);
        }
    }

    public void addExplanations() {
        String expl1 = "Add an item on your to Do List below";
        String expl2 = "Long click on an item to Check off or delete";
        String expl3 = "Good luck!";
        itemsArray.add(expl1);
        itemsArray.add(expl2);
        itemsArray.add(expl3);
        long id1 = dbManager.insert(expl1);
        long id2 = dbManager.insert(expl2);
        long id3 = dbManager.insert(expl3);
        itemsidArray.add(id1);
        itemsidArray.add(id2);
        itemsidArray.add(id3);
        fetchCursor();
        todoAdapter.notifyDataSetChanged();
    }

    public void addItem(View view) {
        String itemToAdd = inputText.getText().toString();
        itemsArray.add(itemToAdd);
        long id = dbManager.insert(itemToAdd);
        itemsidArray.add(id);
        fetchCursor();
        todoAdapter.notifyDataSetChanged();
        if (inputText.length() > 0) {
            inputText.getText().clear();
        }
    }

    public void deleteItem(int pos) {
        Object itemToDelete = itemsArray.get(pos);
        long itemid = itemsidArray.get(pos);
        itemsArray.remove(itemToDelete);
        itemsidArray.remove(itemid);
        dbManager.delete(itemid);
        fetchCursor();
        todoAdapter.notifyDataSetChanged();
    }

    public void checkItem(int pos) {
        String doneText = "DONE: "+itemsArray.get(pos);
        itemsArray.set(pos, doneText);
        dbManager.update(itemsidArray.get(pos),doneText);
        fetchCursor();
        todoAdapter.notifyDataSetChanged();
    }

    public void makeItemsAdapter() {
        cursor = dbManager.fetch();
        lvItems = (ListView) findViewById(todolistItems);
        todoAdapter = new TodoCursorAdapter(this,cursor);
        assert lvItems != null;
        lvItems.setAdapter(todoAdapter);
        todoAdapter.notifyDataSetChanged();
    }

    public void fetchCursor() {
        cursor = dbManager.fetch();
        todoAdapter.changeCursor(cursor);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listItem = (int) info.id;
        int menuItem = item.getItemId();
        switch (menuItem) {
            case R.id.check:
                checkItem(listItem);
                return true;
            case R.id.delete:
                deleteItem(listItem);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("itemsArray",itemsArray);
        for(int i=0; i<itemsidArray.size(); i++) {
            outState.putLong("itemsid"+i,itemsidArray.get(i));
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        inputText = (EditText) findViewById(R.id.inputText);

        registerForContextMenu(lvItems);
        menu1 = (ContextMenu) findViewById(R.id.menu1);

        itemsArray = inState.getStringArrayList("itemsArray");
        makeItemsAdapter();

        itemsidArray = new ArrayList<>();
        int i=0;
        while(true) {
            Long newId = inState.getLong("itemsid"+i);
            long defaultVal = (long) 0;
            if(newId != defaultVal) {
                itemsidArray.add(newId);
            } else {
                break;
            }
            i=i+1;
        }
        fetchCursor();
        todoAdapter.notifyDataSetChanged();
    }


    public void onDestroy() {
        super.onDestroy();
        if(dbManager != null) {
            dbManager.close();
        }
    }

    public class TodoCursorAdapter extends CursorAdapter {
        public TodoCursorAdapter(Context context, Cursor cursor) {
            super(context,cursor,0);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            dbManager.open();
            TextView textViewTitle = (TextView) view.findViewById(R.id.listitem);
            String itemname = cursor.getString( cursor.getColumnIndex(dbManager.dbHelper.TODOSUBJECT) );
            textViewTitle.setText(itemname);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
    }




}
