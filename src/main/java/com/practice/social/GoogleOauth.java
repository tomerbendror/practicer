package com.practice.social;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.util.ServiceException;
import com.practice.etc.OAuthSession;
import org.apache.commons.lang.StringUtils;
import org.brickred.socialauth.Contact;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by Tomer
 */
@Component
@PropertySource("classpath:META-INF/spring/social.properties")
public class GoogleOauth {

    @Value("${google.clientId}")
    private String clientId;

    @Value("${google.clientSecret}")
    private String clientSecret;

    @Value("${google.userInfoUrl}")
    private String userInfoUrl;

    @Value("${google.contactUrl}")
    private String contactUrl;

    public GoogleAuthorizationCodeFlow getGoogleFlow(FlowType type) {
        Collection<String> scope;
        switch (type){
            case PROFILE:
                scope = Collections.singletonList("https://www.googleapis.com/auth/userinfo.profile");
                break;
            case EMAIL:
                scope = Collections.singletonList("https://www.googleapis.com/auth/userinfo.email");
                break;
            case CONTACTS:
                scope = Collections.singletonList("https://www.google.com/m8/feeds/");
                break;
            default:
                scope = Collections.singletonList("https://www.googleapis.com/auth/userinfo.profile");
        }
        return new GoogleAuthorizationCodeFlow(new NetHttpTransport(), new JacksonFactory(), clientId, clientSecret, scope);
    }

    /**
     * Produce the URI that will be sent back to the browser for google authentication
     */
    public String getAuthUrl(FlowType flowType, OAuthSession oAuthSession) {
        GoogleAuthorizationCodeFlow flow = getGoogleFlow(flowType);
        GoogleAuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl();
        return authorizationUrl.setRedirectUri(oAuthSession.getRedirectUri()).build();
    }

    /**
     * Should be called after authentication
     */
    public Map<String, String> getUserProfilePropMap(String code, OAuthSession oAuthSession) throws IOException, ServiceException {
        GoogleAuthorizationCodeFlow flow = getGoogleFlow(FlowType.EMAIL);
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(oAuthSession.getRedirectUri()).execute();
        Credential credential = flow.createAndStoreCredential(response, null);
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(credential);
        // Make an authenticated request
        GenericUrl url = new GenericUrl(userInfoUrl);
        HttpRequest request = requestFactory.buildGetRequest(url);
        request.getHeaders().setContentType(MediaType.APPLICATION_JSON_VALUE);
        String jsonIdentity = request.execute().parseAsString();
        JsonParser jsonParser = new JacksonFactory().createJsonParser(jsonIdentity);
        @SuppressWarnings("unchecked")
        Map<String, String> userProfile = jsonParser.parse(HashMap.class);
        return userProfile;
    }

    public List<Contact> getContactList(String code, OAuthSession oAuthSessionD) throws IOException, ServiceException {
        GoogleAuthorizationCodeFlow flow = getGoogleFlow(FlowType.CONTACTS);
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(oAuthSessionD.getRedirectUri()).execute();
        Credential credential = flow.createAndStoreCredential(response, null);
        ContactsService service = new ContactsService("practicer");
        service.setOAuth2Credentials(credential);
        Query query = new Query(new URL(contactUrl));
        query.setMaxResults(500);
        ContactFeed contactFeed = service.getFeed(query, ContactFeed.class);
        List<Contact> contactList = new ArrayList<>();
        for (ContactEntry entry : contactFeed.getEntries()){
            List<String> emailList = new ArrayList<>();
            for (Email email : entry.getEmailAddresses()){
                if (StringUtils.isNotBlank(email.getAddress())){
                    emailList.add(email.getAddress());
                }
            }
            if (emailList.isEmpty()){
                continue;
            }
            Contact contact = new Contact();
            contact.setDisplayName(entry.getTitle().getPlainText());
            contact.setEmail(emailList.get(0));
            if (emailList.size() > 1) {
                contact.setOtherEmails(emailList.subList(1, emailList.size()).toArray(new String[emailList.size() -1]));
            }
            contactList.add(contact);
        }
        return contactList;
    }


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public enum FlowType{
        PROFILE, EMAIL, CONTACTS
    }
}