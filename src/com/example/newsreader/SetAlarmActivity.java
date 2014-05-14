package com.example.newsreader;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SetAlarmActivity extends Activity{
	ArrayList<String> alarmItemArray = new ArrayList<String>();
	MyListView lv;
	ArrayAdapter<String> alarmAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_alarm);
		
		this.init();
		
	}
	
	public void init() {
		alarmAdapter = new ArrayAdapter<String>(this, 
				R.layout.activity_alarm_item, R.id.time, alarmItemArray);
		lv = (MyListView) findViewById(R.id.listview);
		lv.setAdapter(alarmAdapter);
		alarmItemArray.add("Hello");
		alarmAdapter.notifyDataSetChanged();
	}
	
	public void showAlarm(View view) {
		Intent intent = new Intent(this, AlarmActivity.class);
		startActivity(intent);
	}
}
