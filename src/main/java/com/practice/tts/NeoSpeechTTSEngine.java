package com.practice.tts;

import com.practice.model.TTSRecord;
import com.practice.type.Language;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomer
 */
public class NeoSpeechTTSEngine extends AbsTTSEngine {

    private static final Logger logger = Logger.getLogger(NeoSpeechTTSEngine.class);

    private String accountEmail = "tomerbd0910@gmail.com";
    private String accountId = "e44815375a";
    private String loginKey = "LoginKey";
    private String loginPassword = "0a852f4b62a6cd6a2119";

    public NeoSpeechTTSEngine() {
    }

    @Override
    public String getAudioFileUrl(String source) {
        String conversionNumber = getConversionNumber(source);
        if (conversionNumber != null) {
            return getAudioUrl(conversionNumber);
        }
        return null;
    }

    private String getAudioUrl(String conversionNumber) {
        String getAudioUrlStr = "https://tts.neospeech.com/rest_1_1.php?method=GetConversionStatus&email=" + accountEmail +
                "&accountId=" + accountId + "&conversionNumber=" + conversionNumber;
        URL getAudioUrl;
        try {
            getAudioUrl = new URL(getAudioUrlStr);
        } catch (MalformedURLException e) {
            String message = "error while trying to call GetConversionStatus URL";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
        URLConnection urlConnection;
        StringBuffer b;
        try {
            urlConnection = getAudioUrl.openConnection();
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            b = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                b.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            String message = "Unable to connect to NeoSpeech GetConversionStatus API";
            logger.error(message, e);
            throw new RuntimeException(message , e);
        }
        String responseStr = b.toString();
        if (responseStr.contains("resultString=\"success\"")) {
            Pattern p = Pattern.compile("downloadUrl=\\\"(.*)\\\"");
            Matcher m = p.matcher(responseStr);
            if (m.find()) {
                return m.group(1);
            }
        }
        throw new RuntimeException(responseStr);
    }

    private String getConversionNumber(String sourceText) {
        // todo generate the url only once after properties set
        // todo use url encoder for the sourceText
        URL convertSimpleUrl;
        try {
            String convertSimpleUrlStr = "https://tts.neospeech.com/rest_1_1.php?method=ConvertSimple&email=" + accountEmail +
                    "&accountId=" + accountId + "&loginKey=" + loginKey + "&loginPassword=" + loginPassword + "&voice=TTS_PAUL_DB%20&outputFormat=FORMAT_WAV&" +
                    "sampleRate=16&text=" + URLEncoder.encode(sourceText, "UTF-8");
            convertSimpleUrl = new URL(convertSimpleUrlStr);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            String message = "error while trying to create convertSimple URL";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
        URLConnection urlConnection;
        StringBuffer b;
        try {
            urlConnection = convertSimpleUrl.openConnection();
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            b = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                b.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            String message = "Unable to connect to NeoSpeech convertSimple API";
            logger.error(message, e);
            throw new RuntimeException(message , e);
        }

        String responseStr = b.toString();
        if (responseStr.contains("resultString=\"success\"")) {
            Pattern p = Pattern.compile("conversionNumber=\\\"([\\d]+)\\\"");
            Matcher m = p.matcher(responseStr);
            if (m.find()) {
                return m.group(1);
            }
        }
        throw new RuntimeException(responseStr);
    }

    @Override
    protected Language getLanguage() {
        return Language.EN;
    }

    public static void main(String[] args) {
        TTSRecord audio = new NeoSpeechTTSEngine().getAudioContent("my home page", Language.EN);
        System.out.println(audio);
    }

    @Override
    protected TtsSource getSource() {
        return TtsSource.NEO_SPEECH;
    }
}
