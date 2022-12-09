package com.example.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Objects;

public class translateActivity<extras> extends AppCompatActivity {

    TextView txtFromLang;
    TextView txtTranslated;
    ImageView imgLangFrom;
    ImageView imgLangTo;
    Spinner languageSpinner;
    Button btnTranslate;
    Button btnReturn;
    Translator tr;
    EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        //bind to user input by id
        editText = findViewById(R.id.edInput);

        //bind return button
        btnReturn = findViewById(R.id.btnReturn);

        //bind a func to it to return to the main page
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(translateActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        //bind to text by id
        txtTranslated = findViewById(R.id.txtOutput);

        //bind translate button by id and create onClick for it
        btnTranslate = findViewById(R.id.btnTranslate);

        //get from text display id
        txtFromLang = findViewById(R.id.txtBoxLang);

        //hide app name bar
        getSupportActionBar().hide();

        //get context of selected from lang
        Bundle extras = getIntent().getExtras();
        String fromLang = "";
        if (extras != null) {
            fromLang = extras.getString("lang");
        }

        //initialize translator by default
        TranslatorOptions to = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.fromLanguageTag(fromLang))
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();
        tr = Translation.getClient(to);

        //display flag of selected from lang
        imgLangFrom = findViewById(R.id.imgFlagFrom);
        imgLangFrom.setImageResource(getResources().getIdentifier(fromLang, "drawable", getPackageName()));

        //display full name of from lang
        switch(fromLang){
            case "en":
                txtFromLang.setText("English");
                break;
            case "fr":
                txtFromLang.setText("French");
                break;
            case "it":
                txtFromLang.setText("Italian");
                break;
            case "ru":
                txtFromLang.setText("Russian");
                break;
            case "es":
                txtFromLang.setText("Spanish");
                break;
            case "pt":
                txtFromLang.setText("Portuguese");
                break;
        }

        //bind spinner and fill it with possible languages
        languageSpinner = findViewById(R.id.spinLang);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("English");
        arrayList.add("French");
        arrayList.add("Italian");
        arrayList.add("Russian");
        arrayList.add("Spanish");
        arrayList.add("Portuguese");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(arrayAdapter);

        //change flag on selected item change
        imgLangTo = findViewById(R.id.imgFlagTo);

        String finalFromLang = fromLang;
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String toLang = parent.getItemAtPosition(position).toString();
                switch(toLang){
                    case "English":
                        toLang = "en";
                        break;
                    case "French":
                        toLang = "fr";
                        break;
                    case "Italian":
                        toLang = "it";
                        break;
                    case "Russian":
                        toLang = "ru";
                        break;
                    case "Spanish":
                        toLang = "es";
                        break;
                    case "Portuguese":
                        toLang = "pt";
                        break;
                }
                imgLangTo.setImageResource(getResources().getIdentifier(toLang, "drawable", getPackageName()));

                //change translator to/from
                tr = createTranslator(finalFromLang, toLang);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //translate if ready!
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepTrans(tr);
            }
        });
    }

    public Translator createTranslator(String fromLang, String toLang){
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.fromLanguageTag(fromLang))
                .setTargetLanguage(TranslateLanguage.fromLanguageTag(toLang))
                .build();

        final Translator genTranslator = Translation.getClient(options);

        return genTranslator;
    }

    public void prepTrans(Translator t){
        //check if models are already avaliable
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        t.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //model downloaded successfully
                                //so we can now translate
                                translation(t);
                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //model couldn't be downloaded or other problem
                                //display error messoge
                            }
                        }
                );
    }

    private void translation(Translator t){
        t.translate(editText.getText().toString())
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                //translation successful
                                txtTranslated.setText(s);
                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //translation failed
                            }
                        }
                );
    }

}