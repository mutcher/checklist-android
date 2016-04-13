package home.checklist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainListActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayAdapter<String> _adapter = null;

    private void updateList() {
        _adapter.clear();
        GetListTask task = new GetListTask();
        task.execute(0);
        try {
            ArrayList<String> items = task.get(2, TimeUnit.SECONDS);
            if (items != null) {
                _adapter.addAll(items);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void onAddItemClick(View view) {
        AddListTask task = new AddListTask((byte)0x00);
        EditText itemNameEdit = (EditText)findViewById(R.id.itemNameEdit);
        String text = itemNameEdit.getText().toString();
        task.execute(text);
        try {
            task.get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        updateList();
        itemNameEdit.setText("");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.mainListView) {
            menu.add("Remove item").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    ListView lw = (ListView) findViewById(R.id.mainListView);
                    AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
                    String item = _adapter.getItem(acmi.position);

                    RemoveListTask removeListTask = new RemoveListTask();
                    removeListTask.setListId(0);
                    removeListTask.execute(item);

                    try {
                        boolean ret = removeListTask.get(5, TimeUnit.SECONDS);
                        updateList();
                        return ret;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            });
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Button button = (Button)findViewById(R.id.addItemButton);
        button.setText("Add List");
        button.setOnClickListener(this);
        ListView lw = (ListView)findViewById(R.id.mainListView);
        registerForContextMenu(lw);
        _adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        lw.setAdapter(_adapter);
        updateList();
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), SubListActivity.class);
                intent.putExtra("SelectedItem", i + 1);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.addItemButton:
                onAddItemClick(view);
                break;
        }
    }
}
