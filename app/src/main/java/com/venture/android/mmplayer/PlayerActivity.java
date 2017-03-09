package com.venture.android.mmplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.venture.android.mmplayer.domain.Music;

import java.util.List;

import static com.venture.android.mmplayer.App.PAUSE;
import static com.venture.android.mmplayer.App.PLAY;
import static com.venture.android.mmplayer.App.STOP;
import static com.venture.android.mmplayer.App.playStatus;
import static com.venture.android.mmplayer.MusicService.position;

public class PlayerActivity extends AppCompatActivity implements ControlInterface {
    // 음악 데이터
    List<Music> data;
    // 뷰페이저
    ViewPager viewPager;
    PlayerAdapter adapter;
    // 위젯
    ImageButton btnRew, btnPlay, btnFf;
    SeekBar seekBar;
    TextView txtDuration, txtProgress;
    //String list_type = "";

    Controller controller;

    Intent intent = null;

    /**
     * PlayActivity onCreate 메소드
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        intent = new Intent(this, MusicService.class);

        mediaButtonInit();
        seekBarInit();
        viewPagerInit();
        callSelectPage();
    }

    private void settingInit(){
        playStatus = STOP;
        // 볼륨 조절 버튼으로 미디어 음량만 조절하기 위한 설정
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void mediaButtonInit() {
        btnRew      = (ImageButton) findViewById(R.id.btnRew);
        btnPlay     = (ImageButton) findViewById(R.id.btnPlay);
        btnFf       = (ImageButton) findViewById(R.id.btnFf);

        btnPlay.setOnClickListener(clickListener);
        btnRew.setOnClickListener(clickListener);
        btnFf.setOnClickListener(clickListener);
    }

    private void seekBarInit() {
        seekBar     = (SeekBar)  findViewById(R.id.seekBar);
        //seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtProgress = (TextView) findViewById(R.id.txtProgress);
    }

    private void viewPagerInit() {
        // 0. 데이터 가져오기
        data = DataLoader.getMusics(this);
        // 1. 뷰 페이저 가져오기
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        // 2. 뷰 페이저용 아답터 생성
        PlayerAdapter adapter = new PlayerAdapter(data, this);
        // 3. 뷰페이저 아답터 연결
        viewPager.setAdapter(adapter);
        // 4. 뷰페이지 리스너 연결 (페이지가 바뀌는 경우를 위한 리스너)
        viewPager.addOnPageChangeListener(viewPagerListener);

        //viewPager.setPageTransformer(false, pageTransformer);
    }

    private void callSelectPage() {
        // 5. 특정 페이지 호출
        Intent intent = getIntent();
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            MusicService.listType = bundle.getString(ListFragment.ARG_LIST_TYPE);
            MusicService.position = bundle.getInt(ListFragment.ARG_POSITION);

            // 첫페이지일 경우만 init 호출
            // 이유 : 첫페이지가 아닐경우 위의 setCurrentItem 에 의해서 ViewPager의 onPageSelected가 호출된다.
            if(MusicService.position == 0) {
                init();
            } else {
                viewPager.setCurrentItem(position);
            }
        }
        controller = Controller.getInstance();
        controller.addObserver(this);

    }


    /**
     * PlayerActivity에서 MediaPlayer를 사용하기 위한 초기화 메소드
     */
    private void init () {
        //playerInit();
        controllerInit();
    }

//    private void playerInit() {
//        //서비스로 이관
//    }

    private void controllerInit(){
        Music music = data.get(MusicService.position);
        txtProgress.setText("0");
        txtDuration.setText(music.getDurationText());
        Log.i("controllerInit","=========="+music.getDurationText());
        seekBar.setMax(music.getDuration());
        seekBar.setProgress(0);
    }


    /**
     *  btnPlay 선택시
     */
    private void play() {
        Intent intent = new Intent(this, MusicService.class);
        if(playStatus==PLAY) {
            intent.setAction(MusicService.ACTION_PAUSE);
            pausePlayer();
        } else {
            intent.setAction(MusicService.ACTION_PLAY);
            startPlayer();
        }
        intent.putExtra(ListFragment.ARG_POSITION, MusicService.position);
        intent.putExtra(ListFragment.ARG_LIST_TYPE,MusicService.listType);
        startService(intent);
    }

    public void startPlayer(){
        playStatus = PLAY;
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
    }

    public void pausePlayer(){
        playStatus = PAUSE;
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void stopPlayer() {
        playStatus = STOP;
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
    }


    /**
     * btnRew 선택시
     */
    private void prev() {

    }


    /**
     * btnFf 선택시
     */
    private void next() {
        Intent intent = new Intent(this, MusicService.class);
        if(playStatus==PLAY) {
            intent.setAction(MusicService.ACTION_STOP);
            stopPlayer();
        }
        startService(intent);
    }

    /**
     *  PlayerActivity에서 발생하는 이벤트
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnPlay:
                    play();
                    break;
                case R.id.btnRew:
                    prev();
                    break;
                case R.id.btnFf:
                    next();
                    break;
            }
        }
    };


    @Override
    protected void onDestroy(){
        controller.remove(this);
        super.onDestroy();
    }

    /**
     * ViewPageChangeListener
     */
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            MusicService.position = position;
            init();
            Log.i("onPageSelected","============="+ MusicService.position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };


//    /**
//     * seekBarChangeListener
//     */
//    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if(player != null && fromUser)  {
//                player.seekTo(progress);
//            }
//        }
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//
//        }
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//
//        }
//    };
//
//    ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
//        @Override
//        public void transformPage(View page, float position) {
//            float normalizedposition = Math.abs( 1 - Math.abs(position) );
//            page.setAlpha(normalizedposition);  //View의 투명도 조절
//            page.setScaleX(normalizedposition/2 + 0.5f); //View의 x축 크기조절
//            page.setScaleY(normalizedposition/2 + 0.5f); //View의 y축 크기조절
//            page.setRotationY(position * 80); //View의 Y축(세로축) 회전 각도
//        }
//    };
}
