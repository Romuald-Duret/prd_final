package fr.polytech.larynxapp.controller.splash;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import fr.polytech.larynxapp.MainActivity;
import fr.polytech.larynxapp.R;

public class SplashScreen extends AppCompatActivity {

    Animation titleAnim, appTextAnim, soundAnim;

    private TextView splashTitle;
    private TextView splashAppText;
    private LottieAnimationView splashSound;
    /**
     * the splashScreen activity
     * @param savedInstanceState save the activity's state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        titleAnim = AnimationUtils.loadAnimation(this, R.anim.title_anim);
        appTextAnim = AnimationUtils.loadAnimation(this, R.anim.apptext_anim);
        soundAnim = AnimationUtils.loadAnimation(this, R.anim.sound_anim);

        splashTitle = findViewById(R.id.splashtitle);
        splashAppText = findViewById(R.id.splashapptext);
        splashSound = findViewById(R.id.splashsound);

        splashTitle.setAnimation(titleAnim);
        splashAppText.setAnimation(appTextAnim);
        splashSound.setAnimation(soundAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                Animatoo.animateSlideDown(SplashScreen.this);

                finish();
            }
        }, 3500);
    }

}
