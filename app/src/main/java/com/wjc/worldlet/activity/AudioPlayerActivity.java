package com.wjc.worldlet.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wjc.worldlet.IMusicPlayerService;
import com.wjc.worldlet.R;
import com.wjc.worldlet.service.MusicPlayerService;
import com.wjc.worldlet.utils.LogUtil;
import com.wjc.worldlet.utils.Utils;

public class AudioPlayerActivity extends Activity implements View.OnClickListener {

    /**
     * 更新进度
     */
    private static final int PROGRESS = 1;
    private ImageView iv_icon;

    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioPlayPause;
    private Button btnAudioNext;
    private Button btnLyric;
    /**
     * 音频列表中的位置
     */
    private int position;
    private IMusicPlayerService service;
    private MyReceiver receiver;
    private Utils utils;
    private ServiceConnection con = new ServiceConnection() {

        /**
         * 当连接服务成功的时候回调这个方法
         * 要得到服务的代理类
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            LogUtil.e("音频服务连接了------------------------------");
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if (service != null) {
                try {
                    if (!from) {
                        //来自列表
                        service.openAudio(position);
                    } else {
                        //来自状态栏
                        service.notifyChange(MusicPlayerService.OPEN_COMPLETE);
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 当断开服务的时候回调这个方法
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS://更新进度

                    try {
                        //更新Seekbar的进度
                        int currentPosition = service.getCurrentPosition();

                        seekbarAudio.setProgress(currentPosition);


                        //更新文本时间
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);


                    break;
            }
        }
    };
    //false:来自列表，true,来自状态栏
    private boolean from = false;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-09-10 11:45:39 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_audio_player);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_icon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable drawable = (AnimationDrawable) iv_icon.getBackground();
        drawable.start();
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlaymode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioPlayPause = (Button) findViewById(R.id.btn_audio_play_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnLyric = (Button) findViewById(R.id.btn_lyric);

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioPlayPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnLyric.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-09-10 11:45:39 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnAudioPlaymode) {
            // Handle clicks for btnAudioPlaymode
            changePlaymode();
        } else if (v == btnAudioPre) {
            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnAudioPre
        } else if (v == btnAudioPlayPause) {
            // Handle clicks for btnAudioPlayPause
            try {
                if (service.isPlaying()) {
                    //暂停
                    service.pause();
                    //按钮状态-播放
                    btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_play_selector);
                } else {
                    //播放
                    service.start();
                    //按钮状态-暂停
                    btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }


            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnAudioNext) {
            // Handle clicks for btnAudioNext
            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnLyric) {
            // Handle clicks for btnLyric
        }
    }

    private void changePlaymode() {
        try {
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayerService.REPEAT_NOMAL) {
                playmode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                playmode = MusicPlayerService.REPEAT_ALL;
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                playmode = MusicPlayerService.REPEAT_NOMAL;
            } else {
                playmode = MusicPlayerService.REPEAT_NOMAL;
            }

            service.setPlaymode(playmode);//保持到Service里面

            //显示按钮的状态
            showPlaymode(true);


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlaymode(boolean isShowToast) {
        int playmode = 0;
        try {
            playmode = service.getPlaymode();

            if (playmode == MusicPlayerService.REPEAT_NOMAL) {
                //设置按钮的背景
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                if (isShowToast) {
                    Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                }

                //tost
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                if (isShowToast) {
                    Toast.makeText(AudioPlayerActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                }

            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                if (isShowToast) {
                    Toast.makeText(AudioPlayerActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
                }
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                if(isShowToast) {
                    Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        bindAndStartService();
        setListener();

    }

    private void setListener() {
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private void initData() {
        //注册监听广播
        receiver = new MyReceiver();
        IntentFilter intenFilter = new IntentFilter();
        intenFilter.addAction(MusicPlayerService.OPEN_COMPLETE);
        registerReceiver(receiver, intenFilter);

        utils = new Utils();
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MusicPlayerService.OPEN_COMPLETE)) {
                //显示演唱者，歌曲的名称
                showData();

                try {
                    int duration = service.getDuration();
                    seekbarAudio.setMax(duration);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                showPlaymode(false);
                //发消息到Handler，开始跟新进度
                handler.sendEmptyMessage(PROGRESS);
            }
        }


    }

    private void showData() {
        if (service != null) {
            try {
                tvArtist.setText(service.getArtist());
                tvName.setText(service.getName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 绑定方式启动服务
     */
    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.wjc.worldlet.OPENAUDIO");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void getData() {
        from = getIntent().getBooleanExtra("Notification", false);
        if (!from) {
            position = getIntent().getIntExtra("position", 0);
        }

    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        if (con != null) {
            unbindService(con);
            con = null;
        }
        super.onDestroy();

    }
}
