package com.wjc.worldlet.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wjc.worldlet.R;
import com.wjc.worldlet.entity.MediaItem;
import com.wjc.worldlet.utils.LogUtil;
import com.wjc.worldlet.utils.Utils;
import com.wjc.worldlet.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ${万嘉诚} on 2016/9/28.
 * 系统播放器
 */
public class SystemPlayerActivity extends Activity implements View.OnClickListener {

    /**
     * 常量-视频进度更新
     */
    private static final int PROGRESS = 1;
    /**
     * 常量-隐藏控制面板
     */
    private static final int HIDE_MEDIACONTROLL = 2;
    /**
     * 默认
     */
    private static final int SCREEN_DEFULT = 1;
    /**
     * 全屏
     */
    private static final int SCREEN_FULL = 2;

    private VideoView videoview;
    private Uri uri;

    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVideoVoice;
    private SeekBar seekbarVoice;
    private Button btnVideoSwichPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrent;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnVideoExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSwichScreen;

    private MyBroadcastReceiver receiver;
    /**
     * 当前音量
     */
    private int currentVolume;
    //最大音量
    private int maxVolume;
    private Utils utils;
    /**
     * 视频列表数据
     */
    private ArrayList<MediaItem> mediaItems;
    /**
     * 点击视频在列表中的位置
     */
    private int position;

    //1.定义手势识别器
    private GestureDetector detector;
    /**
     * 是否是全屏
     */
    private boolean isFullScreen = false;
    /**
     * 屏幕的宽和高
     */
    private int screenWidth;
    private int screenHeight;
    /**
     * 真实视频的宽和高
     */
    private int videoWidht;
    private int videoHeight;

    /**
     * 调节声音
     */
    private AudioManager am;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-10-04 17:24:21 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        videoview = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVideoVoice = (Button) findViewById(R.id.btn_video_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnVideoSwichPlayer = (Button) findViewById(R.id.btn_video_swich_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrent = (TextView) findViewById(R.id.tv_current);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnVideoExit = (Button) findViewById(R.id.btn_video_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSwichScreen = (Button) findViewById(R.id.btn_video_swich_screen);

        btnVideoVoice.setOnClickListener(this);
        btnVideoSwichPlayer.setOnClickListener(this);
        btnVideoExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSwichScreen.setOnClickListener(this);

        //设置最大音量
        seekbarVoice.setMax(maxVolume);
        seekbarVoice.setProgress(currentVolume);
    }

    private boolean isMute = false;

    @Override
    public void onClick(View v) {
        if (v == btnVideoVoice) {
            isMute = !isMute;
            updateVolume(currentVolume);

        } else if (v == btnVideoSwichPlayer) {


        } else if (v == btnVideoExit) {
            finish();

        } else if (v == btnVideoPre) {
            setPlayPreVideo();
        } else if (v == btnVideoStartPause) {//播放和暂停
            setStartAndPause();


        } else if (v == btnVideoNext) {
            setPlayNextVideo();

        } else if (v == btnVideoSwichScreen) {
            //切换视频模式
            setVideoMode();
        }
    }

    private void setVideoMode() {
        if(isFullScreen) {
            //默认
            setVideoType(SCREEN_DEFULT);
        } else {
            //全屏
            setVideoType(SCREEN_FULL);
        }

    }

    private void setVideoType(int videoType) {
        switch (videoType) {
            case  SCREEN_DEFULT:
                isFullScreen = false;

                //真实视频的宽和高
                int mVideoWidth = videoWidht;
                int mVideoHeight = videoHeight;

                //屏幕的真实宽和高
                int width  = screenWidth;
                int height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                videoview.setVideoSize(width,height);

                //按钮状态
                btnVideoSwichScreen.setBackgroundResource(R.drawable.btn_video_swich_screen_full_selector);
        
                break;
            case  SCREEN_FULL:
                isFullScreen  = true;
                videoview.setVideoSize(screenWidth,screenHeight);
                //按钮状态
                btnVideoSwichScreen.setBackgroundResource(R.drawable.btn_video_swich_screen_defualt_selector);

                break;
        }
    }

    private void setPlayPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position--;
            if (position >= 0) {
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                setButtonState();
            } else {
                //列表播放完成
                position = 0;
            }
        }

    }

    private void setPlayNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position++;
            if (position < mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                setButtonState();
                if (position == mediaItems.size() - 1) {
                    Toast.makeText(SystemPlayerActivity.this, "播放最后一个视频", Toast.LENGTH_SHORT).show();
                }
            } else {
                position = mediaItems.size() - 1;
                finish();
            }
        } else if (uri != null) {
            finish();
        }
    }

    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //设置上一个和下一个可以点击
            setIsEnableButton(true);
            //如果是第0个，上一个不可以点
            if (position == 0) {
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnVideoPre.setEnabled(false);
            }
            //如果是最后一个，下一个按钮不可以点
            if (position == mediaItems.size() - 1) {
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnVideoNext.setEnabled(false);
            }
        } else if (uri != null) {//只有一个播放地址
            setIsEnableButton(false);
        }
    }

    private void setIsEnableButton(boolean b) {
        //改变图片背景
        if (b) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
        } else {
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
        }
        //设置按钮是否可点击
        btnVideoPre.setEnabled(b);
        btnVideoNext.setEnabled(b);

    }

    private void setStartAndPause() {
        if (videoview.isPlaying()) {
            videoview.pause();
            //按钮状态--播放
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            videoview.start();
            //按钮-暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS:
                    //得到当前进度
                    int currProgress = videoview.getCurrentPosition();
                    seekbarVideo.setProgress(currProgress);

                    //设置时间跟新
                    tvCurrent.setText(utils.stringForTime(currProgress));

                    //得到系统时间
                    tvSystemTime.setText(getSystemTime());
                    //循环发消息
                    removeMessages(PROGRESS);
                    sendEmptyMessage(PROGRESS);

                    break;
                case HIDE_MEDIACONTROLL://隐藏
                    hideMediaController();
                    break;
            }
        }
    };

    private boolean isShowMediaController = false;

    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        isShowMediaController = false;
        llTop.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);
    }

    /**
     * 显示控制面板
     */
    private void showMediaController() {
        isShowMediaController = true;
        llTop.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.VISIBLE);
    }

    /**
     * 得到系统时间
     *
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediacontroller);

        initData();
        findViews();

        getData();
        setListener();
        setData();


        //设置控制面板
//        videoview.setMediaController(new MediaController(this));
    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            Log.e("TAG", "列表有数据");
            //有列表数据
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            videoview.setVideoPath(mediaItem.getData());
        } else if (uri != null) {//文件，第三方应用
            Log.e("TAG", "第三方应用");
            //设置播放地址
            videoview.setVideoURI(uri);

        } else {
            Toast.makeText(SystemPlayerActivity.this, "没有传递数据进入播放器", Toast.LENGTH_SHORT).show();

        }

    }

    private void initData() {
        utils = new Utils();
        //注册广播--监听电量变化的广播
        receiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);

        //2.创建手势识别器
        detector = new GestureDetector(this, new MySimpleOnGestureListener());

        //得到屏幕的宽和高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //得到当前相关音量信息
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        LogUtil.e("currentVolume" + currentVolume);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }

    class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            setStartAndPause();
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            setVideoMode();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isShowMediaController) {
                //隐藏
                hideMediaController();
                //把消息移除
                handler.removeMessages(HIDE_MEDIACONTROLL);
            } else {
                //显示
                showMediaController();
                //发延迟消息
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 3000);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            //主线程
            //显示电量
            setBattery(level);
        }
    }

    /**
     * 显示电量
     *
     * @param level
     */
    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setListener() {
        //设置视频播放的监听：准备好，播放出错，播放完成
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        videoview.setOnErrorListener(new MyOnErrorListener());
        videoview.setOnCompletionListener(new MyOnCompletionListener());
        //设置视频拖动
        seekbarVideo.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        //设置音量的拖动
        seekbarVoice.setOnSeekBarChangeListener(new VolumeOnSeekBarChangeListener());
    }

    public void getData() {
        //视频播放地址-文件-->null
        uri = getIntent().getData();//?
        LogUtil.e("uri---------->" + uri);

        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("medialist");
        Log.e("TAG", "SystemPlayerActivity------------>getData()" + mediaItems);
        position = getIntent().getIntExtra("position", 0);
    }

    class VolumeOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                updateProgreess(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLL);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
        }
    }

    private void updateVolume(int progress) {
        if(isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekbarVoice.setProgress(0);
        }else{
            am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            currentVolume = progress;
            seekbarVoice.setProgress(progress);
        }

    }

    private void updateProgreess(int progress) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        currentVolume = progress;
        seekbarVoice.setProgress(progress);
        isMute = progress <= 0;
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当SeekBar 改变的时候回调这个方法
         *
         * @param seekBar
         * @param progress
         * @param fromUser 自动false,用户操作true
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoview.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLL);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 3000);
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //退出播放器
//            finish();
            setPlayNextVideo();
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemPlayerActivity.this, "播放出错了", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            //得到视频的宽和高
            videoWidht = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            //开始播放
            videoview.start();
            //设置按键是否可被点击
            setButtonState();

            //隐藏控制面板
            hideMediaController();

            //得到视频的总时长
            int duration = videoview.getDuration();
            seekbarVideo.setMax(duration);

            tvDuration.setText(utils.stringForTime(duration));

            handler.sendEmptyMessage(PROGRESS);

            setVideoType(SCREEN_DEFULT);
        }
    }

    private float startY;
    //触摸范围
    private float touchRang;
    //起始音量
    private float currVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {//3.接收事件，传递给手识别器
        detector.onTouchEvent(event);

        //手指上下滑动屏幕实现音量控制
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :

                startY = event.getY();
                touchRang = Math.min(screenWidth,screenHeight);
                currVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(HIDE_MEDIACONTROLL);
        
                break;
            case MotionEvent.ACTION_MOVE :
                float endY = event.getY();
                float distanceY = endY - startY;
                //要改变的声音 = (滑动的距离 / 总距离)*最大音量
                float delta = (distanceY/touchRang)*maxVolume;
                //最终声音 = 原来的声音 + 要改变的声音
                float volume = Math.min(Math.max(delta,0),maxVolume);
                if(delta != 0) {
                    updateVolume((int) delta);
                }

                break;
            case MotionEvent.ACTION_UP :
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL,3000);

                break;
        }
        return super.onTouchEvent(event);
    }

    //监听按手机音量键改变音量
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVolume--;
            updateVolume(currentVolume);
            handler.removeMessages(HIDE_MEDIACONTROLL);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL,3000);
            return true;

        } else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVolume++;
            updateVolume(currentVolume);
            handler.removeMessages(HIDE_MEDIACONTROLL);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL,3000);
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        //先把子类释放
        //移除所有消息
        handler.removeCallbacksAndMessages(null);

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }
}
