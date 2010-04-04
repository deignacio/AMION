package com.delauneconsulting.AMION;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AMIONPrefs extends Activity {

    EditText txtUsername;
    EditText txtPassword;
    Button btnSavePrefs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs);

        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnSavePrefs = (Button) findViewById(R.id.btnSavePrefs);

        SharedPreferences settings = getSharedPreferences(AMION.PREFS_NAME, 0);

        txtUsername.setText(settings.getString("pagingUsername", ""));
        txtPassword.setText(settings.getString("pagingPassword", ""));

        OnClickListener btnSavePrefsListener = new OnClickListener() {
            public void onClick(View v) {
                Editable username = txtUsername.getText();
                Editable pwd = txtPassword.getText();

                SharedPreferences settings = getSharedPreferences(AMION.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("pagingUsername", username.toString());
                editor.putString("pagingPassword", pwd.toString());
                editor.commit();

                Toast.makeText(getApplicationContext(), "Preferences Saved!", Toast.LENGTH_LONG)
                        .show();
            }
        };
        btnSavePrefs.setOnClickListener(btnSavePrefsListener);
    }
}
