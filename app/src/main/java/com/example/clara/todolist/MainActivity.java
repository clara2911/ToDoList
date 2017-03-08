package com.example.clara.todolist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
    ListView lvItems;
    ContextMenu menu1;
    TodoCursorAdapter todoAdapter;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.deleteDatabase("TODOLISTDATABASE.db");
        inputText = (EditText) findViewById(R.id.inputText);
        menu1 = (ContextMenu) findViewById(R.id.menu1);

        // initialize db
        dbManager = new DBManager(this);
        dbManager.open();
        makeItemsAdapter();
        registerForContextMenu(lvItems);
        addExplanations();
    }

    public void addExplanations() {
        String expl1 = "Add an item on your to Do List below";
        String expl2 = "Long click on an item to Check off or delete";
        String expl3 = "Good luck!";
        if(!(cursor.moveToNext())) {
            dbManager.insert(expl1);
            dbManager.insert(expl2);
            dbManager.insert(expl3);
        }
        fetchCursor();
        todoAdapter.notifyDataSetChanged();
    }

    public void addItem(View view) {
        String itemToAdd = inputText.getText().toString();
        dbManager.insert(itemToAdd);
        fetchCursor();
        todoAdapter.notifyDataSetChanged();
        if (inputText.length() > 0) {
            inputText.getText().clear();
        }
    }

    public void deleteItem() {
        long itemid = cursor.getInt(cursor.getColumnIndex("_id"));
        dbManager.delete(itemid);
        fetchCursor();
        todoAdapter.notifyDataSetChanged();
    }

    public void checkItem() {
        String doneText = "DONE: "+cursor.getString(cursor.getColumnIndex("todosubject"));
        long id = cursor.getInt(cursor.getColumnIndex("_id"));
        dbManager.update(id,doneText);
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
        int menuItem = item.getItemId();
        switch (menuItem) {
            case R.id.check:
                checkItem();
                return true;
            case R.id.delete:
                deleteItem();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        inputText = (EditText) findViewById(R.id.inputText);

        registerForContextMenu(lvItems);
        menu1 = (ContextMenu) findViewById(R.id.menu1);
        makeItemsAdapter();
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
            String itemname = cursor.getString( cursor.getColumnIndex("todosubject"));
            textViewTitle.setText(itemname);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
    }




}
