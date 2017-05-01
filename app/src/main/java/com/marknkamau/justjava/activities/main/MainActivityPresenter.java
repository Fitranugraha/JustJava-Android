package com.marknkamau.justjava.activities.main;

import com.marknkamau.justjava.models.DataProvider;

class MainActivityPresenter {
    private MainActivityView activityView;

    MainActivityPresenter(MainActivityView activityView) {
        this.activityView = activityView;
    }

    void getCatalogItems(){
        activityView.displayCatalog(DataProvider.drinksList);
    }
}
