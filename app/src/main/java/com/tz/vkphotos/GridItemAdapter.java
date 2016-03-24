package com.tz.vkphotos;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoAlbum;
import com.vk.sdk.api.model.VKApiPhotoSize;
import com.vk.sdk.api.model.VKAttachments.VKApiAttachment;
import com.vk.sdk.api.model.VKPhotoSizes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GridItemAdapter extends BaseAdapter {

    private Activity mContext;
    private int mViewType;
    private List<VKApiAttachment> mItems;

    private static final int VIEW_TYPE_COUNT = 3;
    public static final int VIEW_TYPE_ALBUM = 0;
    public static final int VIEW_TYPE_PHOTO = 1;
    private int mThumbWidth;
    private int mThumbHeigth;

    public GridItemAdapter(Activity context, VKResponse response) {

        if (context instanceof AlbumActivity) {
            mViewType = VIEW_TYPE_ALBUM;
            mThumbWidth = (int) context.getResources().getDimension(R.dimen.album_thumb_width);
            mThumbHeigth = (int) context.getResources().getDimension(R.dimen.album_thumb_heigth);
        }
        else if (context instanceof PhotosActivity) {
            mViewType = VIEW_TYPE_PHOTO;
            mThumbWidth = (int) context.getResources().getDimension(R.dimen.photo_thumb_width);
            mThumbHeigth = (int) context.getResources().getDimension(R.dimen.photo_thumb_heigth);
        }

        mContext = context;

        mItems = new ArrayList<>();
        JSONArray jArray;
        try {

            jArray = response.json.getJSONObject("response").getJSONArray("items");
            for (int i = 0; i < jArray.length(); i++)
                mItems.add(createItem(jArray.getJSONObject(i)));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private VKApiAttachment createItem(JSONObject jo) throws JSONException{

        switch (mViewType){

            case VIEW_TYPE_ALBUM:
                return new VKApiPhotoAlbum(jo);
            case VIEW_TYPE_PHOTO:
                return new VKApiPhoto(jo);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            convertView =  LayoutInflater.from(mContext).inflate(R.layout.grid_item_layout, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        String url = null;

        switch (mViewType) {
            case VIEW_TYPE_ALBUM:
                holder.itemText.setText(((VKApiPhotoAlbum) mItems.get(i)).title);
                url = ((VKApiPhotoAlbum) mItems.get(i)).thumb_src;
                break;

            case VIEW_TYPE_PHOTO:
//                url = ((VKApiPhoto) mItems.get(i)).src.get(1).src;
                url = pickProperSource('m', i);
                break;
        }

        Picasso.with(mContext)
                .load(url)
                .resize(mThumbWidth, mThumbHeigth)
                .centerCrop()
                .into(holder.itemThumb,
                        new PicassoCallback(holder.itemThumbProgress) {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                super.onError();
                            }
                        });
        return convertView;
    }

    public String pickProperSource(char type, int i) {

        VKPhotoSizes sizes = ((VKApiPhoto) getItem(i)).src;
        for (VKApiPhotoSize size : sizes) {
            if (size.type == type)
                return size.src;
        }
        if (type == 'm')
            return sizes.get(1).src;
        else
            return sizes.get(sizes.size()-1).src;
    }

    class ViewHolder {

        @Bind(R.id.item_name) public TextView itemText;
        @Bind(R.id.item_thumb) public ImageView itemThumb;
        @Bind(R.id.item_thumb_progress) public ProgressBar itemThumbProgress;

        public ViewHolder(View view) {

            ButterKnife.bind(this, view);
            switch (mViewType){
                case VIEW_TYPE_ALBUM:
                    itemText.setVisibility(View.VISIBLE);
                    break;
                case VIEW_TYPE_PHOTO:
                    itemText.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private class PicassoCallback implements Callback {
        ProgressBar progressBar;

        public  PicassoCallback(ProgressBar progBar){
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {
        }

        @Override
        public void onError() {

        }
    }
}
