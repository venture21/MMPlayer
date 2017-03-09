package com.venture.android.mmplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.venture.android.mmplayer.domain.Music;

import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getService;

public class MusicService extends Service implements ControlInterface {

    private static final String TAG = "MusicService";
    private static final int NOTIFICATION_ID = 1;

    public static final String ACTION_PLAY ="action_play";
    public static final String ACTION_PAUSE ="action_pause";
    public static final String ACTION_NEXT ="action_next";
    public static final String ACTION_PREVIOUS ="action_previous";
    public static final String ACTION_STOP ="action_stop";

    //NotificationCompat.Builder builder;

    private MediaPlayer mMediaPlayer = null;
    public static String listType = "";
    public static int position = -1;

    List<Music> data = new ArrayList<>();
    Controller controller;

    public MusicService(){
        controller = Controller.getInstance();
        controller.addObserver(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null) {
            if(intent.getExtras()!=null) {
                Log.i("onStartCommand","====="+startId);
                listType = intent.getExtras().getString(ListFragment.ARG_LIST_TYPE);
                position = intent.getExtras().getInt(ListFragment.ARG_POSITION);
                if(mMediaPlayer == null) {
                    initMedia();
                }
            }
        }

        handleAction(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMedia() {

        if(data.size()<1){
            switch(listType){
                case ListFragment.TYPE_SONG:
                    data = DataLoader.getMusics(getBaseContext());
                    break;
                case ListFragment.TYPE_ARTIST:
                    break;
            }
        }
        Uri musicUri = data.get(position).music_uri;
        mMediaPlayer = MediaPlayer.create(this, musicUri);
        mMediaPlayer.setLooping(false);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });

    }

    private void handleAction( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;
        String action = intent.getAction();
        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            controller.play();
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            controller.pause();
        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {

        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
            controller.next();
        } else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
            controller.stop();

        }
    }

    // Notification.Action -> API Level 19
    // Activity에서 클릭 버튼 생성
    private NotificationCompat.Action generateAction(int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), MusicService.class );
        intent.setAction( intentAction );
        // PendingIntent : 실행 대상이 되는 인텐트를 지연시키는 역할
        PendingIntent pendingIntent = getService(getApplicationContext(), 1, intent, 0);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }

    private void buildNotification( NotificationCompat.Action action, String action_flag ) {
        Music music = data.get(position);

        // Notification Bar 전체를 클릭했을 때 실행되는 메인 intent
        Intent intentStop = new Intent( getApplicationContext(), MusicService.class );
        intentStop.setAction( ACTION_STOP );
        PendingIntent stopIntent = getService(getApplicationContext(), 1, intentStop, 0);

        // Notification Bar 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this );

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(music.getTitle())
                .setContentText(music.getArtist());

        builder.setDeleteIntent(stopIntent);
        builder.setOngoing(false);

        builder.addAction(generateAction(android.R.drawable.ic_media_previous, "PREV", ACTION_PREVIOUS));
        builder.addAction(action);
        builder.addAction(generateAction(android.R.drawable.ic_media_next, "NEXT", ACTION_NEXT));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 노티바를 화면에 보여준다
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void playerStart(){
        // 노티피케이션 바 생성
        buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ), ACTION_PAUSE );
        mMediaPlayer.start();
    }

    private void playerPause(){
        buildNotification( generateAction( android.R.drawable.ic_media_play, "Play", ACTION_PLAY ), ACTION_PLAY );
            mMediaPlayer.pause();
    }

    private void playerStop(){
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel( NOTIFICATION_ID );
        Intent intent = new Intent( getApplicationContext(), MusicService.class );
        stopService( intent );
    }


    @Override
    public void startPlayer() {
        playerStart();
    }

    @Override
    public void pausePlayer() {
        playerPause();
    }

    @Override
    public void stopPlayer() {
        playerStop();
    }

    @Override
    public void onDestroy() {
        controller.remove(this);
        super.onDestroy();
    }
}
