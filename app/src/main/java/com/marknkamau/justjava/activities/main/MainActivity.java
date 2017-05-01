package com.marknkamau.justjava.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marknkamau.justjava.BuildConfig;
import com.marknkamau.justjava.R;
import com.marknkamau.justjava.adapters.CatalogAdapter;
import com.marknkamau.justjava.activities.cart.CartActivity;
import com.marknkamau.justjava.models.CoffeeDrink;
import com.marknkamau.justjava.models.DataProvider;
import com.marknkamau.justjava.utils.MenuActions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, MainActivityView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_catalog)
    RecyclerView recyclerView;
    @BindView(R.id.fab_cart)
    FloatingActionButton fabCart;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scroll Down
                    if (fabCart.isShown()) {
                        fabCart.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!fabCart.isShown()) {
                        fabCart.show();
                    }
                }
            }
        });

        presenter = new MainActivityPresenter(this);
        presenter.getCatalogItems();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!fabCart.isShown()) {
            fabCart.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        user = firebaseAuth.getCurrentUser();
        MenuInflater inflater = getMenuInflater();
        if (user == null) {
            inflater.inflate(R.menu.toolbar_menu, menu);
        } else {
            inflater.inflate(R.menu.toolbar_menu_logged_in, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log_in:
                MenuActions.ActionLogIn(this);
                return true;
            case R.id.menu_log_out:
                MenuActions.ActionLogOut(this);
                return true;
            case R.id.menu_profile:
                MenuActions.ActionProfile(this);
                return true;
            case R.id.menu_about:
                MenuActions.ActionAbout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        invalidateOptionsMenu();
    }

    @OnClick(R.id.fab_cart)
    public void onClick() {
        startActivity(new Intent(MainActivity.this, CartActivity.class));
    }

    @Override
    public void displayCatalog(List<CoffeeDrink> drinkList) {
        recyclerView.setAdapter(new CatalogAdapter(this, DataProvider.drinksList));
    }
}
