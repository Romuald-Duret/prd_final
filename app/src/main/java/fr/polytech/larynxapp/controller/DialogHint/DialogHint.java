package fr.polytech.larynxapp.controller.DialogHint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import fr.polytech.larynxapp.R;
import fr.polytech.larynxapp.model.DialogModel.DialogModel;
import me.relex.circleindicator.CircleIndicator;

public class DialogHint extends AppCompatActivity {

    private ViewPager viewPager;

    private ArrayList<DialogModel> modelArrayList;

    private DialogHintAdapter dialogHintAdapter;

    private Button understand_bttn;

    private CheckBox checkbox;

    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        setContentView(R.layout.activity_dialoghint);

        this.setFinishOnTouchOutside(false);


        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(7);
        loadCards();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == modelArrayList.size()-1){
                    understand_bttn.setTextColor(getApplication().getResources().getColor(R.color.baseblue));
                    understand_bttn.setClickable(true);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        CircleIndicator indicator = findViewById(R.id.circle_indicator);
        indicator.setViewPager(viewPager);


        checkbox = findViewById(R.id.check_box_dialog);

        understand_bttn = findViewById(R.id.understand_button);



        understand_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkbox.isChecked()){
                    sharedPreferences = getSharedPreferences("larynxSettings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean("DialogHintToDismiss", true);
                    editor.apply();
                }

                finish();
            }
        });

        understand_bttn.setClickable(false);
    }

    private void loadCards() {
        modelArrayList = new ArrayList<>();

        modelArrayList.add(new DialogModel("Bienvenue", "Ce tutoriel permet de comprendre le fonctionnement de LarynxApp.\nLarynxApp est une application permettant l'analyse de votre voix.\nCes analyses peuvent permettre d'aider votre médecin.", R.drawable.capture1));
        modelArrayList.add(new DialogModel("Enregistrement", "L'enregistrement est la première étape pour analyser votre voix et en même temps la plus importante, essayez d'être au maximum dans un endroit isolé et calme pour vous enregistrer. Des indications sont affichées tout le long de l'enregistrement pour vous aiguiller.", R.drawable.capture2));
        modelArrayList.add(new DialogModel("Enregistrement", "Afin d'avoir un enregistrement précis, placez-vous à environ 20cm de votre microphone et evitez de vous en eloigner pendant l'enregistrement. Prononcez un 'ah' prolongé tout le long de l'enregistrement.", R.drawable.capture21));
        modelArrayList.add(new DialogModel("Enregistrement", "Une fois l'enregistrement terminé vous pouvez le recommencer ou le sauvegarder. Vous retrouverez alors cet enregistrement dans vos résultats et dans l'onglet 'Historique'.", R.drawable.capture22));
        modelArrayList.add(new DialogModel("Historique", "Cet onglet permet de voir tous les enregsitrements que vous avez réalisés et d'en avoir les détails. En appuyant simplement sur un enregistrement vous verrez alors un récapitulatif des résultats ainsi que leur signification. Un appui prolongé vous permettra de le supprimer.", R.drawable.capture3));
        modelArrayList.add(new DialogModel("Analyse Shimmer", "L'onglet d'analyse Shimmer affiche un graphique de l'évolution de la variable Shimmer de tous vos enregistrements. Vous pourrez alors voir l'évolution de votre voix sous ce paramètre. Cet onglet permet égalemennt de sélectionner une période à afficher sur le graphique.", R.drawable.capture4));
        modelArrayList.add(new DialogModel("Analyse Jitter", "L'onglet d'analyse Jitter affiche un graphique de l'évolution de la variable Jhimmer de tous vos enregistrements. Vous pourrez alors voir l'évolution de votre voix sous ce paramètre. Cet onglet permet égalemennt de sélectionner une période à afficher sur le graphique.", R.drawable.capture5));

        dialogHintAdapter = new DialogHintAdapter(this, modelArrayList);

        viewPager.setAdapter(dialogHintAdapter);

        viewPager.setPadding(65, 0, 65, 0);
    }
}