package com.practice.tts;

import com.practice.model.TTSRecord;
import com.practice.type.Language;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Tomer
 */
public class AlmaReaderTTSEngine extends AbsTTSEngine {

    private static final Logger logger = Logger.getLogger(ISpeechTrialTTSEngine.class);

    private static final String TTS_FLAVOR = "Sivan";

    @Override
    public String getAudioFileUrl(String source) {
        URL getAudioUrl;
        try {
            String encoded = URLEncoder.encode(source, "UTF-8");
            String referer = URLEncoder.encode("http://www.aisrael.org/?CategoryID=2764&ArticleID=45083", "UTF-8");
            String getAudioUrlStr = "http://www.almagu5.com/webreader?referer=" + referer + "&cid=CID&markup=" + encoded + "&preferredOnly=false";
            getAudioUrl = new URL(getAudioUrlStr);
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            logger.error("error while trying to generate AlmaReader getHash URL", e);
            return null;
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
            logger.error("Unable to connect to AlmaReader getHash API", e);
            return null;
        }
        String responseStr = b.toString();
        try {
            JSONObject json = new JSONObject(responseStr);
            if (json.has("Status") && json.getInt("Status") == 0) {
                String phraseHash = json.getString("PhraseHash");
                return "http://www.almagu5.com/webreader.audio/CID_" + phraseHash + "_s_" + TTS_FLAVOR + ".mp3";
            } else {
                logger.error("fail to parse the json got from the server: " + responseStr);
                return null;
            }
        } catch (JSONException e) {
            logger.error("fail to parse the json got from the server: " + responseStr, e);
            return null;
        }
    }

    @Override
    protected Language getLanguage() {
        return Language.HE;
    }

    @Override
    protected TtsSource getSource() {
        return TtsSource.ALMA_READER;
    }

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        AlmaReaderTTSEngine almaReaderTTSEngine = new AlmaReaderTTSEngine();
        TTSRecord audioContent = almaReaderTTSEngine.getAudioContent("ירון הוא ילד מאוד בוגר", Language.HE);
        Files.write(Paths.get("C:\\Users\\Owner\\Downloads\\alma.mp3"), audioContent.getContent());
    }
}
