package com.practice.social;

import com.practice.etc.OAuthSession;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tomer
 */
@Component
@PropertySource("classpath:META-INF/spring/social.properties")
public class FacebookOauth {

    private static final Logger logger = Logger.getLogger(FacebookOauth.class);
    private static final String PROFILE_IMAGE_URL = "http://graph.facebook.com/%1$s/picture";

    @Value("${facebook.clientId}")
    private String clientId;

    @Value("${facebook.clientSecret}")
    private String clientSecret;

    @Value("${facebook.fields}")
    private String fbFields;

    public String getFBAuthUrl(OAuthSession oAuthSession) {
        String fbLoginUrl = "";
        try {
            fbLoginUrl = "http://www.facebook.com/dialog/oauth?client_id=" + clientId + "&redirect_uri=" + URLEncoder.encode(oAuthSession.getRedirectUri(), "UTF-8") + "&scope=email";
        } catch (UnsupportedEncodingException e) {
            logger.error("fail to get facebook auth url", e);
        }
        return fbLoginUrl;
    }

    public String getFBGraphUrl(String code, String redirectUrl) {
        String fbGraphUrl = "";
        try {
            fbGraphUrl = "https://graph.facebook.com/oauth/access_token?"
                    + "client_id=" + clientId + "&redirect_uri="
                    + URLEncoder.encode(redirectUrl, "UTF-8")
                    + "&client_secret=" + clientSecret + "&code=" + code;
        } catch (UnsupportedEncodingException e) {
            logger.error("fail to get facebook graph url", e);
        }
        return fbGraphUrl;
    }

    public String getAccessToken(String code, OAuthSession oAuthSession) {
        URL fbGraphURL;
        try {
            fbGraphURL = new URL(getFBGraphUrl(code, oAuthSession.getRedirectUri()));
        } catch (MalformedURLException e) {
            logger.error("Invalid code received", e);
            throw new RuntimeException("Invalid code received " + e);
        }
        URLConnection fbConnection;
        StringBuffer b;
        try {
            fbConnection = fbGraphURL.openConnection();
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(
                    fbConnection.getInputStream()));
            String inputLine;
            b = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                b.append(inputLine).append("\n");
            }
            in.close();
        } catch (IOException e) {
            logger.error("Unable to connect with Facebook", e);
            throw new RuntimeException("Unable to connect with Facebook " + e);
        }

        String accessToken = b.toString();
        if (accessToken.startsWith("{")) {
            logger.error("Unable to connect with Facebook");
            throw new RuntimeException("ERROR: Access Token Invalid: " + accessToken);
        }

        return accessToken;
    }

    public String getFBGraph(String accessToken) {
        String graph;
        try {
            String g = "https://graph.facebook.com/me?fields=" + URLEncoder.encode(fbFields, "UTF-8") + "&" + accessToken;
            URL u = new URL(g);
            URLConnection c = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String inputLine;
            StringBuilder b = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                b.append(inputLine).append("\n");
            }
            in.close();
            graph = b.toString();
            System.out.println(graph);
        } catch (Exception e) {
            logger.error("ERROR in getting FB graph data.", e);
            throw new RuntimeException("ERROR in getting FB graph data. " + e);
        }
        return graph;
    }

    public Map<String, String> getUserProfilePropMap(String code, OAuthSession oAuthSession) {
        String accessToken = getAccessToken(code, oAuthSession);
        String fbGraph = getFBGraph(accessToken);

        Map<String, String> fbProfile = new HashMap<>();
        try {
            JSONObject json = new JSONObject(fbGraph);
            fbProfile.put("id", json.getString("id"));
            if (json.has("name")) {
                fbProfile.put("name", json.getString("name"));
            }
            if (json.has("first_name")) {
                fbProfile.put("first_name", json.getString("first_name"));
            }
            if (json.has("last_name")) {
                fbProfile.put("last_name", json.getString("last_name"));
            }
            if (json.has("email")) {
                fbProfile.put("email", json.getString("email"));
            }
            if (json.has("gender")) {
                fbProfile.put("gender", json.getString("gender"));
            }
            fbProfile.put("profile_image_url", String.format(PROFILE_IMAGE_URL, json.getString("id")));
        } catch (JSONException e) {
            logger.error("ERROR in parsing FB graph data.", e);
            throw new RuntimeException("ERROR in parsing FB graph data. " + e);
        }
        return fbProfile;
    }
}
