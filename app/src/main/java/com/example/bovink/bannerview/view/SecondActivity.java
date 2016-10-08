package com.example.bovink.bannerview.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.example.bovink.bannerview.BannerView;

/**
 * com.example.bovink.bannerview.view
 *
 * @author bovink
 * @since 2016/7/18
 */
public class SecondActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class ViewItemViewHolder implements BannerView.ItemViewHolder<String> {
        @Override
        public View createView(Context context) {
            return null;
        }

        @Override
        public void viewCreated(Context context, int position, String data) {

        }
    }

}
