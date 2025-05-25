# 📸 ImagePicker

An Android image picker library inspired by **WhatsApp's image selection style**.  
Designed to provide a smooth and familiar image selection experience for users.

---

## ✨ Features

- 📂 Pick images from device gallery
- 📷 Capture photos using device camera
- 🖼️ WhatsApp-style interface
- ✅ Easy integration into existing projects
- ⚙️ Kotlin-based and lightweight
- 🎯 Minimal dependencies

---

## 🚀 Demo

Coming soon!  
You can build and run the included sample app module to test the picker.

---

## 🔧 Installation

### Step 1: Add JitPack to your root `build.gradle`

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.iFarmer-softs:imagePicker:1.0.5'
}

ImagePicker.with(ImagePreviewActivity.this)
                        .start(new ImageSelectionListener() {
                            @Override
                            public void onImageSelected(LinkedHashMap<String, String> selectedImages) {
                                images.addAll(selectedImages.keySet());
                                if (imagePreviewRVAdapter != null) {
                                    imagePreviewRVAdapter.notifyDataSetChanged();
                                }
                            }
                        });