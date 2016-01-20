package com.sweetsound.piechart.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.sweetsound.piechart.PieChartImageView;
import com.sweetsound.piechart.R;
import com.sweetsound.piechart.PieChartImageView.SHOW_TEXT_TYPE;
import com.sweetsound.piechart.listener.OnPieClickListener;

public class SamplePieChartActivity extends Activity {
    private static final String TAG = SamplePieChartActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	setContentView(R.layout.sample_pie_chart_activity);
	
	// 100%를 기준으로 만든 값
	final float data_values[] = {10, 20, 30, 40};
//	final float data_values[] = {40, 60};
	int color_values[] = {Color.MAGENTA, Color.RED, Color.GREEN,Color.BLUE,Color.YELLOW,Color.CYAN};
	
	// 각도(Angle)를 기준으로 만든 값
//	final float data_values[] = {100, 200, 60};
//	int color_values[] = {Color.RED, Color.YELLOW, Color.BLUE};
	
	PieChartImageView imgView = (PieChartImageView) findViewById(R.id.image_placeholder);
	
	// 파이 차트를 설정하여 ImageView에 넣는다.
	imgView.setShowTextType(SHOW_TEXT_TYPE.OVERLAP);
	imgView.setPieInfo(color_values, data_values, true);
	imgView.setOnPieClickListener(new OnPieClickListener() {
	    @Override
	    public void onPieClick(int pieIndex, boolean isSelected) {
		switch(pieIndex) {
		case 0:
		    Log.e(TAG, pieIndex + " selected : " + isSelected);
		    break;
		case 1:
		    Log.e(TAG, pieIndex + " selected : " + isSelected);
		    break;
		case 2:
		    Log.e(TAG, pieIndex + " selected : " + isSelected);
		    break;
		}
		
		Toast.makeText(getApplicationContext(), "Pie Size : " + data_values[pieIndex], Toast.LENGTH_SHORT).show();
	    }
	});
	
	imgView.setSelectPid(1);
    }
}
