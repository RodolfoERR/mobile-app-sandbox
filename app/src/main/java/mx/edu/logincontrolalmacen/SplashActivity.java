package mx.edu.logincontrolalmacen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ocultar la barra de acción si está presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Mostrar el splash screen por 2 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar la MainActivity después del retraso
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000); // 2000 milisegundos = 2 segundos
    }
}
