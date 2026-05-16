package com.nhom2.learnenglish.feature.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.nhom2.learnenglish.R;

public class OnboardingFragment1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            return inflater.inflate(R.layout.activity_onboarding_1, container, false);
        } catch (Exception e) {
            e.printStackTrace();
            // Return empty view if layout fails
            return new android.widget.TextView(container.getContext());
        }
    }
}