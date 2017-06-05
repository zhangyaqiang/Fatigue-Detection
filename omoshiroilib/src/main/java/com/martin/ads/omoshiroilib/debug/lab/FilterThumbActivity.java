package com.martin.ads.omoshiroilib.debug.lab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.filter.helper.FilterResourceHelper;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;

/**
 * Created by Ads on 2017/2/13.
 */
@Deprecated
public class FilterThumbActivity extends AppCompatActivity {
    private FilterType[] filterTypes=FilterType.values();
    private int filterSize=filterTypes.length;
    private int currentPos=0;
    private ImageView imageView;
    private TextView currentFilterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_filter_thumb);

        findViewById(R.id.generate_thumb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterResourceHelper.logAllFilters();
                FilterResourceHelper.generateFilterThumbs(FilterThumbActivity.this,false);
            }
        });

        findViewById(R.id.set_thumb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateThumb();
                    }
                });
            }
        });
        currentFilterText= (TextView) findViewById(R.id.current_filter_text);
        imageView= (ImageView) findViewById(R.id.test_image);
    }

    private void updateThumb(){
        imageView.setImageBitmap(
                FilterResourceHelper.getFilterThumbFromFile(this,filterTypes[currentPos]));
        currentFilterText.setText(filterTypes[currentPos].name().toUpperCase());
        currentPos=(currentPos+1)%filterSize;
    }
}
