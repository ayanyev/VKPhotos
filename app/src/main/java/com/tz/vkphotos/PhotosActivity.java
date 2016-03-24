package com.tz.vkphotos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoSize;
import com.vk.sdk.api.model.VKPhotoSizes;

import static com.tz.vkphotos.GridItemAdapter.*;

public class PhotosActivity extends GenericActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getIntent().getStringExtra(ALBUM_TITLE_KEY));
        if (isConnected())
            fillGridWithItems(VIEW_TYPE_PHOTO);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent =
                new Intent(this, PhotoActivity.class)
                        .putExtra(PHOTO_URL_KEY, mAdapter.pickProperSource('z', i))
                        .putExtra(PHOTO_TEXT_KEY, ((VKApiPhoto) mAdapter.getItem(i)).text);
        startActivity(intent);
    }
}
