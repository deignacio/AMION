package com.delauneconsulting.AMION;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

        // set title
        this.setTitle("AMION - " + pwd);

        // Set the click listener for the btnWhosOnCall
        OnClickListener btnWhosOnCallListener = new OnClickListener() {
            public void onClick(View v) {
                PerformOnCallSearch();
            }
        };
        btnWhosOnCall.setOnClickListener(btnWhosOnCallListener);

        // Set the click listener for the btnWhosOnBprCoverage
        OnClickListener btnWhosOnBprCoverageListener = new OnClickListener() {
            public void onClick(View v) {
                PerformBprCoverageSearch();
            }
        };
        btnWhosOnBprCoverage.setOnClickListener(btnWhosOnBprCoverageListener);
    }

    private void PerformOnCallSearch() {
        try {

            // String origUrl =
            // "http://www.amion.com/cgi-bin/ocs?Lo=%s&Rpt=619";
            // String response =
            // Helper.getHttpResponseAsString(String.format(origUrl, pwd));

            // prepend the password to the response string, so you can see it on
            // the next screen.
            // response = pwd + " | " + response;

            Intent intent = new Intent(getApplication(), ResultList.class);
            // intent.setData(Uri.parse(response));
            intent.putExtra("pwd", pwd);
            intent.putExtra("filter", "OnCall");
            startActivity(intent);

        } catch (Exception e) {
        }
    }

    private void PerformBprCoverageSearch() {
        try {

            String origUrl = "http://www.amion.com/cgi-bin/ocs?Lo=%s&Rpt=619";
            String response = Helper.getHttpResponseAsString(String.format(origUrl, pwd));

            // prepend the password to the response string, so you can see it on
            // the next screen.
            // response = pwd + " | " + response;

            Intent intent = new Intent(getApplication(), ResultList.class);
            intent.setData(Uri.parse(response));
            intent.putExtra("pwd", pwd);
            intent.putExtra("filter", "BprCoverage");
            startActivity(intent);

        } catch (Exception e) {
        }
    }

}
