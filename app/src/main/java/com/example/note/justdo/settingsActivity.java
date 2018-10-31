package com.example.note.justdo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class settingsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.settings_test);

                //加载PrefFragment
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                SettngsFargement PF= new SettngsFargement();
                transaction.add(R.id.test,PF);
        transaction.commit();

    }
}
