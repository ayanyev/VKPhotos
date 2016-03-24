package com.tz.vkphotos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.tz.vkphotos.GenericActivity.*;

public class PhotoActivity extends AppCompatActivity{

    @Bind(R.id.item_layout) RelativeLayout layout;
    @Bind(R.id.item_thumb) ImageView photoView;
    @Bind(R.id.item_name) TextView photoText;
    @Bind(R.id.item_thumb_progress) ProgressBar photoProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_item_layout);

        setTitle(R.string.back_to_album);
        ButterKnife.bind(this);

        String text = getIntent().getStringExtra(PHOTO_TEXT_KEY);
        if (!text.equals(""))
            photoText.setText(text);
        else
            photoText.setVisibility(View.GONE);

        Picasso.with(this)
                .load(getIntent().getStringExtra(PHOTO_URL_KEY))
                .into(photoView,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                photoProgress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                            }
                        });
    }
}
