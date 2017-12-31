package com.practice.tts;

import com.practice.type.Language;
import org.apache.log4j.Logger;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Tomer
 */
public class GoogleTTSEngine extends AbsTTSEngine {

    private static final Logger logger = Logger.getLogger(NeoSpeechTTSEngine.class);

    @Override
    public String getAudioFileUrl(String source) {
        try {
            String encoded = URLEncoder.encode(source, "UTF-8");
            return "https://translate.google.com/translate_tts?ie=UTF-8&q=" + encoded + "&tl=en&total=1&idx=0&textlen=5&client=t&prev=input";
        } catch (UnsupportedEncodingException e) {
            logger.error("fail to generate TTS for text: " + source, e);
            return null;
        }
    }

    @Override
    protected Language getLanguage() {
        return Language.EN;
    }

    public static void main(String[] args) {
        try{
            String word="car";
            word = URLEncoder.encode(word, "UTF-8");
//            URL url = new URL("https://translate.google.com/translate_tts?ie=UTF-8&q=tomer+ben+dror&tl=en&total=1&idx=0&textlen=5&tk=378614&client=t&prev=input");
            URL url = new URL("http://media.neospeech.com/audio/ws/2015-09-04/e44815375a/result-31440183.wav");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.addRequestProperty("User-Agent", "Mozilla/4.76");
            InputStream audioSrc = urlConn.getInputStream();
            DataInputStream read = new DataInputStream(audioSrc);
            AudioStream as = new AudioStream(read);
            AudioPlayer.player.start(as);
            AudioPlayer.player.stop(as);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected TtsSource getSource() {
        return TtsSource.GOOGLE;
    }
}
