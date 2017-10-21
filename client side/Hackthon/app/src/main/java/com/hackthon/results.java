package com.hackthon;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

/**
 * Created by cody on 2017/10/21.
 */
public class results extends Activity {

    //protected Typeface mTfLight;
    private PieChart chart;
    private BarChart barchart1;
    private BarChart barchart2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        //mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        chart = (PieChart) findViewById(R.id.piechart);
        barchart1 = (BarChart) findViewById(R.id.barchart1);
        barchart2 = (BarChart) findViewById(R.id.barchar2);
        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        barEntries1.add(new BarEntry(1,44f));
        barEntries1.add(new BarEntry(2,55f));
        barEntries1.add(new BarEntry(3,30f));
        barEntries1.add(new BarEntry(4,55f));
        barEntries1.add(new BarEntry(5,22f));
        barEntries2.add(new BarEntry(1,44f));
        barEntries2.add(new BarEntry(2,55f));
        barEntries2.add(new BarEntry(3,30f));
        barEntries2.add(new BarEntry(4,55f));
        barEntries2.add(new BarEntry(5,22f));
        BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Dates");
        BarDataSet barDataSet2 = new BarDataSet(barEntries2,"Dates");

        BarData theData1 = new BarData(barDataSet1);
        BarData theData2 = new BarData(barDataSet2);
        theData1.setBarWidth(0.9f);
        theData2.setBarWidth(0.9f);
        barchart1.setData(theData1);
        barchart2.setData(theData2);

        barchart1.setTouchEnabled(true);
        barchart1.setDragEnabled(true);
        barchart1.setScaleEnabled(true);
        barchart2.setTouchEnabled(true);
        barchart2.setDragEnabled(true);
        barchart2.setScaleEnabled(true);

        setpiedata(4,100);
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

    protected String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };



    private void setpiedata(int count, float range){
        float mult = range;
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for (int i = 0; i < count ; i++) {
            entries.add(new PieEntry((float) ((Math.random() * mult) + mult / 5),
                    mParties[i % mParties.length],
                    getResources().getDrawable(R.drawable.star)));
        }
        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);
 //       data.setValueTypeface(mTfLight);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);
        chart.setTouchEnabled(true);
        chart.setVerticalScrollBarEnabled(true);
        chart.invalidate();
    }


}
