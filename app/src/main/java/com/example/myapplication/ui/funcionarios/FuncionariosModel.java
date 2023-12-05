package com.example.myapplication.ui.funcionarios;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FuncionariosModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FuncionariosModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is FuncionariosModel fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

