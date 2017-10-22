package com.hackthon;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.hackthon.Submit.NegWordPercent;
import static com.hackthon.Submit.NegativePercent;
import static com.hackthon.Submit.PosWordPercent;
import static com.hackthon.Submit.PostivePercent;
import static com.hackthon.Submit.ProfileImgLink;
import static com.hackthon.Submit.TargetName;
import static com.hackthon.Submit.Top5NegWords;
import static com.hackthon.Submit.Top5PosWords;
import static com.hackthon.Submit.isCriminal;

/**
 * Created by cody on 2017/10/21.
 */
public class results extends Activity {

    //protected Typeface mTfLight;
    private PieChart chart;
    private PieChart chart1;
    private BarChart barchart1;
    private BarChart barchart2;
    private ImageView imageView;
    private TextView textView;
    private TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        imageView = (ImageView)findViewById(R.id.imageview);
        LoadImageFromUrl(ProfileImgLink);

        textView = (TextView)findViewById(R.id.textView2);
        textView.setText(TargetName);
        textView.setTextSize(30);

        //mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        chart = (PieChart) findViewById(R.id.piechart);
        chart1 = (PieChart) findViewById(R.id.piechart1);
        barchart1 = (BarChart) findViewById(R.id.barchart1);
        barchart2 = (BarChart) findViewById(R.id.barchar2);
        textView2 = (TextView)findViewById(R.id.textView3);


        textView2.setTextSize(30);
        if (isCriminal == 1){
            textView2.setTextColor(Color.RED);
        }
        else{
            textView2.setVisibility(View.INVISIBLE);
        }

        setpiedata();
        setbarchartdata();

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
                Toast.makeText(this,"setting clicked",Toast.LENGTH_SHORT).show();
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


    private void setpiedata(){
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        ArrayList<PieEntry> entries1 = new ArrayList<PieEntry>();


        entries.add(new PieEntry( PosWordPercent,
                "Positive words",
                getResources().getDrawable(R.drawable.star)));
        entries.add(new PieEntry( NegWordPercent,
                "Negative words",
                getResources().getDrawable(R.drawable.star)));

        entries1.add(new PieEntry( PostivePercent,
                "Positive Percent"));
        entries1.add(new PieEntry( NegativePercent,
                "Negative Percent"));

        PieDataSet dataSet = new PieDataSet(entries, "sentiment");
        PieDataSet dataSet1 = new PieDataSet(entries1, "sentiment");


        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        dataSet1.setSliceSpace(3f);
        dataSet1.setIconsOffset(new MPPointF(0, 40));
        dataSet1.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        //for (int c : ColorTemplate.VORDIPLOM_COLORS)
        //    colors.add(c);

        //for (int c : ColorTemplate.JOYFUL_COLORS)
         //   colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        dataSet1.setColors(colors);

        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(16f);
        data.setValueTextColor(Color.BLACK);

        PieData data1 = new PieData(dataSet1);
        data1.setValueFormatter(new PercentFormatter());
        data1.setValueTextSize(16f);
        data1.setValueTextColor(Color.BLACK);
 //       data.setValueTypeface(mTfLight);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);
        chart.setTouchEnabled(true);
        chart.setVerticalScrollBarEnabled(true);
        chart.invalidate();

        chart1.setData(data1);

        // undo all highlights
        chart1.highlightValues(null);
        chart1.setTouchEnabled(true);
        chart1.setVerticalScrollBarEnabled(true);
        chart1.invalidate();
    }

    private void setbarchartdata(){
        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        Set<String> posbarchartvalue1 = Top5PosWords.keySet();
        Set<String> posbarchartvalue2 = Top5NegWords.keySet();
        String[] words1 = new String[posbarchartvalue1.size()];
        String[] words2 = new String[posbarchartvalue2.size()];
        int index = 1;
        for (String word: posbarchartvalue1){
            Integer number = Top5PosWords.get(word);
            barEntries1.add(new BarEntry(index, number));
            words1[index-1] = word;
            index++;
        }

        int indes = 1;
        for (String word: posbarchartvalue2){
            Integer number = Top5NegWords.get(word);
            barEntries2.add(new BarEntry(indes, number));
            words2[indes-1] = word;
            indes++;
        }
//        barEntries1.add(new BarEntry(1, 22f));
//        barEntries1.add(new BarEntry(2, 11f));
//        barEntries1.add(new BarEntry(3, 22f));
//        barEntries1.add(new BarEntry(4, 33f));
//        barEntries1.add(new BarEntry(5, 22f));




        BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Words");
        BarDataSet barDataSet2 = new BarDataSet(barEntries2,"Dates");

        BarData theData1 = new BarData(barDataSet1);
        BarData theData2 = new BarData(barDataSet2);

        theData2.setBarWidth(0.9f);
        barchart1.setData(theData1);
        barchart2.setData(theData2);
        theData1.setBarWidth(0.9f);

        XAxis xAxis = barchart1.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(words1));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(0);

        XAxis xAxis2 = barchart2.getXAxis();
        xAxis2.setDrawLabels(true);
        xAxis2.setValueFormatter(new IndexAxisValueFormatter(words2));
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setGranularity(1);
        xAxis2.setCenterAxisLabels(true);
        xAxis2.setAxisMinimum(0);


        barchart1.setTouchEnabled(true);
        barchart1.setDragEnabled(true);
        barchart1.setScaleEnabled(true);
        barchart2.setTouchEnabled(true);
        barchart2.setDragEnabled(true);
        barchart2.setScaleEnabled(true);
    }

//    public class MyXAxisValueFormatter implements IAxisValueFormatter{
//
//        private String[] mValues;
//        public MyXAxisValueFormatter(String[] values){
//            this.mValues = values;
//        }
//
//        @Override
//        public String getFormattedValue(float value, AxisBase axis) {
//            System.out.println("value:"+ value);
//            if (value<5){
//                return mValues[(int)value];
//            }
//            else {
//                return "";
//            }
//        }
//    }
    private void LoadImageFromUrl(String url){
        Picasso.with(this).load(url).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });

    }


}
