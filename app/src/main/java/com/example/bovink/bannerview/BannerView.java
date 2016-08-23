package com.example.bovink.bannerview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

/**
 * com.example.bovink.bannerview
 *
 * @author bovink
 * @since 2016/7/18
 */
public class BannerView extends FrameLayout implements View.OnTouchListener {
    private ViewPager viewPager;
    private LinearLayout dotLinearLayout;
    private Context context;
    private SwitchRunnable switchRunnable = new SwitchRunnable();

    private Long switchTime;


    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutParams params;

        // 广告Banner
        viewPager = new ViewPager(context);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewPager.setLayoutParams(params);
        viewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener());
        viewPager.setOnTouchListener(this);
        addView(viewPager);

        // 指示点线性布局
        dotLinearLayout = new LinearLayout(context);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.bottomMargin = 16;
        dotLinearLayout.setLayoutParams(params);
        addView(dotLinearLayout);


    }

    public BannerView setData(Holder<String> holder, List<String> strings) {
        viewPager.setAdapter(new ViewPagerAdapter<>(holder, strings));

        for (int i = 0; i < strings.size(); i++) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 10;
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.shape_dot_normal);

            dotLinearLayout.addView(imageView);
        }
        return this;
    }

    public void startSwitch(long time) {
        switchTime = time;
        viewPager.postDelayed(switchRunnable, time);
    }

    public void stopSwitch() {
        viewPager.removeCallbacks(switchRunnable);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                viewPager.removeCallbacks(switchRunnable);
                break;
            case MotionEvent.ACTION_UP:
                viewPager.postDelayed(switchRunnable, switchTime);
                break;
        }

        return false;
    }

    private class SwitchRunnable implements Runnable {

        @Override
        public void run() {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            startSwitch(switchTime);

        }
    }

    private class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            System.out.println(position + "");
            setCurrentDot(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 设置当前Dot的状态
     * @param position
     */
    private void setCurrentDot(int position) {
        int realPosition = position % dotLinearLayout.getChildCount();
        for (int i = 0; i < dotLinearLayout.getChildCount(); i++) {
            ((ImageView) dotLinearLayout.getChildAt(i)).setImageResource(R.drawable.shape_dot_normal);
        }
        ((ImageView) dotLinearLayout.getChildAt(realPosition)).setImageResource(R.drawable.shape_dot_focused);
    }

    /**
     * 内容适配器
     *
     * @param <T>
     */
    private class ViewPagerAdapter<T> extends PagerAdapter {

        Holder holder;
        List<T> datas;

        public ViewPagerAdapter(Holder holder, List<T> datas) {
            this.holder = holder;
            this.datas = datas;
        }

        public int toRealPosition(int position) {
            int realCount = getRealCount();
            if (realCount == 0)
                return 0;
            return position % realCount;
        }

        public int getRealCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int realPosition = toRealPosition(position);
            View view = getView(container, realPosition, null);
            container.addView(view);
            return view;
        }

        private View getView(ViewGroup container, int position, View view) {
            Holder holder;
            if (view == null) {
                holder = this.holder;
                view = holder.createView(container.getContext());
                view.setTag(holder);

            } else {
                holder = (Holder) view.getTag();
            }
            if (datas != null && datas.size() != 0) {
                holder.viewCreated(container.getContext(), position, datas.get(position));
            }
            return view;
        }
    }

    /**
     * 通过此接口初始化
     *
     * @param <T>
     */
    public interface Holder<T> {
        View createView(Context context);

        void viewCreated(Context context, int position, T data);
    }
}
