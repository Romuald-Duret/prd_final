package fr.polytech.larynxapp.controller.DialogHint;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import fr.polytech.larynxapp.R;
import fr.polytech.larynxapp.model.DialogModel.DialogModel;

public class DialogHintAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<DialogModel> modelArrayList;

    public DialogHintAdapter(Context context, ArrayList<DialogModel> modelArrayList){
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    public int getCount(){
        return modelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_item, container, false);

        ImageView imageViewTv = view.findViewById(R.id.banner_Cv);
        TextView titleTv = view.findViewById(R.id.title_Cv);
        TextView textTv = view.findViewById(R.id.text_Cv);

        DialogModel model = modelArrayList.get(position);
        final String title = model.getTitle();
        final String text = model.getText();
        final int imageSrc = model.getImageSrc();

        imageViewTv.setImageResource(imageSrc);
        titleTv.setText(title);
        textTv.setText(text);

        container.addView(view, position);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
