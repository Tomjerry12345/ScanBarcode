package com.tomjerry.scanbarcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tomjerry.scanbarcode.adapter.MyAdapter;
import com.tomjerry.scanbarcode.db.DbHelper;
import com.tomjerry.scanbarcode.model.ListItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerview;

    ArrayList<ListItem> arrayList;
    DbHelper helper;

    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerview = findViewById(R.id.recylerview);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        helper = new DbHelper(this);

        arrayList = helper.getAllInformation();

        if (arrayList.size() > 0) {

            myAdapter = new MyAdapter(arrayList , this);
            recyclerview.setAdapter(myAdapter);

        } else {
            Toast.makeText(this , "No Data Found" , Toast.LENGTH_LONG).show();
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return 0;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                Toast.makeText(getApplicationContext(), "Di geser" , Toast.LENGTH_LONG).show();

                final int position = viewHolder.getAdapterPosition();
                ListItem listItem = arrayList.get(position);

                helper.deleteRow(listItem.getId());

                arrayList.remove(position);
                myAdapter.notifyItemRemoved(position);
                myAdapter.notifyItemRangeChanged(position , arrayList.size());

            }
        }).attachToRecyclerView(recyclerview);

        final IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setCameraId(0);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentIntegrator.initiateScan();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode , resultCode , data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this , "No Result Found" , Toast.LENGTH_LONG).show();
            } else {
                boolean isInserted = helper.insertData(result.getFormatName() , result.getContents());

                if (isInserted) {
                    arrayList.clear();
                    arrayList = helper.getAllInformation();
                    myAdapter = new MyAdapter(arrayList , this);
                    recyclerview.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                }
            }
        } else {
            super.onActivityResult(requestCode ,resultCode ,data);
        }
    }
}
