// DashboardActivity.java
package mx.edu.logincontrolalmacen;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mx.edu.logincontrolalmacen.api.ApiResponse;
import mx.edu.logincontrolalmacen.api.ApiService;
import mx.edu.logincontrolalmacen.api.Part;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;
    private PartAdapter adapter;
    private List<Part> parts = new ArrayList<>();
    private List<Part> originalParts = new ArrayList<>();
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Ocultar la barra de acción si está presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Obtener el token pasado desde MainActivity
        token = getIntent().getStringExtra("TOKEN");

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);

        // Crear el adaptador con el token
        adapter = new PartAdapter(this, parts, token);
        listView.setAdapter(adapter);


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://64.23.200.218/api/")  // Reemplaza con la URL de tu API
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getClientWithAuthHeader(token))
                .build();

        apiService = retrofit.create(ApiService.class);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchParts(query);
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }

        });


        // Cargar datos iniciales
        loadInitialData();
    }

    private OkHttpClient getClientWithAuthHeader(String token) {
        return new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        }).build();
    }

    private void searchParts(String query) {
        Call<ApiResponse> call = apiService.searchParts(query);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("DashboardActivity", "Response: " + response.body().toString());
                    adapter.updateData(response.body().getData());
                } else {
                    try {
                        Log.d("DashboardActivity", "Response Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("DashboardActivity", "Search Error: " + response.code());
                    Toast.makeText(DashboardActivity.this, "Search failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("DashboardActivity", "Network Error: " + t.getMessage());
                Toast.makeText(DashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadInitialData() {
        Call<ApiResponse> call = apiService.searchParts(""); // Cargar todos los datos iniciales sin filtro
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("DashboardActivity", "Initial Data Response: " + response.body().toString());
                    originalParts = response.body().getData();

                    // Imprimir JSON de la respuesta
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(response.body());
                    Log.d("DashboardActivity", "JSON Response: " + jsonResponse);

                    Collections.sort(originalParts, new Comparator<Part>() {
                        @Override
                        public int compare(Part p1, Part p2) {
                            return p1.getName().compareToIgnoreCase(p2.getName());
                        }
                    });

                    adapter.updateData(originalParts);
                } else {
                    try {
                        Log.d("DashboardActivity", "Initial Data Response Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("DashboardActivity", "Initial Data Error: " + response.code());
                    Toast.makeText(DashboardActivity.this, "Initial data load failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("DashboardActivity", "Network Error: " + t.getMessage());
                Toast.makeText(DashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
