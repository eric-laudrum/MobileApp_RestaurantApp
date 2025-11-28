package com.gb.finddining.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gb.finddining.NavigationHost;
import com.gb.finddining.R;
import com.gb.finddining.auth.AuthManager;

public class SplashFragment extends Fragment {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable navigateToLogin = () -> {
        FragmentActivity activity = getActivity();
        if (activity instanceof NavigationHost navigationHost) {
            // Avoid committing after state has been saved (e.g. when app is backgrounded)
            if (!activity.isFinishing()
                    && !activity.getSupportFragmentManager().isStateSaved()
                    && isAdded()) {
                if (AuthManager.isLoggedIn(activity)) {
                    navigationHost.navigateToHome();
                } else {
                    navigationHost.navigateToLogin();
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(navigateToLogin, 5000);
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(navigateToLogin);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(navigateToLogin);
        super.onDestroyView();
    }
}
