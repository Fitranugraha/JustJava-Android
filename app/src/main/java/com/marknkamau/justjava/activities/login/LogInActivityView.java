package com.marknkamau.justjava.activities.login;

public interface LogInActivityView {
    void closeActivity();
    void signIn();
    void saveUserDefaults(String name, String phone, String deliveryAddress);
    void resetUserPassword();
    void displayMessage(String message);
    void showDialog();
    void dismissDialog();
    void finishSignUp();
}
