package com.example.rocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

	private Button mBtn_startRecoket;
	private Button mBtn_stopRecoket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUi();
	}
	
	/**
	 * 初始化UI
	 */
	private void initUi() {
		
		mBtn_startRecoket = (Button)findViewById(R.id.btn_startRecoket);
		mBtn_stopRecoket = (Button)findViewById(R.id.btn_stopRecocket);
		mBtn_startRecoket.setOnClickListener(this);
		mBtn_stopRecoket.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_startRecoket:
			//开启服务
			ShowRocket();
			break;

		case R.id.btn_stopRecocket:
			//停止服务
			StopRocket();
			break;
		}
		
	}
	
	/**
	 * 
	 * 开启小火箭
	 */
	private void ShowRocket() {
		startService(new Intent(this,KaKaService.class));
		finish();
	}
	
	/**
	 * 停止小火箭
	 */
	private void StopRocket() {
		stopService(new Intent(this, KaKaService.class));
	}

	
}
