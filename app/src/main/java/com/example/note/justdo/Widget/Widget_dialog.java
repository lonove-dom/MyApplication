package com.example.note.justdo.Widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.note.justdo.Event;
import com.example.note.justdo.Eventdaomanger;
import com.example.note.justdo.R;

public class Widget_dialog extends Activity {
    EditText newEditText;
    Button tick;
    InputMethodManager im;//此处用于管理软键盘
    protected void onCreate(Bundle savedInstanceState) {
        //  getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_add_dialog);
        im = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        newEditText = (EditText)findViewById(R.id.wg_edittext);
        im.showSoftInput(newEditText,0);
        tick=(Button)findViewById(R.id.wg_tick);
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=newEditText.getText().toString();
                if(!content.isEmpty()) {
                    addEvent(content);
                }
                final AppWidgetManager mgr= AppWidgetManager.getInstance(Widget_dialog.this);
                final ComponentName cn = new ComponentName(Widget_dialog.this,
                        WidgetProvider.class);
                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                        R.id.wg_listview);
                onDestroy();
            }
        });

    }
    private void addEvent(String string){
        Event event=new Event(1,string);
        Eventdaomanger manger=new Eventdaomanger(Widget_dialog.this);
        manger.insertevent(event);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
