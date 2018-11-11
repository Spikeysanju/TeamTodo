package com.spikeysanju98gmail.teamtodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView task_rv;
    private DatabaseReference taskDB;
    FirebaseRecyclerAdapter TaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        task_rv = (RecyclerView)findViewById(R.id.task_RV);

        taskDB = FirebaseDatabase.getInstance().getReference().child("Tasks");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        task_rv.setLayoutManager(linearLayoutManager);

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        task_rv.setNestedScrollingEnabled(false);


        loadAllTask();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(MainActivity.this,AddTask.class));
            }
        });
    }

    private void loadAllTask() {

        TaskAdapter = new FirebaseRecyclerAdapter<Task,TaskViewHolder>(

                Task.class,
                R.layout.task_rv_layout,
                TaskViewHolder.class,
                taskDB
        ) {
            @Override
            protected void populateViewHolder(TaskViewHolder viewHolder, Task model, int position) {


                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(MainActivity.this,model.getImage());

            }

        };

        TaskAdapter.notifyDataSetChanged();
        task_rv.setAdapter(TaskAdapter);


    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        View mView;
        public TaskViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mView.setOnCreateContextMenuListener(this);

        }

        public void setTitle(String title){

            TextView titleTV = (TextView)mView.findViewById(R.id.title);
            titleTV.setText(title);
        }
        public void setDescription(String description){

            TextView descriptionTV = (TextView)mView.findViewById(R.id.description);
            descriptionTV.setText(description);
        }
        public void setImage(Context ctx, String image){

            ImageView momImg = (ImageView) mView.findViewById(R.id.image);
            momImg.setClipToOutline(true);
            Picasso.with(ctx).load(image).into(momImg);

        }
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {


            menu.setHeaderTitle("Select Action");
            menu.add(0, 0, getAdapterPosition(), "DELETE");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals("DELETE")) {

            deleteItem(TaskAdapter.getRef(item.getOrder()).getKey());
            TaskAdapter.notifyDataSetChanged();

        }

        return super.onContextItemSelected(item);


    }

    private void deleteItem(String key) {
        taskDB.child(key).removeValue();
        TaskAdapter.notifyDataSetChanged();
    }

}
