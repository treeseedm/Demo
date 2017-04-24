package com.csja.smlocked;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PhoneListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_phone_list);

        ListView listView = (ListView) findViewById(R.id.list);
        String[] phone = new String[]{"1820189872673", "18909383474"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, phone);
        listView.setAdapter(arrayAdapter);
    }
}
