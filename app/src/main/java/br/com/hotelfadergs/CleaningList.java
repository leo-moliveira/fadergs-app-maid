package br.com.hotelfadergs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CleaningList extends AppCompatActivity {

    private static String URL_HOTEL = "http://ec2-18-228-11-151.sa-east-1.compute.amazonaws.com";
    private static String PUT_TO_CLEAN = "/api/cleaning/"; // deve estra logado como gerente / muda status para limpar
    private static String PUT_TO_CLEANING = "/api/cleaning/start/"; // muda status para limpando
    private static String PUT_TO_CLEAN_COMPLETE = "/api/cleaning/completed/"; // muda status para limpo
    private List<CleaningRoom> cleaningRoomList;
    private ArrayAdapter<CleaningRoom> adapter;
    private ListView listaClean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaning_list);
        this.cleaningRoomList = new ArrayList<>();

        listaClean = (ListView)findViewById(R.id.lista);
        adapter = new ArrayAdapter<CleaningRoom>(this, android.R.layout.simple_list_item_1, cleaningRoomList);
        listaClean.setAdapter(adapter);

        listaClean.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CleaningRoom cr = (CleaningRoom) adapterView.getItemAtPosition(i);
                Log.d("ITEM LISTA", cr.toString());
                if(cr.status.equals("to-clean"))
                {
                    startCleaning(cr.id);
                }
                else if(cr.status.equals("cleaning"))
                {
                    cleaningFinished(cr.id);
                }
                getCleaningList();
            }
        });
        //Teste();
        //startCleaning(3);
        //cleaningFinished(2);
        getCleaningList();
    }

    public void Teste() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL hotelEndpoint = new URL(URL_HOTEL + "/api/cleaning/2");

                    Log.d("URL", hotelEndpoint.toString());

                    HttpURLConnection connection = (HttpURLConnection) hotelEndpoint.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("Authorization", "Bearer "+TokenSaver.getTokenKey(CleaningList.this));
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("room_number", 102);
                    jsonParam.put("manager_id", 2);
                    jsonParam.put("employee_id", 3);
                    jsonParam.put("status", "clean");
                    jsonParam.put("cleaning_date", "2021-11-02 18:14:18");
                    //jsonParam.put("description", "2 beds and 1 bathroom");

                    Log.d("JSON", jsonParam.toString());

                    DataOutputStream os = new DataOutputStream(connection.getOutputStream());

                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();

                    Log.d("STATUS", String.valueOf(connection.getResponseCode()));
                    Log.d("MSG", connection.getResponseMessage());

                    Log.d("RESPONSE", connection.getInputStream().toString());
                    InputStream responseBody = connection.getInputStream();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseBody, "UTF-8"));
                    String line = "";

                    StringBuilder responseStrBuilder = new StringBuilder();
                    while((line = bufferedReader.readLine()) != null)
                    {
                        responseStrBuilder.append(line);
                    }
                    responseBody.close();

                    JSONObject jObj = new JSONObject(responseStrBuilder.toString());


                    Log.d("RESPONSE", jObj.toString());

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

    private void cleaningFinished(int id){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL hotelEndpoint = new URL(URL_HOTEL+PUT_TO_CLEAN_COMPLETE+id);

                    Log.d("URL", hotelEndpoint.toString());

                    HttpURLConnection connection = (HttpURLConnection) hotelEndpoint.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("Authorization", "Bearer "+TokenSaver.getTokenKey(CleaningList.this));
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    Log.d("STATUS", String.valueOf(connection.getResponseCode()));
                    Log.d("MSG", connection.getResponseMessage());

                    Log.d("RESPONSE", connection.getInputStream().toString());

                    connection.disconnect();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startCleaning(int id){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL hotelEndpoint = new URL(URL_HOTEL+PUT_TO_CLEANING+id);

                    Log.d("URL", hotelEndpoint.toString());

                    HttpURLConnection connection = (HttpURLConnection) hotelEndpoint.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("Authorization", "Bearer "+TokenSaver.getTokenKey(CleaningList.this));
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    Log.d("STATUS", String.valueOf(connection.getResponseCode()));
                    Log.d("MSG", connection.getResponseMessage());

                    Log.d("RESPONSE", connection.getInputStream().toString());

                    connection.disconnect();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getCleaningList() {
        //ArrayList<CleaningRoom> cleaningRoomList = new ArrayList<>();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL hotelEndpoint = new URL(URL_HOTEL+"/api/cleaning");

                    Log.d("URL", hotelEndpoint.toString());

                    HttpURLConnection connection = (HttpURLConnection) hotelEndpoint.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("Authorization", "Bearer "+TokenSaver.getTokenKey(CleaningList.this));

                    Log.d("STATUS", String.valueOf(connection.getResponseCode()));
                    Log.d("MSG", connection.getResponseMessage());

                    Log.d("RESPONSE", connection.getInputStream().toString());

                    InputStream responseBody = connection.getInputStream();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseBody, "UTF-8"));
                    String line = "";

                    StringBuilder responseStrBuilder = new StringBuilder();
                    while((line = bufferedReader.readLine()) != null)
                    {
                        responseStrBuilder.append(line);
                    }
                    responseBody.close();

                    JSONObject jObj = new JSONObject(responseStrBuilder.toString());


                    Log.d("RESPONSE", jObj.toString());
                    JSONArray jsonArray = jObj.getJSONArray("data");
                    cleaningRoomList.clear();
                    for(int i =0; i < jsonArray.length(); i++)
                    {
                        JSONObject o = jsonArray.getJSONObject(i);
                        CleaningRoom clr = new CleaningRoom();
                        clr.setId(o.getInt("id"));
                        clr.setRoom_num(o.getInt("rooms_id"));
                        clr.setManager_id(o.getInt("manager_id"));
                        clr.setEmployee_id(o.getInt("employee_id"));
                        clr.setStatus(o.getString("status"));
                        cleaningRoomList.add(clr);
                    }

                    connection.disconnect();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

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

    public class CleaningRoom{
        private int id;
        private int room_num;
        private int manager_id;
        private int employee_id;
        private String status;
        private Date date;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString(){
            return "Quarto : "+ room_num +" ID: " +id+ "\nStatus : " + status;
        }

        public int getManager_id() {
            return manager_id;
        }

        public void setManager_id(int manager_id) {
            this.manager_id = manager_id;
        }

        public int getEmployee_id() {
            return employee_id;
        }

        public void setEmployee_id(int employee_id) {
            this.employee_id = employee_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public int getRoom_num() {
            return room_num;
        }

        public void setRoom_num(int room_num) {
            this.room_num = room_num;
        }

    }
}