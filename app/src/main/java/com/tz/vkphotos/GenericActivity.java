package com.tz.vkphotos;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tz.vkphotos.GridItemAdapter.*;

public class GenericActivity extends AppCompatActivity implements GridView.OnItemClickListener{

    protected static final String ALBUM_ID_KEY = "album_id";
    protected static final String ALBUM_TITLE_KEY = "album_title";
    protected static final String PHOTO_URL_KEY = "photo_url";
    protected static final String PHOTO_TEXT_KEY = "photo_text";
    @Bind(R.id.generic_grid) protected GridView mGrid;
    @Bind(R.id.emptyGrid) protected TextView mEmptyGridText;
    @Bind(R.id.sync) protected ImageView mSyncButton;
    protected GridItemAdapter mAdapter;
    protected GenericActivity mContext;
    private boolean isConnected = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic);

        mContext = this;

        ButterKnife.bind(this);

        mGrid.setOnItemClickListener(this);
    }

    protected boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        // if connection is absent
        if (info == null || !info.isConnected()){
            mEmptyGridText.setText(R.string.no_internet);
            mGrid.setVisibility(View.GONE);
            mEmptyGridText.setVisibility(View.VISIBLE);
            mSyncButton.setVisibility(View.VISIBLE);
            isConnected = false;
            invalidateOptionsMenu();
        }
        return isConnected;
    }

    @OnClick(R.id.sync)
    void onClick(View view){

        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflates action bar menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // hide/show menu item dependent on connectivity status
        menu.getItem(0).setVisible(isConnected);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vk_logout:
                // logs out user and restarts main activity to be able to log in again
                VKSdk.logout();
                finish();
                startActivity(new Intent(this, AlbumActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // fills gridView with items depending on its type
    protected void fillGridWithItems(int type) {

        String method;
        VKParameters params;
        final int width;

        switch (type){

            case VIEW_TYPE_ALBUM:

                mEmptyGridText.setText(R.string.zero_albums);
                method = "photos.getAlbums";
                params = VKParameters.from(
                        VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId,
                        VKApiConst.PHOTO_SIZES, "1",
                        "need_covers", "1");
                width = ((int) getResources().getDimension(R.dimen.album_thumb_width));
                break;

            case VIEW_TYPE_PHOTO:

                mEmptyGridText.setText(R.string.zero_photos);
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

        // api async request with callback
        new VKRequest(method, params)
                .executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {

                        JSONArray jArray;
                        try {
                            // gets an array of json items out of response
                            jArray = response.json.getJSONObject("response").getJSONArray("items");
                            // if array empty - shows message and returns
                            if (jArray.length() == 0){
                                mGrid.setVisibility(View.GONE);
                                mEmptyGridText.setVisibility(View.VISIBLE);
                                return;
                            }

                            mAdapter = new GridItemAdapter(mContext, jArray);
                            mGrid.setNumColumns(mGrid.getMeasuredWidth() / width);
                            mGrid.setAdapter(mAdapter);

                        } catch (JSONException e) {
                            mEmptyGridText.setText(R.string.error);
                            mGrid.setVisibility(View.GONE);
                            mEmptyGridText.setVisibility(View.VISIBLE);
                        }
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
