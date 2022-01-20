package fr.polytech.larynxapp.model.DialogModel;

public class DialogModel {

    private String title,text;

    private int imageSrc;

    public DialogModel(String title, String text, int imageSrc){
        this.title = title;
        this.text = text;
        this.imageSrc = imageSrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(int imageSrc) {
        this.imageSrc = imageSrc;
    }
}
