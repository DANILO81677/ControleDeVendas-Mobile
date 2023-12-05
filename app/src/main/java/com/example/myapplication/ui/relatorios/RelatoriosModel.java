package com.example.myapplication.ui.relatorios;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RelatoriosModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RelatoriosModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is RelatoriosModel fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}