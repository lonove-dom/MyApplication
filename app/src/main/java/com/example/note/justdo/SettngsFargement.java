package com.example.note.justdo;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

public class SettngsFargement extends PreferenceFragment {
//    private ListPreference language;
//    private ListPreference textsize;
//    private SwitchPreference nightMode;
//    private Preference connect;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
