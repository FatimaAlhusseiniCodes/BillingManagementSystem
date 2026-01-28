package com.example.billingmanagementsystem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class PaymentViewModel extends ViewModel {
    private final MutableLiveData<List<payment>> paymentsLiveData = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<payment>> getPayments() {
        return paymentsLiveData;
    }

    public void addPayment(payment p) {
        List<payment> currentList = paymentsLiveData.getValue();
        if (currentList != null) {
            currentList.add(0, p);
            paymentsLiveData.setValue(currentList);
        }
    }
}
