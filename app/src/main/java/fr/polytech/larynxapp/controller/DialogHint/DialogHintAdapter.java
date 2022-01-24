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

    /**
     * The context of the activity
     */
    private Context context;

    /**
     * List that contains all the different page's informations
     */
    private ArrayList<DialogModel> modelArrayList;


    /**
     * Constructor
     * @param context Context of the activity
     * @param modelArrayList List that contains all the different page's informations
     */
    public DialogHintAdapter(Context context, ArrayList<DialogModel> modelArrayList){
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    /**
     * Give the number of pages
     * @return the size of modelArrayList
     */
    public int getCount(){
        return modelArrayList.size();
    }


    /**
     * check if the object equals the view
     * @param view a view
     * @param object and object
     * @return a boolean
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


    /**
     * return a view that contains all the different cards
     * @param container a view
     * @param position position of the card on the view
     * @return the view
     */
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


    /**
     * Remove an item of a certain position on the container
     * @param container The container
     * @param position The position of the element to be removed
     * @param object The card
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
