package ml.ajwad.hermswayfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class SplashScreenActivity extends AppCompatActivity {

    ImageView iv;
    Handler handler1, handler2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        iv = findViewById(R.id.splash);
        handler2 = new Handler();
        handler2.postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, MonitorActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
