package com.example.bovink.bannerview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BannerView bannerView;
    private List<String> networkImages;
    /**
     * 网络图片
     */
    private String[] images = {"http://img2.imgtn.bdimg.com/it/u=3093785514,1341050958&fm=21&gp=0.jpg",
            "http://img2.3lian.com/2014/f2/37/d/40.jpg",
            "http://d.3987.com/sqmy_131219/001.jpg",
            "http://img2.3lian.com/2014/f2/37/d/39.jpg",
            "http://www.8kmm.com/UploadFiles/2012/8/201208140920132659.jpg",
            "http://f.hiphotos.baidu.com/image/h%3D200/sign=1478eb74d5a20cf45990f9df460b4b0c/d058ccbf6c81800a5422e5fdb43533fa838b4779.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/09fa513d269759ee50f1971ab6fb43166c22dfba.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);


        networkImages= Arrays.asList(images);

        bannerView = (BannerView) findViewById(R.id.bannerView);
        bannerView.setData(new ViewPagerHolder(), networkImages);
        bannerView.startSwitch(1000);
    }

    private class ViewPagerHolder implements BannerView.Holder<String> {
        SimpleDraweeView simpleDraweeView;

        @Override
        public View createView(Context context) {
            simpleDraweeView = new SimpleDraweeView(context);
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
            builder.setPlaceholderImage(R.mipmap.ic_launcher);
            simpleDraweeView.setHierarchy(builder.build());

            return simpleDraweeView;
        }

        @Override
        public void viewCreated(Context context, int position, String data) {
            simpleDraweeView.setImageURI(data);
        }
    }

}
