package com.tz.vkphotos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKApiPhotoAlbum;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKUsersArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.tz.vkphotos.GridItemAdapter.*;

public class AlbumActivity extends GenericActivity {

    String[] scope = new String[]{VKScope.PHOTOS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isConnected()) {
            if (!VKSdk.isLoggedIn())
                VKSdk.login(this, scope);
            else {
                setTitle(R.string.albums);
                fillGridWithItems(VIEW_TYPE_ALBUM);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {

                setTitle(R.string.albums);
                fillGridWithItems(VIEW_TYPE_ALBUM);
            }

            @Override
            public void onError(VKError error) {
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent =
                new Intent(this, PhotosActivity.class)
                .putExtra(ALBUM_ID_KEY, String.valueOf(((VKApiPhotoAlbum) mAdapter.getItem(i)).getId()))
                .putExtra(ALBUM_TITLE_KEY, String.valueOf(((VKApiPhotoAlbum) mAdapter.getItem(i)).title));
        startActivity(intent);
    }
}
