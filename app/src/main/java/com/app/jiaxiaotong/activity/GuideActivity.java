package com.app.jiaxiaotong.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.weight.CirclePageIndicator;


/**
 * 首次启动APP导航界面
 * @author fy
 *
 */
public class GuideActivity extends BaseActivity{
	
	private GuideActivity activity = GuideActivity.this;
	private List<View> views = new ArrayList<View>();
	private int count = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		initView();
		gestureDetector = new GestureDetector(new GuideViewTouch());
		// 获取分辨率
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		flaggingWidth = dm.widthPixels / 3;
	}

	private void initView() {
		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.activity_guide_indicator);
		ViewPager pager = (ViewPager) findViewById(R.id.activity_guide_pager);
		final ImageView skipIv = (ImageView) findViewById(R.id.guide_jump_iv);
		skipIv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent updataIntent = new Intent(activity, LoginActivity.class);
				startActivity(updataIntent);
				finish();
			}
		});
		//引导页3张图片
		View pager1 = activity.getLayoutInflater().inflate(R.layout.item_guide_viewpager1, null);
		View pager2 = activity.getLayoutInflater().inflate(R.layout.item_guide_viewpager2, null);
		View pager3 = activity.getLayoutInflater().inflate(R.layout.item_guide_viewpager3, null);
		views.add(pager1);
		views.add(pager2);
		views.add(pager3);
		GuidePagerAdapter adapter = new GuidePagerAdapter();
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
//		indicator.setRadius(4);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 2) {
					skipIv.setVisibility(View.GONE);
				}else {
					skipIv.setVisibility(View.VISIBLE);
				}
				count = arg0;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		}
		return super.dispatchTouchEvent(event);
	}
	private GestureDetector gestureDetector; // 用户滑动
	/** 记录当前分页ID */
	private int flaggingWidth;// 互动翻页所需滚动的长度是当前屏幕宽度的1/3
	private class GuideViewTouch extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							   float velocityY) {
			if (count == 2) {
				if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY()
						- e2.getY())
						&& (e1.getX() - e2.getX() <= (-flaggingWidth) || e1
						.getX() - e2.getX() >= flaggingWidth)) {
					if (e1.getX() - e2.getX() >= flaggingWidth) {
						Intent updataIntent = new Intent(activity, LoginActivity.class);
						startActivity(updataIntent);
						finish();
						return true;
					}
				}
			}
			return false;
		}
	}
	class GuidePagerAdapter extends PagerAdapter{
		
		@Override
		public int getCount() {
			
			return views.size();
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(views.get(position), 0);
			return views.get(position);
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return super.getPageTitle(position);
		}
		
	}

}
