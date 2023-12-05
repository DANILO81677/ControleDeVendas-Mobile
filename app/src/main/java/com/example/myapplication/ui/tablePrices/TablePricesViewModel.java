package com.example.myapplication.ui.tablePrices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TablePricesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TablePricesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is TablePricesViewModel fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
