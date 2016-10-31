package com.wjc.worldlet.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wjc.worldlet.R;
import com.wjc.worldlet.activity.SystemPlayerActivity;
import com.wjc.worldlet.entity.MediaItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by ${万嘉诚} on 2016/10/5.
 * 微信：wjc398556712
 * 作用：
 */
public class NetVideoFragment extends MyFragment {
    private View view;
    private ListView listview;
    private ProgressBar progressbar;
    private TextView tv_nomedia;
    private ArrayList<MediaItem> mediaItems;
    private MyAdapter myAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_new_video, container, false);

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    protected void onVisible(boolean isInit) {
        // TODO Auto-generated method stub
        if (isInit) {
            initView();
            netGetData();// 网络访问，获取列表数据
        }

    }

    private void netGetData() {
        Log.e("TAG", "网络视频数据初始化了--------");

        getDataFromNet();

        isGrantExternalRW(getActivity());

    }

    private void getDataFromNet() {
        final RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess======" + result);
                processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "onError========" + ex.getMessage());

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("TAG", "onCancelled========" + cex.getMessage());

            }

            @Override
            public void onFinished() {
                Log.e("TAG", "onFinished=========");

            }
        });

    }

    /**
     * 解析和绑定数据
     * @param json
     */
    private void processData(String json) {
        mediaItems = parsedJson(json);
        if(mediaItems != null && mediaItems.size() >0){
            //有视频
            tv_nomedia.setVisibility(View.GONE);
            //设置适配器
            myAdapter = new MyAdapter();
            listview.setAdapter(myAdapter);
        }else{
            //没有视频
            tv_nomedia.setVisibility(View.VISIBLE);
        }

        progressbar.setVisibility(View.GONE);

    }

    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mediaItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(getActivity(),R.layout.item_net_video,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据位置获得对应的数据
            MediaItem mediaItem = mediaItems.get(position);
            x.image().bind(viewHolder.iv_icon,mediaItem.getImageUrl());
            viewHolder.tv_name.setText(mediaItem.getName());
            viewHolder.tv_desc.setText(mediaItem.getDesc());


            return convertView;
        }

    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;

    }

    /**
     * 手动解析json数据
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parsedJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray trailers = jsonObject.optJSONArray("trailers");
            for (int i = 0; i < trailers.length(); i++) {
                JSONObject item = (JSONObject) trailers.get(i);
                if (item != null) {
                    MediaItem mediaItem = new MediaItem();

                    String name = item.optString("movieName");
                    mediaItem.setName(name);

                    String desc = item.optString("videoTitle");
                    mediaItem.setDesc(desc);

                    String imageUrl = item.optString("coverImg");
                    mediaItem.setImageUrl(imageUrl);

                    String data = item.optString("url");
                    mediaItem.setData(data);

                    mediaItems.add(mediaItem);

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mediaItems;
    }

    private void initView() {
        listview = (ListView) view.findViewById(R.id.listview);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SystemPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");

                //使用Bundler传递视频列表数据
                Bundle bundle = new Bundle();
                bundle.putSerializable("medialist",mediaItems);
                intent.putExtra("position" , position);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });
    }


    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

}
