package com.venture.android.mmplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.venture.android.mmplayer.domain.Artist;
import com.venture.android.mmplayer.domain.Music;

import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    private static List<Music> musicData = new ArrayList<>();
    private static List<Artist> artistData = new ArrayList<>();
    //private static List<Genre> genreData = new ArrayList<>();

    /**
     * static 변수인 musicdata 를 체크해서 null이면 loadMusic메소드를 실행
     * @param context
     * @return List<Music>
     */
    public static List<Music> getMusics(Context context){
        if(musicData == null || musicData.size() == 0){
            loadMusic(context);
        }
        return musicData;
    }

    /**
     * static 변수인 artistdata 를 체크해서 널이면 loadArtist메소드를 실행
     * @param context
     * @return List<Artist>
     */
    public static List<Artist> getArtist(Context context){
        if(artistData == null || artistData.size() == 0){
            loadArtist(context);
        }
        return artistData;
    }

    /**
     * loadMusic 함수는 getMusic메소드를 통해서만 접근한다.
     * @param context
     */
    private static void loadMusic(Context context) {

        // 1. 데이터 컨텐츠 URI 정의
        final Uri URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;


        // 2. 데이터에서 가져올 데이터 컬럼명을 String 배열에 담는다.
        //    데이터컬럼명은 Content Uri의 패키지에 들어있다.
        final String PROJ[] = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_KEY,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.YEAR,
        };

        // 1. 데이터에 접근하기위해 ContentResolver 를 불러온다.
        ContentResolver resolver = context.getContentResolver();

        // 2. Content Resolver 로 쿼리한 데이터를 Cursor 에 담는다.
        Cursor cursor = resolver.query(URI, PROJ, null, null, null);

        // 3. Cursor 에 담긴 데이터를 반복문을 돌면서 꺼낸다
        if(cursor != null) {
            while(cursor.moveToNext()){
                Music music = new Music();
                // 데이터
                music.id              = getInt(cursor, PROJ[0]);
                music.album_id        = getInt(cursor, PROJ[1]);
                music.title           = getString(cursor, PROJ[2]);
                music.artist_id       = getInt(cursor, PROJ[3]);
                music.artist          = getString(cursor, PROJ[3]);
                music.artist_key      = getString(cursor, PROJ[5]);
                music.duration        = getInt(cursor,PROJ[6]);
                music.is_music        = getString(cursor, PROJ[7]);
                music.composer        = getString(cursor, PROJ[8]);
                music.year            = getString(cursor, PROJ[9]);

                music.music_uri       = getMusicUri(music.id);
                music.album_image_uri = getAlbumImageSimple(music.album_id);

                musicData.add(music);
            }
            // 4. 처리후 커서를 닫아준다
            cursor.close();
        }
    }

    // loadMusic 함수는 get 함수를 통해서만 접근한다.
    private static void loadArtist(Context context) {
        final Uri URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

        final String PROJ[] = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                MediaStore.Audio.Artists.ARTIST_KEY,
        };

        // 1. 데이터에 접근하기위해 ContentResolver 를 불러온다.
        ContentResolver resolver = context.getContentResolver();

        // 2. Content Resolver 로 쿼리한 데이터를 Cursor 에 담는다.
        Cursor cursor = resolver.query(URI, PROJ, null, null, null);

        // 3. Cursor 에 담긴 데이터를 반복문을 돌면서 꺼낸다
        if(cursor != null) {
            while(cursor.moveToNext()){
                Artist artist = new Artist();
                // 데이터
                artist.id = getInt(cursor, PROJ[0]);
                artist.artist = getString(cursor, PROJ[1]);
                artist.artist_key = getString(cursor, PROJ[2]);
                artist.number_of_albums = getInt(cursor, PROJ[3]);
                artist.number_of_tracks = getInt(cursor, PROJ[4]);

                artist.album_id = (artist.id);
                artist.album_image_uri = getAlbumUriByArtistId(artist.id);

                artistData.add(artist);
            }
            // 4. 처리후 커서를 닫아준다
            cursor.close();
        }
    }

    public int getAlbumIdByArtistId(int artist_id){
        for(Music music : musicData){
            if(music.artist_id == artist_id){
                return music.album_id;
            }
        }
        return -1;
    }

    public static Uri getAlbumUriByArtistId(int artist_id){
        for(Music music : musicData){
            if(music.artist_id == artist_id){
                return music.album_image_uri;
            }
        }
        return null;
    }

    private static String getGenre(){
        // MediaStore.Audio.Genres.getContentUriForAudioId();
        return "";
    }

    private static String getString(Cursor cursor, String columnName){
        int idx = cursor.getColumnIndex(columnName);
        return cursor.getString(idx);
    }

    //
    private static int getInt(Cursor cursor, String columnName){
        int idx = cursor.getColumnIndex(columnName);
        return cursor.getInt(idx);
    }

    // music id를 받아 음악 uri를 가져오는 메소드
    private static Uri getMusicUri(int music_id) {
        Uri content_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return Uri.withAppendedPath(content_uri,music_id+"");
    }

    // album id를 받아서 앨범 Uri 생성
    private static Uri getAlbumImageSimple(int album_id) {
        return Uri.parse("content://media/external/audio/albumart/" + album_id);
    }

}


