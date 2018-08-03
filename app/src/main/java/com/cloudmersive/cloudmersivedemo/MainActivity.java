package com.cloudmersive.cloudmersivedemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.cloudmersive.client.invoker.*;
import com.cloudmersive.client.invoker.auth.*;
import com.cloudmersive.client.ConvertDocumentApi;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    static MainActivity main;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        main = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private static void downloadFile(String url, File outputFile) {
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch(FileNotFoundException e) {
            return; // swallow a 404
        } catch (IOException e) {
            return; // swallow a 404
        }
    }

    private class DownloadFilesTask extends AsyncTask<Long, Integer, Long>
    {
        @Override
        protected Long doInBackground(Long... input)
        {
            String path = "";

            try {
                File outputDir = main.getCacheDir(); // context being the Activity pointer
                File outputFile = File.createTempFile("prefix", "docx", outputDir);

                downloadFile("https://cdn.cloudmersive.com/demo/input.docx", outputFile);

                path = outputFile.getPath();
            }
            catch (Exception ex)
            {
                System.out.println(ex.toString());
            }


            ApiClient defaultClient =    Configuration.getDefaultApiClient();

// Configure API key authorization: Apikey
            ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
            Apikey.setApiKey("71b067cf-1d07-474d-9403-1d0e53ca3da4");
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//Apikey.setApiKeyPrefix("Token");

            ConvertDocumentApi apiInstance = new ConvertDocumentApi();
            File inputFile = new File(path); // File | Input file to perform the operation on.
            try {
                byte[] result = apiInstance.convertDocumentDocxToPdf(inputFile);
                System.out.println(result);
            } catch (ApiException e) {
                System.err.println("Exception when calling ConvertDocumentApi#convertDocumentDocxToPdf");
                e.printStackTrace();
            }

            return 0L;
        }

        @Override
        protected void onProgressUpdate(Integer... progress)
        {

        }

        @Override
        protected void onPostExecute(Long result) {

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        new DownloadFilesTask().execute(0L);


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
