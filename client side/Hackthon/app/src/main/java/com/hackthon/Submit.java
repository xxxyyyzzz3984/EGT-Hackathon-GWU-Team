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
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.hackthon.utils.ProgressGenerator;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class Submit extends Activity implements ProgressGenerator.OnCompleteListener {

    public static final String EXTRAS_ENDLESS_MODE = null ;
    private ActionProcessButton btnSbt;
    private EditText usernameedittext;
    private String mUserName = "";
    private String mServerIP = "192.168.1.155";
    private String mServerPort = "44444";
    public static String TargetName = "";
    public static String ProfileImgLink = "";
    public static int NumPosWords;
    public static int NumNegWords;
    public static float PosWordPercent;
    public static float NegWordPercent;
    public static HashMap<String, Integer> Top5PosWords;
    public static HashMap<String, Integer> Top5NegWords;
    public static float PostivePercent;
    public static float NegativePercent;
    public static int isCriminal;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sign_in);

        Top5PosWords = new HashMap<>();
        Top5NegWords = new HashMap<>();

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
                            startresults();
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

            else {
                MainActivity.judgeprocess = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSbt.setProgress(0);
                    }
                });
                System.out.println(content);
                parse_response(content);
                startresults();
            }
        }

        finally {
            System.out.println("Connection terminated");
            urlConnection.disconnect();
        }

    }

    public void parse_response(String content) throws JSONException {
//        String content = "{\"target_name\"=\"Little Donalad\", \"bio\": \"See the whole picture with @ABC News. Facebook: " +
//                "https://www.facebook.com/abcnews\\u00a0 Instagram: https://www.instagram.com/abcnews\\u00a0\"," +
//                " \"poswords_percent\": 0.4, \"pos_percentage\": 0.8920354843139648, \"total_negwords\": 12," +
//                " \"total_poswords\": 8, \"top5poswords\": {\"unity\": 1, \"work\": 1, \"relief\": 2, \"optimism\": 1," +
//                " \"trump\": 1}, \"top5negwords\": {\"warned\": 1, \"discourage\": 1, \"uncertain\": 1, \"killed\": 2, \"myth\": 1}, " +
//                "\"location\": \"NewYorkCity/Worldwide\", \"negative_percentage\": 0.10796445608139038, \"negwords_percent\": 0.6, " +
//                "\"profile_image_link\": \"https://pbs.twimg.com/profile_images/877547979363758080/ny06RNTT_400x400.jpg\"," +
//                " \"criminal_alert\": 1}";

        JSONObject jObject = new JSONObject(content);
        TargetName = jObject.getString("target_name");
        ProfileImgLink = jObject.getString("profile_image_link");
        NumPosWords = jObject.getInt("total_poswords");
        NumNegWords = jObject.getInt("total_negwords");
        NegWordPercent = Float.parseFloat(jObject.getString("negwords_percent")) * 100;
        PosWordPercent = Float.parseFloat(jObject.getString("poswords_percent")) * 100;

        JSONObject Poswords_jsobj = jObject.getJSONObject("top5poswords");
        JSONObject Negwords_jsobj = jObject.getJSONObject("top5negwords");

        
        for(int i = 0; i<Poswords_jsobj.names().length(); i++){
            Poswords_jsobj.get(Poswords_jsobj.names().getString(i));
            Top5PosWords.put(Poswords_jsobj.names().getString(i), Poswords_jsobj.getInt(Poswords_jsobj.names().getString(i)));
        }

        for(int i = 0; i<Negwords_jsobj.names().length(); i++){
            Negwords_jsobj.get(Negwords_jsobj.names().getString(i));
            Top5NegWords.put(Negwords_jsobj.names().getString(i), Negwords_jsobj.getInt(Negwords_jsobj.names().getString(i)));
        }

        PostivePercent = Float.parseFloat(jObject.getString("pos_percentage")) * 100;
        NegativePercent = Float.parseFloat(jObject.getString("negative_percentage")) * 100;
        isCriminal = jObject.getInt("criminal_alert");
    }
}
