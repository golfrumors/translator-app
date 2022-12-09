package com.example.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView img_english;
    ImageView img_french;
    ImageView img_italian;
    ImageView img_russian;
    ImageView img_spanish;
    ImageView img_brazilian;

    Button btnDownload;
    Button buttonQuit;

    private RemoteModelManager modelManager;

    MutableLiveData<List<String>> availableModels = new MutableLiveData<>();

    HashMap<String, Task<Void>> pendingDownloads = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modelManager = RemoteModelManager.getInstance();

        //bind quit button
        buttonQuit = findViewById(R.id.btnQuit);
        buttonQuit.setOnClickListener(this);

        //bind download model button by id
        btnDownload = findViewById(R.id.btnModels);

        //hide app name bar
        getSupportActionBar().hide();

        //get image views & create their on click listeners
        img_english = findViewById(R.id.imgEN);
        img_english.setOnClickListener(this);

        img_french = findViewById(R.id.imgFR);
        img_french.setOnClickListener(this);

        img_italian = findViewById(R.id.imgIT);
        img_italian.setOnClickListener(this);

        img_russian = findViewById(R.id.imgRU);
        img_russian.setOnClickListener(this);

        img_spanish = findViewById(R.id.imgSP);
        img_spanish.setOnClickListener(this);

        img_brazilian = findViewById(R.id.imgBR);
        img_brazilian.setOnClickListener(this);

        //download models on click
        btnDownload.setOnClickListener(view -> {
            fetchDownloadedModels();
            String[] languages = {"en", "es", "fr", "it", "pt", "ru"};

            for(int i = 0; i < 6; i++){
                Language tmp = new Language(languages[i]);
                downloadLanguage(tmp);
            }
        });
    }

    private TranslateRemoteModel getModel(String languageCode) {
        return new TranslateRemoteModel.Builder(languageCode).build();
    }

    void fetchDownloadedModels(){
        modelManager
                .getDownloadedModels(TranslateRemoteModel.class)
                .addOnSuccessListener(
                        remoteModels -> {
                            List<String> modelCodes = new ArrayList<>(remoteModels.size());
                            for(TranslateRemoteModel model : remoteModels){
                                modelCodes.add(model.getLanguage());
                            }

                            Collections.sort(modelCodes);
                            availableModels.setValue(modelCodes);
                        }
                );
    }

    void downloadLanguage(Language language){
        TranslateRemoteModel model = getModel(TranslateLanguage.fromLanguageTag(language.getCode()));
        Task<Void> downloadTask;

        if(pendingDownloads.containsKey(language.code)){
            downloadTask = pendingDownloads.get(language.code);
            //found existing task
            if(downloadTask != null && !downloadTask.isCanceled()){
                return;
            }
        }

        downloadTask = modelManager
                .download(model, new DownloadConditions.Builder().build())
                .addOnCompleteListener(
                        task -> {
                            pendingDownloads.remove(language.getCode());
                            fetchDownloadedModels();
                        }
                );
        pendingDownloads.put(language.code, downloadTask);

    }


    private void goToTranslateView(String value){
        Intent i = new Intent(MainActivity.this, translateActivity.class);
        i.putExtra("lang", value);
        startActivity(i);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgEN:
                goToTranslateView("en");
                break;

            case R.id.imgFR:
                goToTranslateView("fr");
                break;

            case R.id.imgIT:
                goToTranslateView("it");
                break;

            case R.id.imgRU:
                goToTranslateView("ru");
                break;

            case R.id.imgSP:
                goToTranslateView("sp");
                break;

            case R.id.imgBR:
                goToTranslateView("pt");
                break;

            case R.id.btnQuit:
                finish();
                System.exit(0);
                break;

            default:
                break;
        }
    }

    static class Language implements Comparable<Language>{
        private final String code;

        Language(String code){
            this.code = code;
        }

        String getDisplayName(){
            return new Locale(code).getDisplayName();
        }

        String getCode(){
            return code;
        }

        @Override
        public boolean equals(Object o){
            if(o == this){
                return true;
            }

            if(!(o instanceof Language)){
                return false;
            }

            Language otherLang = (Language) o;
            return otherLang.code.equals(code);
        }

        @NonNull
        @Override
        public String toString(){
            return code + " - " + getDisplayName();
        }

        public int hashCode(){
            return code.hashCode();
        }

        @Override
        public int compareTo(Language o) {
            return this.getDisplayName().compareTo(o.getDisplayName());
        }
    }

}