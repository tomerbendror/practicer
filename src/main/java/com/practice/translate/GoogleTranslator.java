package com.practice.translate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.practice.type.Language;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tomer
 */
@PropertySource("classpath:META-INF/spring/tts.properties")
public class GoogleTranslator implements Translator {

    private static final Logger logger = Logger.getLogger(GoogleTranslator.class);

    @Value("${apiKey}")
    private String apiKey;

    @Value("${baseUrl}")
    private String baseUrl;

    @Override
    public List<String> translate(String text, Language source, Language target) {
        StringBuilder result = new StringBuilder();
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlStr = baseUrl + apiKey + "&q=" + encodedText + "&source=" + source + "&target=" + target;

            URL url = new URL(urlStr);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            InputStream stream;
            if (conn.getResponseCode() == 200) {
                stream = conn.getInputStream();
            } else
                stream = conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result.toString());

            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.get("error") == null) {
                    String translation =  obj.get("data").getAsJsonObject().
                            get("translations").getAsJsonArray().
                            get(0).getAsJsonObject().
                            get("translatedText").getAsString();
                    if (StringUtils.isBlank(translation)) { // just in case we couldn't get translation- (for example 'A')
                        translation =  text.trim();
                    }
                    return Collections.singletonList(translation);
                }
            }

            if (conn.getResponseCode() != 200) {
                String errorMsg = element.getAsJsonObject().get("error").getAsJsonObject().get("errors").getAsJsonArray().get(0).getAsJsonObject().get("message").toString();
                logger.error("fail to translate '" + text + "' using google translate, errorMsg: " + errorMsg);
            }

        } catch (IOException | JsonSyntaxException e) {
            logger.error("fail to translate '" + text + "' using google translate", e);
        }
        return null;
    }

    public static void main(String[] args) {
        List<String> translate = new GoogleTranslator().translate("my name is tomer", Language.EN, Language.HE);
        System.out.println(translate.get(0));
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
