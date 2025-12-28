package com.example.billingmanagementsystem;
import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InvoiceViewModel extends AndroidViewModel {

    // Holds a partner's name and ID together
    public static class Partner {
        public final int id;
        public final String name;

        public Partner(int id, String name) {
            this.id = id;
            this.name = name;
        }

        // The dropdown menu will display the result of this method
        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }

    private static final String API_URL = "YOUR_API_ENDPOINT_HERE"; // Replace with your actual API endpoint
    private final RequestQueue requestQueue;

    private final MutableLiveData<List<Partner>> partners = new MutableLiveData<>();
    private final MutableLiveData<Boolean> invoiceSubmissionStatus = new MutableLiveData<>();

    public final String[] invoiceTypes = {"Sales", "Purchase"};

    public InvoiceViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
    }

    public LiveData<List<Partner>> getPartners() {
        return partners;
    }

    public LiveData<Boolean> getInvoiceSubmissionStatus() {
        return invoiceSubmissionStatus;
    }

    // Load partner list from API (mock example)
    public void fetchPartners() {
        // TODO: Replace with a real Volley API call to fetch partners
        List<Partner> partnerList = new ArrayList<>();
        partnerList.add(new Partner(1, "Partner A"));
        partnerList.add(new Partner(2, "Partner B"));
        partners.setValue(partnerList);
    }

    // Submit invoice to the server
    public void submitInvoice(String type, int partnerId, String date, String amount, String notes) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("partner_id", partnerId);
            json.put("date", date);
            json.put("amount", amount);
            json.put("notes", notes);
        } catch (JSONException e) {
            Log.e("InvoiceViewModel", "Error creating JSON for invoice", e);
            invoiceSubmissionStatus.setValue(false); // Notify observer of failure
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL, json,
                response -> {
                    Log.d("InvoiceViewModel", "Invoice submitted successfully");
                    invoiceSubmissionStatus.setValue(true); // Success
                },
                error -> {
                    Log.e("InvoiceViewModel", "Error submitting invoice", error);
                    invoiceSubmissionStatus.setValue(false); // Failure
                }
        );

        requestQueue.add(request);
    }
}
