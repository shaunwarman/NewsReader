package com.example.newsreader;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener{
	Spinner spinner;
	private TextToSpeech tts;
	Button btnSpeak;
	ArrayList<String> articleArray = new ArrayList<String>();
	ArrayList<String> urlArray = new ArrayList<String>();
	ArrayList<String> tempArray = new ArrayList<String>();
	ArrayList<String> summaryArray = new ArrayList<String>();
	MyListView lv;
	ArrayAdapter<String> lvAdapter;
	String itemUrl = null;
	boolean isReading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.init();

		tts = new TextToSpeech(this, this);

		startWebServiceTask(getSectionFromDropDown());
	}

	public String getSectionFromDropDown() {
		String newsSection = null;

		Spinner mySpinner = (Spinner)findViewById(R.id.name);
		String section = mySpinner.getSelectedItem().toString();

		if(section.equals("World")) {
			newsSection = "http://api.feedzilla.com/v1/categories/19/articles.atom?count=10";
		} else if(section.equals("United States")) {
			newsSection = "http://api.feedzilla.com/v1/categories/7/articles.atom?count=10";
		} else if(section.equals("Business")) {
			newsSection = "http://api.feedzilla.com/v1/categories/22/articles.atom?count=10";
		} else if(section.equals("Technology")) {
			newsSection = "http://api.feedzilla.com/v1/categories/30/articles.atom?count=10";
		} else if(section.equals("Entertainment")) {
			newsSection = "http://api.feedzilla.com/v1/categories/6/articles.atom?count=10";
		} else if(section.equals("Sports")) {
			newsSection = "http://api.feedzilla.com/v1/categories/27/articles.atom?count=10";
		} else if(section.equals("Science")) {
			newsSection = "http://api.feedzilla.com/v1/categories/8/articles.atom?count=10";
		} else if(section.equals("Health")) {
			newsSection = "http://api.feedzilla.com/v1/categories/11/articles.atom?count=10";
		} else {
			newsSection = "http://api.feedzilla.com/v1/categories/19/articles.atom?count=10";
		}

		return newsSection;
	}

	public void setAlarm(View view) {
		Intent intent = new Intent(this, SetAlarmActivity.class);
		startActivity(intent);
	}


	// create the user interface in the activity
	public void init() {
		spinner = (Spinner) findViewById(R.id.name);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.section_arrays, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		btnSpeak = (Button) findViewById(R.id.dates);
		btnSpeak.setTag(1);
		btnSpeak.setText("Read");
		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final int status =(Integer) v.getTag();
				if(status == 1) {
					isReading = true;
					speakOut();
				    btnSpeak.setText("Stop");
				    v.setTag(0); //pause
				} else {
					isReading = false;
					tts.stop();
				    btnSpeak.setText("Read");
				    v.setTag(1); //read
				} 
			}

		});
		lvAdapter = new ArrayAdapter<String>(this, 
				R.layout.article_item, R.id.label, articleArray);
		lv = (MyListView) findViewById(R.id.list);
		lv.setAdapter(lvAdapter);
		lv.setOnItemClickListener(mMessageClickedHandler);

		spinner.setOnItemSelectedListener(
				new OnItemSelectedListener(){
					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						startWebServiceTask(getSectionFromDropDown());
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				}
				);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				btnSpeak.setEnabled(true);
				//	speakOut();
			}
		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}

	private void speakOut() {
		//	String textView = tv.getText().toString();
		//	tts.speak(textView, TextToSpeech.QUEUE_FLUSH, null);
	}

	private void startWebServiceTask(String url) {
		WebServiceAsyncTask webServiceTask = new WebServiceAsyncTask();
		webServiceTask.execute(url);
	}

	private OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			if(!isReading) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			Log.i("SETTING WEBPAGE", "GETTING POSITION");
			i.setData(Uri.parse(urlArray.get(position)));
			startActivity(i);
			} else {
				String summary = summaryArray.get(position);
				tts.speak(summary, TextToSpeech.QUEUE_FLUSH, null);
			}
		}
	};


	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}



	class WebServiceAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {
		protected ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(MainActivity.this, "", 
                    "Loading. Please wait...", true);
		}
		
		private void clearArrays() {
			tempArray.clear();
			urlArray.clear();
			summaryArray.clear();
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			Log.i("Adapter Count before clear is ", String.valueOf(lvAdapter.getCount()));

			clearArrays();

			String news = grabNews(params[0]);
			List<NewsArticle> list = parseHTML(news);
			StringBuilder sb = new StringBuilder();
			for(NewsArticle na : list) {
				
				tempArray.add(na.getTitle());
				urlArray.add(na.getSource());
				summaryArray.add(na.getSummary());
			}
			return tempArray;
		}


		@Override
		protected void onPostExecute(ArrayList<String> result) {
			articleArray.clear();
			articleArray.addAll(result);
			lvAdapter.notifyDataSetChanged();
			dialog.hide();
		}

		// grab the news specified by the news section in the drop down list
		public String grabNews(String url) {
			String news = null;

			//			Log.i("Grab news method", "Grabbing news method entered " + url);
			try {
				URL feedzillaURL = new URL(url);
				HttpURLConnection con = (HttpURLConnection) feedzillaURL.openConnection();
				InputStream in = new BufferedInputStream(con.getInputStream());
				news = readStream(in);
				//				Log.i("HTML", news);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return news;
		}

		public List<NewsArticle> parseHTML(String html) {
			String title = null;
			String summary = null;
			String source = null;
			String current = null;
			List<NewsArticle> articles = new LinkedList<NewsArticle>();
			NewsArticle pa;

			for(int a = 0; a <= 10; a++) {
				pa = new NewsArticle();
				if(html.indexOf("<title type=\"html\"") == -1) {
					break;
				}

				html = html.substring(html.indexOf("<title type=\"html\""));
				current = html.substring(html.indexOf("title type=\"html\"")+18, html.indexOf("</title>"));
				title = current;
				pa.setTitle(title);
				for(int b = 0; b <1; b++) {
					html = html.substring(html.indexOf("<summary type=\"html\""));
					current = html.substring(html.indexOf("summary type=\"html\"")+20, html.indexOf("&lt;"));
					summary = current;
					pa.setSummary(summary);
					for(int c = 0; c<1;c++) {
						html = html.substring(html.indexOf("<link rel=\"alternate\""));
						current = html.substring(html.indexOf("<link rel=\"alternate\" href=\"")+28, html.indexOf("\" />"));
						//						Log.i("SOURCE", current);
						source = current;
						pa.setSource(source);
						articles.add(pa);
					}
				}
			}
			return articles;
		}

		public String readStream(InputStream in) {
			BufferedReader reader = null;
			StringBuilder sb = new StringBuilder();
			try {
				reader = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return sb.toString();
		} 

	}
}
