package com.example.note.justdo.Widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.note.justdo.Event;
import com.example.note.justdo.Eventdaomanger;
import com.example.note.justdo.R;

import static com.example.note.justdo.Eventdaomanger.context;

public class Widget_dialog extends Activity {
    EditText newEditText;
    Button tick;
    protected void onCreate(Bundle savedInstanceState) {
        //  getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_add_dialog);
        final EditText newEditText = (EditText)findViewById(R.id.wg_edittext);
        Button tick=(Button)findViewById(R.id.wg_tick);
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=newEditText.getText().toString();
                addEvent(content);
                onBackPressed();
            }
        });

    }
    private void addEvent(String string){
        Event event=new Event(1,string);
        Eventdaomanger manger=new Eventdaomanger(Widget_dialog.this);
        manger.insertevent(event);
    }

}
