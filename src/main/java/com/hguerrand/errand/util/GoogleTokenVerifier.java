package com.hguerrand.errand.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

public class GoogleTokenVerifier {

    private static final String CLIENT_ID = "936294527684-ppn525f19crtp39da5te6kl73q2p19gs.apps.googleusercontent.com";

    public static String verifyAndGetEmail(String token) {
        try {
            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(
                            new NetHttpTransport(),
                            JacksonFactory.getDefaultInstance()
                    )
                            .setAudience(Collections.singletonList(CLIENT_ID))
                            .build();

            GoogleIdToken idToken = verifier.verify(token);
            if (idToken == null) return null;

            return idToken.getPayload().getEmail();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
