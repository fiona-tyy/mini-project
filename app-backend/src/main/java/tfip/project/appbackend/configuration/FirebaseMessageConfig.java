package tfip.project.appbackend.configuration;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebaseMessageConfig {
    @Bean
    FirebaseApp initFirebaseApp() throws IOException {
        // FirebaseOptions options=null;
        // try {
        //     options = FirebaseOptions.builder()
        //       .setCredentials(GoogleCredentials.getApplicationDefault())
        //       .build();
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

        FirebaseApp firebase = FirebaseApp.initializeApp(FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.getApplicationDefault())
                                .build());

        return firebase;
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
    
}
