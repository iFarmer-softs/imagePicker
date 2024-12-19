package asia.ifarmer.imagepicker.events;

import java.util.LinkedHashMap;

public interface ImageSelectionListener {
    void onImageSelected(LinkedHashMap<String, String> selectedImages);
}
