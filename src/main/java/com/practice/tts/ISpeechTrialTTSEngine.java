package com.practice.tts;

import com.google.api.client.util.IOUtils;
import com.google.code.mp3fenge.Mp3Fenge;
import com.practice.model.TTSRecord;
import com.practice.type.Language;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Tomer
 */
public class ISpeechTrialTTSEngine extends AbsTTSEngine {

    private static final Logger logger = Logger.getLogger(ISpeechTrialTTSEngine.class);

    private Nonce nonce = new Nonce();

    @Override
    public String getAudioFileUrl(String source) {
        try {
            String encoded = URLEncoder.encode(source, "UTF-8");
            return "https://www.ispeech.org/p/generic/getaudio?text=" + encoded + "&voice=usenglishfemale&speed=-3&action=convert&nonce=" + nonce.getValue();
        } catch (UnsupportedEncodingException e) {
            logger.error("fail to generate TTS for text: " + source, e);
            return null;
        }
    }

    @Override
    protected byte[] manipulateFile(byte[] bytes) {
        try {
            Path inPath = Files.createTempFile("tts_in" , ".tts");
            Files.write(inPath, bytes);

            File inFile = inPath.toFile();
            Mp3Fenge mp3Fenge = new Mp3Fenge(inFile);
            MP3File e = new MP3File(inFile);
            MP3AudioHeader header = (MP3AudioHeader)e.getAudioHeader();
            Path outPath = Files.createTempFile("tts_out", ".tts");
            mp3Fenge.generateNewMp3ByTime(outPath.toFile(), 0, (header.getTrackLength() * 1000) -2100);

            byte[] outStream = Files.readAllBytes(outPath);
            Files.delete(inPath);
            Files.delete(outPath);
            return outStream;
        } catch (Exception e) {
            logger.error("fail to crop the iSpeech TTS file", e);
            throw new RuntimeException("fail to crop the iSpeech TTS file", e);
        }
    }

    @Override
    protected Language getLanguage() {
        return Language.EN;
    }

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        ISpeechTrialTTSEngine iSpeechTrialTTSEngine = new ISpeechTrialTTSEngine();

        TTSRecord audioContent = iSpeechTrialTTSEngine.getAudioContent("hit", Language.EN);
        Files.write(Paths.get("C:\\Users\\Owner\\Downloads\\hit.mp3"), audioContent.getContent());

//        byte[] bytes = iSpeechTrialTTSEngine.downloadFile("https://www.ispeech.org/p/generic/getaudio?text=yoav+is+very+good+child&voice=usenglishfemale&speed=-3&action=convert&nonce=7612e6d0dc575a4fcd157d3430445db0");
//        System.out.println(bytes);
    }

    @Override
    protected TtsSource getSource() {
        return TtsSource.ISPEECH_TRIAL;
    }

    private static class Nonce {
        private String value;
        private long time;
        private long timeToLive = 1000 * 60 * 60 * 12;

        public String getValue() {
            if ((System.currentTimeMillis() - time) > timeToLive) {
                value = getNewNonce();
                time = System.currentTimeMillis();
            }
            return value;
        }

        private String getNewNonce() {
            URL url = null;
            try {
                url = new URL("http://www.ispeech.org/html/tts-demo.php?module=p&class=generic&function=tts");
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.connect();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream inputStream = conn.getInputStream();
                IOUtils.copy(inputStream, baos);

                String content = new String(baos.toByteArray());
                int nIndex = content.indexOf("nonce:");
                int nonceStart = content.indexOf("\"", nIndex);
                int nonceEnd = content.indexOf("\"", nIndex + 10);   // "nonce:" + some buffer
                return content.substring(nonceStart + 1, nonceEnd);
            } catch (Exception e) {
                logger.error("fail to get the nonce from iSpeech, url: " + url);
                return null;
            }
        }

    }
}
