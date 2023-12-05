package com.example.myapplication.ui.despezas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DespezasModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DespezasModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is DespezasModel fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
