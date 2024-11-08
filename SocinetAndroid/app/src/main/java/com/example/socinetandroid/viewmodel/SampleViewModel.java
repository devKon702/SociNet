package com.example.socinetandroid.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SampleViewModel extends ViewModel {
    private MutableLiveData<String> textLiveData;

    public SampleViewModel() {
        this.textLiveData = new MutableLiveData<>();
        textLiveData.setValue("");
    }

    public MutableLiveData<String> getTextLiveData() {
        return textLiveData;
    }

    public void setTextLiveData(String text) {
        this.textLiveData.setValue(text);
    }
}
