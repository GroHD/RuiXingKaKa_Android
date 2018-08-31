package com.example.rocket;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class KaKaService extends Service {

	private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private WindowManager mWm;
	private View mToast;
	private ImageView iv;
	// 双击的第一次点击时间
	float startTime = 0;
	// 随机的动作{第一个参数是 要执行那个动画文件，第二个是动画文件一共有多少张}
	private int[][] kakaActionArray = { { R.drawable.kaka_findv_item_list, 68 },
			{ R.drawable.kaka_deletef_item_list, 40 }, { R.drawable.kaka_gally_item_list, 39 } };
	// 定时任务
	private Timer timer = null;
	private TimerTask task = null;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			iv.setBackgroundResource(msg.what);
			SystemClock.sleep(10);
			AnimationDrawable anim = (AnimationDrawable) iv.getBackground();
		
			anim.start();

			mWm.updateViewLayout(mToast, mParams);
		};
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		mWm = (WindowManager) getSystemService(WINDOW_SERVICE);
		// 开启动画
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		mParams.format = PixelFormat.TRANSLUCENT;
		// 在响铃的时候显示toast TYPE_PHONE(在响铃的时候显示toast 和电话类型一致)
		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		// 指定toast起始位置在位置 (右下角)
		mParams.gravity = Gravity.RIGHT + Gravity.BOTTOM;
		mParams.setTitle("Toast");
		// 显示toast效果
		mToast = View.inflate(getApplicationContext(), R.layout.kaka_anmi_list, null);
		mWm.addView(mToast, mParams);
		iv = mToast.findViewById(R.id.iv_kaka_item_list);

		handler.sendEmptyMessage(R.drawable.kaka_smog_item_list);
		new Thread() {
			public void run() {
				SystemClock.sleep(2830);
				handler.sendEmptyMessage(R.drawable.kaka_stand_item_list);

			};

		}.start();

		// 拖动动画
		iv.setOnTouchListener(new OnTouchListener() {

			private int strtX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					strtX = (int) event.getX();
					startY = (int) event.getY();
					// 点击的时候更换动画
					break;

				case MotionEvent.ACTION_MOVE:
					int moveX = (int) event.getX();
					int moveY = (int) event.getY();
					// 计算移动的角度
					int endMoveX = moveX - strtX;
					int endMoveY = moveY - startY;
					// int tosLeft = mToast.getLeft()-endMoveX;
					// int tosTop = mToast.getBottom() -endMoveY;
					// int height = mWm.getDefaultDisplay().getHeight()-endMoveY;
					// int width = mWm.getDefaultDisplay().getWidth()-endMoveX;
					int width = iv.getWidth() / 2;
					int height = iv.getHeight() / 2;
					Log.i("event", "endMoveX:" + (width) + " endMoveY:" + (height));

					mParams.x = ((mParams.x + width) - moveX);
					mParams.y = ((mParams.y + height) - moveY);
					mWm.updateViewLayout(mToast, mParams);

					// 移动
					break;
				case MotionEvent.ACTION_UP:
					int endX = (int) event.getX();
					int endY = (int) event.getY();
					// 抬起的时候动画切换回来
					break;
				}
				return false;
			}
		});

		// 双击
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (startTime == 0) {
					startTime = SystemClock.uptimeMillis();
					Log.i("startTime", startTime + "");
				} else {
					float endTime = SystemClock.uptimeMillis();
					Log.i("endTime", endTime + "  =>" + (endTime - startTime) + "");
					if ((endTime - startTime) < 500) {
						SystemClock.sleep(10);
						startTime = 0;
						handler.sendEmptyMessage(R.drawable.kaka_dblclk_item_list);
						new Thread() {
							public void run() {

								SystemClock.sleep(1080);
								handler.sendEmptyMessage(R.drawable.kaka_stand_item_list);
							};

						}.start();

					}
					startTime = 0;
				}

			}
		});

		// 开启定时任务
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				// TODO 开始随机的执行一些动作
				int actionSize = kakaActionArray.length;
				Random rd = new Random();
				int rdInt = rd.nextInt(actionSize);
				int[] actionArr = kakaActionArray[rdInt];
				final int actionLength = actionArr[1];
				handler.sendEmptyMessage(actionArr[0]);

				Log.i("actionLength", "" + actionLength);
				new Thread() {
					@Override
					public void run() {
						SystemClock.sleep(actionLength*90);
						handler.sendEmptyMessage(R.drawable.kaka_stand_item_list);
					}
					
				}.start();
				

			}
		};

		// 一分钟后开始执行定时任务，之后每一分钟执行一次
		timer.schedule(task, 1000 * 10, 1000 * 10);

	}

	@Override
	public void onDestroy() {
		if (mWm != null) {
			// 停止定时任务
			if (timer != null && task != null) {
				timer.cancel();
				task.cancel();
			}
			// 隐藏
			handler.sendEmptyMessage(R.drawable.kaka_vanish_item_list);
			new Thread() {
				public void run() {

					SystemClock.sleep(2000);
					mWm.removeView(mToast);
				};

			}.start();

		}
	}

}
