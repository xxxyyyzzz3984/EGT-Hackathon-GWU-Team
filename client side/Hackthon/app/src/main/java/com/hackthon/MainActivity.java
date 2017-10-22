package com.hackthon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static boolean judgeprocess = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sign_in);
       // initView();
        startSubmit(true);
        this.finish();
    }
    private void startSubmit(boolean isEndlessMode) {
        Intent intent = new Intent(this, Submit.class);
        intent.putExtra(Submit.EXTRAS_ENDLESS_MODE, isEndlessMode);
        startActivity(intent);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.mymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.history:
                Toast.makeText(this,"history clicked",Toast.LENGTH_SHORT).show();
                starthistory();
                break;
            case R.id.setting:
                Toast.makeText(this,"setting clecked",Toast.LENGTH_SHORT).show();
                startsetting();
                break;
        }
        return true;
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

    private void startsetting(){
        Intent intent = new Intent(this,setting.class);
        startActivity(intent);
    }

    private void starthistory(){
        Intent intent = new Intent(this,history.class);
        startActivity(intent);
    }
*/
}
