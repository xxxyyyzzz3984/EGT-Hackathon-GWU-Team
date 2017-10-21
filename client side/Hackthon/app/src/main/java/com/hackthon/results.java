package com.hackthon;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by cody on 2017/10/21.
 */
public class results extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
    }
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
    private void startsetting(){
        Intent intent = new Intent(this,setting.class);
        startActivity(intent);
    }
    private void starthistory(){
        Intent intent = new Intent(this,history.class);
        startActivity(intent);
    }




}
