package com.example.civireports;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class TokenManager {
    private static final String PREF_NAME = "secure_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private final SharedPreferences securePrefs;

    public TokenManager(Context context){
        try{
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            securePrefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e){
            throw  new RuntimeException("Failed to initialize TokenManager", e);
        }
    }
    public void saveToken(String token){
        securePrefs.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }
    public String getToken(){
        return securePrefs.getString(KEY_ACCESS_TOKEN, null);
    }
    public boolean hasToken(){
        return getToken() != null;
    }
    public void clearToken(){
        securePrefs.edit().remove(KEY_ACCESS_TOKEN).apply();
    }
}
