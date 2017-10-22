package com.hackthon;

/**
 * Created by cody on 20/10/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.hackthon.utils.ProgressGenerator;


import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;


public class Submit extends Activity implements ProgressGenerator.OnCompleteListener {

    public static final String EXTRAS_ENDLESS_MODE = null ;
    private ActionProcessButton btnSbt;
    private EditText usernameedittext;
    EditText msgTextField;
    private String mUserName = "";
    private String mServerIP = "192.168.1.155";
    private String mServerPort = "44444";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sign_in);

        KenBurnsView kbv = (KenBurnsView) findViewById(R.id.image);
        usernameedittext = (EditText) findViewById(R.id.editusername);
        //final EditText editPassword = (EditText) findViewById(R.id.editPassword);

        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        btnSbt = (ActionProcessButton)findViewById(R.id.btnSbt);
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
                btnSbt.setEnabled(true);
                usernameedittext.setEnabled(true);
                mUserName = usernameedittext.getText().toString();
                // send data thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SendRequest();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

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
        //Toast.makeText(this, R.string.Loading_Complete, Toast.LENGTH_LONG).show();
//        startresults();
//        sendrequest();
    }
    private void startresults(){
        Intent intent = new Intent(this,results.class);
        startActivity(intent);
        this.finish();
    }
    private void startsetting(){
        Intent intent = new Intent(this,setting.class);
        startActivity(intent);
    }
    private void starthistory(){
        Intent intent = new Intent(this,history.class);
        startActivity(intent);
    }


    private void SendRequest() throws Exception{
        //make json file
        JSONObject QueryInfo = new JSONObject();
        QueryInfo.put("username", mUserName);

        URL url = new URL("http://" + mServerIP + ":" + mServerPort);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            int length = QueryInfo.toString().getBytes().length;
            System.out.println("The length is " + length);
            System.out.println(length);
            urlConnection.setRequestProperty("Content-Length", Integer.toString(length + 4));
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(QueryInfo.toString().getBytes());
            out.flush();


            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String content = IOStreamProcessing.readStream(in);
            System.out.println("content len :" + content.length());
            System.out.println("content: " + content);
            if(content.length()<5) {
                System.out.println("wrong response");
                MainActivity.judgeprocess = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSbt.setProgress(-1);
                    }
                });
            }
            // up to here we got the data from server, loading should be end here and move to new activity
            else {
                MainActivity.judgeprocess = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSbt.setProgress(0);
                    }
                });
                startresults();
            }
        }

        finally {
            System.out.println("Connection terminated");
            urlConnection.disconnect();
        }

    }



}
