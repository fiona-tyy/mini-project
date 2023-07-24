package tfip.project.appbackend.configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebaseMessageConfig {

    @Value("${GOOGLE_CREDENTIALS_JSON}")
    private String jsonCredentials;

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    FirebaseApp firebaseApp() throws IOException {
        
        FirebaseApp firebaseApp;
        if(FirebaseApp.getApps().isEmpty()){
            byte[] jsonBytes = jsonCredentials.getBytes();
            InputStream serviceAccount = new ByteArrayInputStream(jsonBytes);
            firebaseApp = FirebaseApp.initializeApp(
                            FirebaseOptions.builder()
                                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                    .build()
            );
   
        } else {
            firebaseApp = FirebaseApp.getApps().get(0);
        }
        return firebaseApp;
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

}
