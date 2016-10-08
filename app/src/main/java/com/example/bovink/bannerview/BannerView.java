package com.example.bovink.bannerview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
 * 工具View，用来提供使用的View
 *
 * @author bovink
 * @since 2016/7/18
 */
public class BannerView extends FrameLayout {
    /**
     * 显示图片
     */
    private ViewPager viewPager;
    /**
     * 显示标识点
     */
    private LinearLayout dotLinearLayout;
    /**
     * 环境
     */
    private Context context;
    /**
     * BannerItem点击事件
     */
    private OnBannerItemClick onBannerItemClick;
    /**
     * 控制Banner切换的Handler
     */
    private SwitchHandler switchHandler = new SwitchHandler();
    /**
     * Banner切换的时间
     */
    private Long switchTime;
    /**
     * Banner是否在切换的flag
     */
    private Boolean isSwitching = false;
    /**
     * 开始切换Banner
     */
    private final static int START_SWITCH = 1;
    /**
     * 停止切换Banner
     */
    private final static int STOP_SWITCH = 2;

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

    /**
     * 初始化
     * @param context 环境
     */
    private void init(Context context) {
        this.context = context;
        LayoutParams params;

        // 广告Banner
        viewPager = new ViewPager(context);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewPager.setLayoutParams(params);
        viewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener());

        addView(viewPager);

        // 指示点线性布局
        dotLinearLayout = new LinearLayout(context);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.bottomMargin = 16;
        dotLinearLayout.setLayoutParams(params);
        addView(dotLinearLayout);


    }

    /**
     * 添加数据
     *
     * @param holder 接口
     * @param strings 数据
     * @return bannerview
     */
    public BannerView setData(Holder<String> holder, List<String> strings) {
        viewPager.setAdapter(new ViewPagerAdapter<>(holder, strings));

        dotLinearLayout.removeAllViews();
        for (int i = 0; i < strings.size(); i++) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.rightMargin = 10;
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.shape_dot_normal);

            dotLinearLayout.addView(imageView);
        }
        return this;
    }

    /**
     * 如果没有在切换，开始切换
     *
     * @param time 切换的时间
     */
    public void startSwitch(long time) {
        if (!isSwitching) {

            switchTime = time;
            isSwitching = true;
            switchHandler.sendEmptyMessageDelayed(START_SWITCH, time);
        }
    }

    /**
     * 如果正在切换，停止切换
     */
    public void stopSwitch() {
        if (isSwitching) {

            isSwitching = false;
            switchHandler.sendEmptyMessage(STOP_SWITCH);
        }
    }

    /**
     * 用来控制Banner切换
     */
    private class SwitchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_SWITCH:
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    sendEmptyMessageDelayed(START_SWITCH, switchTime);
                    break;
                case STOP_SWITCH:
                    removeMessages(START_SWITCH);
                    break;
            }
        }
    }

    /**
     * 设置Banner点击事件
     *
     * @param onBannerItemClick 点击事件
     */
    public void setOnBannerItemClick(OnBannerItemClick onBannerItemClick) {
        this.onBannerItemClick = onBannerItemClick;
    }

    /**
     * 滑动监听
     */
    private class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setCurrentDot(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

            switch (state) {
                case ViewPager.SCROLL_STATE_IDLE:
                    startSwitch(switchTime);
                    break;
            }
        }
    }

    /**
     * 设置当前Dot的状态
     *
     * @param position 位置
     */
    private void setCurrentDot(int position) {
        int realPosition = position % dotLinearLayout.getChildCount();
        for (int i = 0; i < dotLinearLayout.getChildCount(); i++) {
            ((ImageView) dotLinearLayout.getChildAt(i)).setImageResource(R.drawable.shape_dot_normal);
        }
        ((ImageView) dotLinearLayout.getChildAt(realPosition)).setImageResource(R.drawable.shape_dot_focused);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                stopSwitch();
                break;
            // 故意这样写，让ACTION_CANCEL和ACTION_UP执行同一反应
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startSwitch(switchTime);
                break;
        }
        return false;
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

        /**
         * 换算成真实数据
         * @param position 位置
         * @return 位置
         */
        public int toRealPosition(int position) {
            int realCount = getRealCount();
            if (realCount == 0)
                return 0;
            return position % realCount;
        }

        /**
         * 获取图片的真实数据
         * @return 数
         */
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
            final int realPosition = toRealPosition(position);
            final View view = getView(container, realPosition, null);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBannerItemClick != null) {
                        onBannerItemClick.onItemClick(view, realPosition);
                    }
                }
            });
            container.addView(view);
            return view;
        }

        /**
         * 获取视图
         * @param container 父布局
         * @param position 位置
         * @param view 视图
         * @return 视图
         */
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

    /**
     * ViewPagerItem点击监听
     */
    public interface OnBannerItemClick {
        void onItemClick(View view, int position);
    }
}
