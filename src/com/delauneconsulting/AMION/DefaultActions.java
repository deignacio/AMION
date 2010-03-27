package com.delauneconsulting.AMION;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DefaultActions extends Activity {

    Context context = this;

    private Button btnWhosOnCall;
    private Button btnWhosOnBprCoverage;
    String pwd = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defaultactions);

        // get the controls
        btnWhosOnCall = (Button) findViewById(R.id.btnWhosOnCall);
        btnWhosOnBprCoverage = (Button) findViewById(R.id.btnWhosOnBprCoverage);

        // get the password from the intent
        Intent intent = getIntent();
        pwd = intent.getDataString();

        AMIONReport amionReport = AMION.reports.get(pwd);

        // This is a meant to be a generic List page, so you can set the title
        // to whatever here.
        this.setTitle(amionReport.getDefaultTitle());

        // Set the click listener for the btnWhosOnCall
        OnClickListener btnWhosOnCallListener = new OnClickListener() {
            public void onClick(View v) {
                performSearch("OnCall");
            }
        };
        btnWhosOnCall.setOnClickListener(btnWhosOnCallListener);

        // Set the click listener for the btnWhosOnBprCoverage
        OnClickListener btnWhosOnBprCoverageListener = new OnClickListener() {
            public void onClick(View v) {
                performSearch("BprCoverage");
            }
        };
        btnWhosOnBprCoverage.setOnClickListener(btnWhosOnBprCoverageListener);
    }

    private void performSearch(String filter) {
        try {
            Intent intent = new Intent(getApplication(), ResultList.class);
            intent.putExtra("pwd", pwd);
            intent.putExtra("filter", filter);
            startActivity(intent);
        } catch (Exception e) {
        }
    }
}
