package com.practice.tts;

import com.google.api.client.util.IOUtils;
import com.practice.model.TTSRecord;
import com.practice.type.FileMimeType;
import com.practice.type.Language;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Tomer
 */
public abstract class AbsTTSEngine implements TTSEngine {

    private static final Logger logger = Logger.getLogger(AbsTTSEngine.class);

    @Override
    public TTSRecord getAudioContent(String source, Language language) {
        String audioFileUrl = getAudioFileUrl(source);
        logger.debug("get audioFileUrl: " + audioFileUrl);
        if (audioFileUrl != null) {
            byte[] bytes = downloadFileWithRetries(audioFileUrl, 10);
            if (bytes != null) {
                bytes = manipulateFile(bytes);
                return new TTSRecord(source, FileMimeType.MP3, language, getSource(), bytes);
            } else {
                logger.error("fail to download TTS file for [" + source + "]");
            }
        }
        return null;
    }

    private byte[] downloadFileWithRetries(String urlStr, int retries) {
        for (int i = 0; i < retries; i++) {
            byte[] bytes = downloadFile(urlStr);
            if (bytes != null) {
                return bytes;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error("fail to sleep for 100 ms when waiting for TTS audio file to be download");
            }
        }
        return null;
    }

    public byte[] downloadFile(String urlStr) {
        URL url = null;
        try {
            url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream inputStream = conn.getInputStream();
            IOUtils.copy(inputStream, baos);

            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("fail to download file from url: " + url);
            return null;
        }
    }

    public abstract String getAudioFileUrl(String source);
    protected abstract Language getLanguage();
    protected abstract TtsSource getSource();

    protected byte[] manipulateFile(byte[] bytes) {
        return bytes;
    }
}
