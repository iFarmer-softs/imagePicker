package asia.ifarmer.imagepicker;

import android.content.Context;
import android.content.Intent;

import asia.ifarmer.imagepicker.events.ImageSelectionListener;
import asia.ifarmer.imagepicker.views.ImagerActivity;

public class ImagePicker {
    private static ImagePicker ip;
    private Context context;

    public static ImagePicker with(Context context){
        if(ip == null){
            ip = new ImagePicker();
        }
        ip.context = context;
        return ip;
    }

    public void start(ImageSelectionListener imageSelectionListener){
        ImagerActivity.setImageSelectionListener(imageSelectionListener);
        context.startActivity(new Intent(context, ImagerActivity.class));
    }
}
