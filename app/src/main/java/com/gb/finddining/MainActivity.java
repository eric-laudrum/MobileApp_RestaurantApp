package com.gb.finddining;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;

import com.gb.finddining.ui.SplashFragment;
import com.google.android.material.navigation.NavigationView;
import com.gb.finddining.auth.AuthManager;

public class MainActivity extends AppCompatActivity implements NavigationHost {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private View fragmentContainer;
    private int defaultTopMargin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.top_app_bar);
        fragmentContainer = findViewById(R.id.fragment_container);
        setSupportActionBar(toolbar);
        ImageButton openDrawerButton = findViewById(R.id.button_open_drawer);
        openDrawerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        if (fragmentContainer != null && fragmentContainer.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) fragmentContainer.getLayoutParams();
            defaultTopMargin = lp.topMargin;
        }

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        if (savedInstanceState == null) {
            showFragment(new SplashFragment(), false);
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            showFragment(new HomeFragment(), false);
        } else if (itemId == R.id.nav_profile) {
            navigateToProfile();
        } else if (itemId == R.id.nav_about) {
            navigateToAbout();
        } else if (itemId == R.id.nav_logout) {
            logoutToLogin();
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public void showFragment(Fragment fragment, boolean addToBackstack) {
        updateToolbarVisibility(fragment);
        if (addToBackstack) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void navigateToLogin() {
        showFragment(new LoginFragment(), false);
    }

    @Override
    public void navigateToSignUp() {
        showFragment(new SignUpFragment(), true);
    }

    @Override
    public void navigateToHome() {
        showFragment(new HomeFragment(), true);
    }

    @Override
    public void navigateToSearch(String query) {
        showFragment(SearchResultsFragment.newInstance(query), true);
    }

    @Override
    public void navigateToDetails(String id) {
        showFragment(RestaurantDetailsFragment.newInstance(id), true);
    }

    @Override
    public void navigateToRate(String id) {
        showFragment(RateReviewFragment.newInstance(id), true);
    }

    @Override
    public void navigateToAddPhoto(String id) {
        showFragment(AddPhotoFragment.newInstance(id), true);
    }

    @Override
    public void navigateToShare(String id) {
        showFragment(ShareFragment.newInstance(id), true);
    }

    @Override
    public void navigateToAbout() {
        showFragment(new AboutFragment(), true);
    }

    @Override
    public void navigateToProfile() {
        showFragment(new ProfileFragment(), true);
    }

    private void updateToolbarVisibility(Fragment fragment) {
        if (toolbar == null) return;
        boolean hideToolbar = fragment instanceof LoginFragment
                || fragment instanceof SignUpFragment
                || fragment instanceof com.gb.finddining.ui.SplashFragment;
        toolbar.setVisibility(hideToolbar ? View.GONE : View.VISIBLE);
        if (!hideToolbar) {
            boolean showBack = !(fragment instanceof HomeFragment);
            if (showBack) {
                toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
                toolbar.setNavigationOnClickListener(v -> {
                    showFragment(new HomeFragment(), false);
                    drawerLayout.closeDrawer(GravityCompat.END);
                });
            } else {
                toolbar.setNavigationIcon((android.graphics.drawable.Drawable) null);
                toolbar.setNavigationOnClickListener(null);
            }
        } else {
            toolbar.setNavigationIcon((android.graphics.drawable.Drawable) null);
            toolbar.setNavigationOnClickListener(null);
        }
        if (fragmentContainer != null && fragmentContainer.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) fragmentContainer.getLayoutParams();
            lp.topMargin = hideToolbar ? 0 : defaultTopMargin;
            fragmentContainer.setLayoutParams(lp);
        }
    }

    private void logoutToLogin() {
        AuthManager.logout(this);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        navigateToLogin();
    }
}
