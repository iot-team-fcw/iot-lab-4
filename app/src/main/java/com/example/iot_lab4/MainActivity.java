package com.example.iot_lab4;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button speechButton;
    EditText speechText;
    Button button_on;
    Button button_off;

    private static final int RECOGNIZER_RESULT = 1;

    protected boolean sendPost(String urla, String name, String value) {
        try {
            URL url = new URL(urla + "?" + name + "=" + value);

            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1000 * 5); // mTimeout is in seconds
            urlc.connect();

            if (urlc.getResponseCode() == 200) {
                return new Boolean(true);
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        speechButton = findViewById(R.id.speechButton);
        speechText = findViewById(R.id.speechText);
        button_on = findViewById(R.id.button_on);
        button_off = findViewById(R.id.button_off);

        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to Text");
                startActivityForResult(speechIntent, RECOGNIZER_RESULT);
            }
        });

        button_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPost("http://35f107597f62.ngrok.io/", "led", "off");
            }
        });

        button_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPost("http://35f107597f62.ngrok.io/", "led", "on");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        speechText.setText(matches.get(0).toString());

        super.onActivityResult(requestCode, resultCode, data);
    }
}