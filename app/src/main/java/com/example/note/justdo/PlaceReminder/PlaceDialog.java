package com.example.note.justdo.PlaceReminder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.note.justdo.R;
//时间提醒dialog
public class PlaceDialog extends Activity implements View.OnClickListener {
public Button Sure;
public TextView content;
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
               setContentView(R.layout.place_dialog);
               Sure=findViewById(R.id.Sure);
               content=findViewById(R.id.content);
               Sure.setOnClickListener(this);
        }

           @Override
     public void onClick(View v) {
            switch (v.getId()) {
                  case R.id.Sure:

                          break;
//               case R.id.button2_stop_service:
//                        Intent stopIntent = new Intent(this, MyService.class);
//                      stopService(stopIntent);
//                         break;
//              default:
//                       break;
           }
             }
    }
