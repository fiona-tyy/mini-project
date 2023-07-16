importScripts("https://www.gstatic.com/firebasejs/8.10.0/firebase-app.js");
importScripts(
  "https://www.gstatic.com/firebasejs/8.10.0/firebase-messaging.js"
);

firebase.initializeApp({
  apiKey: "AIzaSyDrMHBLgNt7dtFiUX9eZK5KceC7rpLJDps",
  authDomain: "turnkey-guild-390403.firebaseapp.com",
  projectId: "turnkey-guild-390403",
  storageBucket: "turnkey-guild-390403.appspot.com",
  messagingSenderId: "623486988411",
  appId: "1:623486988411:web:ea22194f0e74f4202b72de",
  measurementId: "G-DM9Y86MMS7",
});

const isSupported = firebase.messaging.isSupported();
if (isSupported) {
  const messaging = firebase.messaging();

  messaging.onBackgroundMessage(function (payload) {
    console.log("Received background message ", payload);

    const notificationTitle = payload.notification.title;
    const notificationOptions = {
      body: payload.notification.body,
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
  });
}
