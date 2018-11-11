package com.spikeysanju98gmail.teamtodo;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class TeamTodo extends Application{


    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
