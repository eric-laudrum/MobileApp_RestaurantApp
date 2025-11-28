package com.gb.finddining;

import androidx.fragment.app.Fragment;

public interface NavigationHost {
    void showFragment(Fragment fragment, boolean addToBackstack);
    void navigateToLogin();
    void navigateToSignUp();
    void navigateToHome();
    void navigateToSearch(String query);
    void navigateToDetails(String id);
    void navigateToRate(String id);
    void navigateToAddPhoto(String id);
    void navigateToShare(String id);
    void navigateToAbout();
    void navigateToProfile();
}
