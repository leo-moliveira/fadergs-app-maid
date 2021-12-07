package br.com.hotelfadergs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static String URL_HOTEL = "http://ec2-18-228-11-151.sa-east-1.compute.amazonaws.com";
    private EditText user;
    private EditText pass;
    private Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = (EditText)findViewById(R.id.editTextTextPersonName);
        pass = (EditText)findViewById(R.id.editTextTextPassword);
        btLogin = (Button)findViewById(R.id.button);

        this.btLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL hotelEndpoint = new URL(URL_HOTEL+"/api/auth/login");

                    Log.d("URL", hotelEndpoint.toString());

                    HttpURLConnection connection = (HttpURLConnection) hotelEndpoint.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();

                    jsonParam.put("user_name", user.getText().toString().trim());
                    jsonParam.put("password", pass.getText().toString().trim());

                    Log.d("JSON", jsonParam.toString());

                    DataOutputStream os = new DataOutputStream(connection.getOutputStream());

                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();

                    Log.d("STATUS", String.valueOf(connection.getResponseCode()));
                    Log.d("MSG" , connection.getResponseMessage());

                    //Log.d("RESPONSE", connection.getInputStream().toString());

                    InputStream responseBody = connection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");

                    JsonReader jsonReader = new JsonReader(responseBodyReader);

                    //jsonReader.beginArray();
                    jsonReader.beginObject();
                    //Log.d("RESPONSE", jsonReader.toString());
                    //while(jsonReader.hasNext()){
                    String key = jsonReader.nextName();
                    if(key.equals("token")){
                        TokenSaver.setTokenKey(MainActivity.this, jsonReader.nextString());
                        Intent intent = new Intent(MainActivity.this, CleaningList.class);
                        startActivity(intent);
                        finish();
                    }else{
                        connectionErrorMessage("usuário ou senha estão incorretos");
                    }

                    connection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void connectionErrorMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
            }
        });
    }
}