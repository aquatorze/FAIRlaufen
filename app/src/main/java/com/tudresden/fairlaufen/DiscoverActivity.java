package com.tudresden.fairlaufen;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class DiscoverActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    boolean[] categoryArray = new boolean[6];
    Context context;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        context = this;
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPump.getData(this);
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(context, DescriptionActivity.class);
                String intentName = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);
                intent.putExtra("name",intentName);
                startActivity(intent);
                return true;
            }
        });


    }

    public void onClickCheckbox(View view){
        boolean checked = ((CheckBox) view).isChecked();
        if(checked){
            categoryArray[view.getId()] = true;
        }
        else{
            categoryArray[view.getId()] = false;
        }
    }


    public void onClickToMap(View view){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("category", categoryArray);
        startActivity(intent);
    }

}