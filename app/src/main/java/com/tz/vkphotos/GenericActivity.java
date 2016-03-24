package com.tz.vkphotos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.tz.vkphotos.GridItemAdapter.*;

public class GenericActivity extends AppCompatActivity implements GridView.OnItemClickListener{

    protected static final String ALBUM_ID_KEY = "album_id";
    protected static final String ALBUM_TITLE_KEY = "album_title";
    protected static final String PHOTO_URL_KEY = "photo_url";
    protected static final String PHOTO_TEXT_KEY = "photo_text";
    @Bind(R.id.generic_grid) protected GridView mGrid;
    protected GridItemAdapter mAdapter;
    protected Activity mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic);

        mContext = this;

        ButterKnife.bind(this);

        mGrid.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vk_logout:

                VKSdk.logout();
                finish();
                startActivity(new Intent(this, AlbumActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void fillGridWithItems(int type) {

        String method;
        VKParameters params;
        final int width;

        switch (type){

            case VIEW_TYPE_ALBUM:

                method = "photos.getAlbums";
                params = VKParameters.from(
                        VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId,
                        "need_covers", "1");
                width = ((int) getResources().getDimension(R.dimen.album_thumb_width));
                break;

            case VIEW_TYPE_PHOTO:

                method = "photos.get";
                params = VKParameters.from(
                        VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId,
                        VKApiConst.ALBUM_ID, getIntent().getStringExtra(ALBUM_ID_KEY),
                        VKApiConst.PHOTO_SIZES, "1",
                        VKApiConst.REV, "1");
                width = ((int) getResources().getDimension(R.dimen.photo_thumb_width));
                break;
            default:
                return;
        }

        new VKRequest(method, params)
                .executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {

                        mAdapter = new GridItemAdapter(mContext, response);
                        mGrid.setNumColumns(mGrid.getMeasuredWidth() / width);
                        mGrid.setAdapter(mAdapter);
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
