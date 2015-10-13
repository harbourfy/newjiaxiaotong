package com.app.jiaxiaotong.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.im.MyHXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {
	private ImageView splashIv;
	private static final int sleepTime = 2500;
	private boolean isFristTime = false;
	private static final String APPUSERTIME = "appusertime";
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_splash);
		splashIv = (ImageView) findViewById(R.id.iv_splash_logo);
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		splashIv.startAnimation(animation);
		SharedPreferences preferences = getSharedPreferences(APPUSERTIME, 0);
		int usecount = preferences.getInt(APPUSERTIME, 0);
		if (usecount == 0) {
			isFristTime = true;
		}
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(APPUSERTIME, ++usecount);
		editor.commit();
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread(new Runnable() {
			public void run() {
				if (!isFristTime) {
					if (MyHXSDKHelper.getInstance().isLogined()) {
						// ** 免登陆情况 加载所有本地群和会话
						//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
						//加上的话保证进了主页面会话和群组都已经load完毕
						long start = System.currentTimeMillis();
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						long costTime = System.currentTimeMillis() - start;
						//等待sleeptime时长
						if (sleepTime - costTime > 0) {
							try {
								Thread.sleep(sleepTime - costTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						//进入主页面
						startActivity(new Intent(SplashActivity.this, MainActivity.class));
						finish();
					} else {
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
						}
						startActivity(new Intent(SplashActivity.this, LoginActivity.class));
						finish();
					}
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, GuideActivity.class));
					finish();
				}
			}
		}).start();

	}
	
	/**
	 * 获取当前应用程序的版本号
	 */
	private String getVersion() {
		String st = getResources().getString(R.string.Version_number_is_wrong);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}
}
