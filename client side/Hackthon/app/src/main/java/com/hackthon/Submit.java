package com.hackthon;

/**
 * Created by cody on 20/10/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.hackthon.utils.ProgressGenerator;




public class Submit extends Activity implements ProgressGenerator.OnCompleteListener {

    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sign_in);

        final EditText editEmail = (EditText) findViewById(R.id.editEmail);
        //final EditText editPassword = (EditText) findViewById(R.id.editPassword);

        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final ActionProcessButton btnSbt = (ActionProcessButton) findViewById(R.id.btnSbt);
        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.getBoolean(EXTRAS_ENDLESS_MODE)) {
            btnSbt.setMode(ActionProcessButton.Mode.ENDLESS);
        } else {
            btnSbt.setMode(ActionProcessButton.Mode.PROGRESS);
        }
        btnSbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressGenerator.start(btnSbt);
                btnSbt.setEnabled(false);
                editEmail.setEnabled(false);
                starthistory();
            }
        });
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

    @Override
    public void onComplete() {
        Toast.makeText(this, R.string.Loading_Complete, Toast.LENGTH_LONG).show();
    }
    private void startresults(){
        Intent intent = new Intent(this,results.class);
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

}
