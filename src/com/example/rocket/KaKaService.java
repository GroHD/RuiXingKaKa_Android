package com.example.rocket;

import java.lang.reflect.Field;
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
	private AnimationDrawable anim;
	// 双击的第一次点击时间
	float startTime = 0;
	// 随机的动作
	private int[] kakaActionArray = { R.drawable.kaka_findv_item_list, R.drawable.kaka_deletef_item_list,
			R.drawable.kaka_gally_item_list, R.drawable.kaka_eatwm_item_list, R.drawable.kaka_ignorev_item_list,
			R.drawable.kaka_killv_item_list };
	// 定时任务
	private Timer timer = null;
	private TimerTask task = null;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			iv.setBackgroundResource(msg.what);
			SystemClock.sleep(100);
			anim = (AnimationDrawable) iv.getBackground();
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
		// 执行完上面的动画切回首页动画
		IsStopDrawable();

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
						// handler.sendEmptyMessage(R.drawable.kaka_killv_item_list);
						// 执行完上面的动画切回首页动画
						IsStopDrawable();

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
				int action = kakaActionArray[rdInt];

				handler.sendEmptyMessage(action);
				// 判断动画之后到最后一帧的时候切换回stand动画
				IsStopDrawable();
				System.gc();

			}
		};

		// 30秒后开始执行定时任务，之后每30秒执行一次
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
			SystemClock.sleep(100);
			// 判断线程是否执行到最后一张，如果是则执行后面的动画
			new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							Field field = AnimationDrawable.class.getDeclaredField("mCurFrame");
							field.setAccessible(true);
							int curFrame = field.getInt(anim);// 获取anim动画的当前帧
							if (curFrame == anim.getNumberOfFrames() - 1)// 如果已经到了最后一帧
							{
								mWm.removeView(mToast);

								return;
							}

						} catch (Exception ex) {
							ex.printStackTrace();
						}
						SystemClock.sleep(100);
					}
				}

			}.start();

		}
	}

	/**
	 * 判断是否执行完毕动画，执行完毕之后设置动画为坐下的动画
	 */
	public void IsStopDrawable() {
		// 判断线程是否执行到最后一张，如果是则执行后面的动画
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Field field = AnimationDrawable.class.getDeclaredField("mCurFrame");
						field.setAccessible(true);
						int curFrame = field.getInt(anim);// 获取anim动画的当前帧
						if (curFrame == anim.getNumberOfFrames() - 1)// 如果已经到了最后一帧
						{
							handler.sendEmptyMessage(R.drawable.kaka_stand_item_list);

							return;
						}

					} catch (Exception ex) {
						ex.printStackTrace();
					}
					SystemClock.sleep(10);
				}
			}

		}.start();
	}

}
