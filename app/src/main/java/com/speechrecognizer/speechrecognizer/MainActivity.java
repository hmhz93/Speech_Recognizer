package com.speechrecognizer.speechrecognizer;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    TextView recordedText, translatedText;
    ImageButton mic;
    TextToSpeech t1;
    ImageButton listen;
    Spinner spinnerfrom;
    Spinner spinnerto;

    public static String from,to;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Spinner element

        spinnerfrom = (Spinner) findViewById(R.id.spinnerfrom);

        spinnerto = (Spinner) findViewById(R.id.spinnerto);

        // Spinner click listener
        spinnerfrom.setOnItemSelectedListener(this);
        spinnerto.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> languages = new ArrayList<String>();
        languages.add("English");
        languages.add("French");
        languages.add("German");
        languages.add("Italian");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerfrom.setAdapter(dataAdapter);
        spinnerto.setAdapter(dataAdapter);


        //

        recordedText = (TextView) findViewById(R.id.TVrecordedText);
        translatedText = (TextView) findViewById(R.id.TVtranslatedText);

        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });

        mic = (ImageButton) findViewById(R.id.imageButton);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }

            private void promptSpeechInput() {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                if (from.equals("English")) {
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                }
                if (from.equals("French")) {
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "fr-FR");
                }
                if (from.equals("German")) {
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "de-DE");
                }
                if (from.equals("Italian")) {
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "it-IT");
                }
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");

                try {
                    startActivityForResult(i, 100);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, "Sorry ! Your device doesn't support speech language !", Toast.LENGTH_LONG).show();
                }
            }
        });

        listen = (ImageButton) findViewById(R.id.playbutton);

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listen();

            }

            private void listen() {
                String toSpeak = translatedText.getText().toString();

                    if (to.equals("English")) {t1.setLanguage(Locale.ENGLISH);}
                    if (to.equals("French")) {t1.setLanguage(Locale.FRENCH);}
                    if (to.equals("German")) {t1.setLanguage(Locale.GERMAN);}
                    if (to.equals("Italian")) {t1.setLanguage(Locale.ITALIAN);}

                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                if ((resultCode == RESULT_OK) && (data != null)) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    recordedText.setText(result.get(0));
                    translate(recordedText.getText().toString());
                }
                break;
        }
    }

    private void translate(String word) {

        final String w = word;
        Toast.makeText(MainActivity.this, word, Toast.LENGTH_SHORT).show();


        class Traduction extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Translating...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                translatedText.setText(s);

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Config.KEY_WORD, w);
                params.put(Config.FROM,from);
                params.put(Config.TO,to);



                RequestHandler rh = new RequestHandler();

                String res = rh.sendPostRequest(Config.URL_TRAD, params);

                return res;
            }
        }

        Traduction ae = new Traduction();
        ae.execute();


    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.spinnerfrom) {
            String item = parent.getItemAtPosition(position).toString();
            from = item;
        }
        if (spinner.getId() == R.id.spinnerto) {
            String item = parent.getItemAtPosition(position).toString();
            to = item;
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
