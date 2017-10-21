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


import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;


public class Submit extends Activity implements ProgressGenerator.OnCompleteListener {

    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";
    private EditText usernameedittext;
    EditText msgTextField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sign_in);

        usernameedittext = (EditText) findViewById(R.id.editusername);
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
                usernameedittext.setEnabled(false);

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
        startresults();
        sendrequest();
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

    protected void sendrequest(){
        String msg = usernameedittext.getText().toString();
        String urlString = "http://192.168.1.155:44444";
        String data ="{\"username\":\""+msg+"\"}";
        OutputStream out = null;
        if(msg.length()>0) {

           // HttpClient httpclient = new DefaultHttpClient();
            //HttpPost httppost = new HttpPost("http://192.168.1.155:4444");
            try {

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                out.close();
                urlConnection.connect();
                Toast.makeText(getBaseContext(),data,Toast.LENGTH_SHORT).show();
                System.out.println(data);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            //display message if text field is empty
            Toast.makeText(getBaseContext(),"All fields are required",Toast.LENGTH_SHORT).show();
        }

    }

}
