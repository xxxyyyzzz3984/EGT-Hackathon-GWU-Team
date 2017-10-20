package com.hackthon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        Button submitbutton = (Button)findViewById(R.id.btnsearch);
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSubmit(true);
            }
        });
    }


    private void startSubmit(boolean isEndlessMode) {
        Intent intent = new Intent(this, Submit.class);
        intent.putExtra(Submit.EXTRAS_ENDLESS_MODE, isEndlessMode);
        startActivity(intent);
    }

}
