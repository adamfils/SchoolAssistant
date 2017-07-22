package com.adamapps.coursealert;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;

/**
 * Created by user on 20-Jul-17.
 */

public class SelectFavourites extends AppCompatActivity {

    ChipCloud scienceChip;
    LinearLayout scienceLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_favourites);

        scienceLayout = (LinearLayout) findViewById(R.id.science_chip);


        String[] scienceList = getResources().getStringArray(R.array.fs);

        ChipCloudConfig config = new ChipCloudConfig().selectMode(ChipCloud.SelectMode.multi)
                .checkedChipColor(Color.parseColor("#ff8a80"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#c0c0c0"))
                .uncheckedTextColor(Color.parseColor("#ffffff"))
                .useInsetPadding(true);
        scienceChip = new ChipCloud(this, scienceLayout, config);
        for (int i = 0; i < scienceList.length; i++) {
            scienceChip.addChip(scienceList[i]);
        }

    }
}
