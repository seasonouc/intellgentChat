package com.example.voice_rcd;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private Button mBtnSend;
	//private TextView mBtnRcd;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private RelativeLayout mBottom;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private boolean isShosrt = false;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
			voice_rcd_hint_tooshort;
	private ImageView img1, sc_img1;
	private SoundMeter mSensor;
	private View rcChat_popup;
	private LinearLayout del_re;
	private boolean btn_vocie = false;
	private int flag = 1;
	private Handler mHandler = new Handler();
	private String voiceName;
	private long startVoiceT, endVoiceT;
	
	private SQLiteDatabase mSQLiteDataBase;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		// ����activityʱ���Զ����������
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();

		initData();
		
		openDataBase();
	
	}

	public void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);

		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
		mBtnBack.setOnClickListener(this);
	

		mSensor = new SoundMeter();
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		

	}

	private String[] msgArray = new String[] { "11111","1111","111","11111 ","1111","1111"};

	private String[] dataArray = new String[] { "2012-10-31 18:00",
			"2012-10-31 18:10", "2012-10-31 18:11", "2012-10-31 18:20",
			"2012-10-31 18:30", "2012-10-31 18:35"};
	private final static int COUNT = 6;

	public void initData() {
		for (int i = 0; i < COUNT; i++) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(dataArray[i]);
			if (i % 2 == 0) {
				entity.setName("�׸���");
				entity.setMsgType(true);
			} else {
				entity.setName("�߸�˧");
				entity.setMsgType(false);
			}

			entity.setText(msgArray[i]);
			mDataArrays.add(entity);
		}

		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			finish();
			break;
		}
	}

	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(getDate());
			entity.setName("我");
			entity.setMsgType(false);
			entity.setText(contString);

			
			ChatMsgEntity entity1 = new ChatMsgEntity();
			entity1.setDate(getDate());
			entity1.setName("机器人");
			entity1.setMsgType(true);
			String tmp = select(contString);
			if(tmp!=null){
				entity1.setText(tmp);
			}else{
				entity1.setText("sorry didn't find");
			}
			
		  //  entity1.setText("sorry didn't find");
		   
			mDataArrays.add(entity);
			mDataArrays.add(entity1);
			
			mAdapter.notifyDataSetChanged();

			mEditTextContent.setText("");

			mListView.setSelection(mListView.getCount() - 2);
			
			
		}
	}

	private String getDate() {
		Calendar c = Calendar.getInstance();

		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins);

		return sbBuffer.toString();
	}


	

	private void openDataBase() {
		mSQLiteDataBase = this.openOrCreateDatabase("intellegentchat.db",
				MODE_PRIVATE, null);
		
		Cursor cursor = mSQLiteDataBase.rawQuery("select name from sqlite_master where type='table' AND name='answer';", null);
		
		if(!cursor.moveToNext()){
			String CREATE_TABLE = "create table answer(_id INTEGER PRIMARY KEY,input_word VARCHAR,target_word TEXT,is_need_time SMALLINT,from_time TIME,to_time TIME);";
			mSQLiteDataBase.execSQL(CREATE_TABLE);
			addData();
		}
	//	mSQLiteDataBase.close();
	//	addData();
	}
	private void addData() {

		/* ��ӷ�ʽһ */
		
		ContentValues cv = new ContentValues();
		cv.put("input_word", "morning");
		cv.put("target_word", "おはようございます！");
		cv.put("is_need_time", "1");
		cv.put("from_time", "6:01:00");
		cv.put("to_time", "10:00:00");
		
		mSQLiteDataBase.insert("answer", null, cv);
		
		mSQLiteDataBase.execSQL("Insert INTO answer (input_word,target_word,is_need_time,from_time,to_time) values('lunch','お昼時間！','1','10:01:00','14:00:00')");
		mSQLiteDataBase.execSQL("Insert INTO answer (input_word,target_word,is_need_time,from_time,to_time) values('afternoon','午後の仕事、頑張ってね。','1','14:01:00','17:00:00')");
		mSQLiteDataBase.execSQL("Insert INTO answer (input_word,target_word,is_need_time,from_time,to_time) values('dinner','晩ご飯は何にしようか。','1','17:01:00','21:00:00')");
		mSQLiteDataBase.execSQL("Insert INTO answer (input_word,target_word,is_need_time,from_time,to_time) values('good dream','おやすみなさい！','1','21:01:00','23:00:00')");
		
		

	
	}
	@SuppressLint("SimpleDateFormat")
	private String select(String que){
		String ans = null;
	//	mSQLiteDataBase = this.openOrCreateDatabase("intellegentchat.db",
	//			MODE_PRIVATE, null);
		Cursor c = mSQLiteDataBase.rawQuery("SELECT * FROM answer WHERE input_word = ?", new String[]{que});
		if(c.moveToNext()){
			if(c.getString(c.getColumnIndex("is_need_time")).equals("1")){
				DateFormat  dfs = new SimpleDateFormat("HH:mm:ss");
				Calendar canlendar = Calendar.getInstance();
				String hour = String.valueOf(canlendar.get(Calendar.HOUR_OF_DAY));
				String mins = String.valueOf(canlendar.get(Calendar.MINUTE));
				String sec = String.valueOf(canlendar.get(Calendar.SECOND));
				
				try{
				      Date begin = dfs.parse(c.getString(c.getColumnIndex("from_time")));
				      Date end = dfs.parse(c.getString(c.getColumnIndex("to_time")));
				      Date time = dfs.parse(hour+":"+mins+":"+sec);
				 //     Toast.makeText(this, time.toString(), Toast.LENGTH_LONG).show();
				      if(time.after(begin)&&time.before(end)){
				    	  ans = c.getString(c.getColumnIndex("target_word"));
				      }
				}catch(Exception e){
				//	Toast.makeText(this, "hello world", Toast.LENGTH_LONG).show();
				}    
			}else{
				ans = c.getString(c.getColumnIndex("target_word"));
			}
		}
		c.close();
	//	mSQLiteDataBase.close();
		return ans;
	}
   
}