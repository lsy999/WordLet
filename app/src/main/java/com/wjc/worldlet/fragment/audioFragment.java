package com.wjc.worldlet.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wjc.worldlet.R;
import com.wjc.worldlet.activity.AudioPlayerActivity;
import com.wjc.worldlet.adapter.VideoFragmentAdapter;
import com.wjc.worldlet.entity.MediaItem;

import java.util.ArrayList;

/**
 * Created by ${万嘉诚} on 2016/10/6.
 * 微信：wjc398556712
 * 作用：本地音频
 */
public class audioFragment extends MyFragment {
    private View view;
    private ListView listview;
    private TextView textView;
    private ArrayList<MediaItem> mediaItems;
    private VideoFragmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_audio, container, false);

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据
                textView.setVisibility(View.GONE);
                adapter = new VideoFragmentAdapter(getActivity(), mediaItems, false);
                listview.setAdapter(adapter);
//                adapter.setData(mediaItems);
//                adapter.notifyDataSetChanged();//getCount()-->getView重新执行
            } else {
                //没有数据
                textView.setText("没有找到音频文件...");
                textView.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onVisible(boolean isInit) {
        if (isInit) {
            initView();
            getData();
            isGrantExternalRW(getActivity());
        }

    }

    private void getData() {
        //主线程
        new Thread() {
            @Override
            public void run() {
                super.run();

                //子线程

                mediaItems = new ArrayList<MediaItem>();

                ContentResolver resolver = getActivity().getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//视频的uri
                String[] objs = new String[]{
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频文件的名称
                        MediaStore.Audio.Media.SIZE,//文件大小
                        MediaStore.Audio.Media.DURATION,//视频文件的时长
                        MediaStore.Audio.Media.DATA,//视频文件绝对地址
                        MediaStore.Audio.Media.ARTIST//艺术家


                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        MediaItem mediaItem = new MediaItem();
                        String name = cursor.getString(0);
                        mediaItem.setName(name);
                        long size = cursor.getLong(1);
                        mediaItem.setSize(size);
                        long duration = cursor.getLong(2);
                        mediaItem.setDuration(duration);
                        String data = cursor.getString(3);
                        mediaItem.setData(data);
                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);

                        //添加到集合中
                        mediaItems.add(mediaItem);
                    }


                    cursor.close();


                }

                //发消息
                handler.sendEmptyMessage(0);


            }
        }.start();

        //主线程

    }


    private void initView() {
        Log.e("TAG", "本地音频视图（页面）初始化了...");
        listview = (ListView) view.findViewById(R.id.listview);
        textView = (TextView) view.findViewById(R.id.tv_nomedia);

        //设置item的点击事件
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaItem mediaItem = mediaItems.get(position);

                //传递音频列表
                Intent intent = new Intent(getActivity(), AudioPlayerActivity.class);
                //传递位置
                intent.putExtra("position", position);
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
