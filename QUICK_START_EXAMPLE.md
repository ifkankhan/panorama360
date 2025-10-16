# Quick Start Example

## Step-by-Step Guide to Display Your First 360 Image

### Option 1: Using a Sample URL (Easiest for Testing)

1. **Edit MainActivity.kt** and replace the `load360Image()` function:

```kotlin
private fun load360Image() {
    // Load a sample 360 image from the internet
    panoramaView.loadImageFromUrl(
        "https://raw.githubusercontent.com/aframevr/sample-assets/master/assets/360-image-gallery/4.jpg"
    )
}
```

2. **Make sure you have internet permission** (already added in AndroidManifest.xml):
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

3. **Build and run:**
```bash
./gradlew installDebug
```

4. **That's it!** Swipe to look around the 360 image.

---

### Option 2: Using Your Own Image from Resources

1. **Get a 360 image:**
   - Download any equirectangular 360 image
   - Or take one with a 360 camera
   - Or download sample: https://upload.wikimedia.org/wikipedia/commons/2/2e/Equirectangular_projection_SW.jpg

2. **Add to your project:**
   - Copy the image file
   - Paste into `app/src/main/res/drawable/`
   - Rename to `panorama_image.jpg` (or keep original name, no spaces)

3. **Edit MainActivity.kt:**

```kotlin
private fun load360Image() {
    // Load from drawable resources
    panoramaView.loadImageFromResources(R.drawable.panorama_image)
}
```

4. **Build and run:**
```bash
./gradlew clean installDebug
```

---

## Controls

- **Swipe left/right**: Rotate horizontally
- **Swipe up/down**: Rotate vertically (limited to prevent flipping)

---

## Troubleshooting

### "Image appears black"
✅ **Solution**: Make sure your image is in equirectangular format (looks like a stretched panorama)

### "App crashes on start"
✅ **Solution**: 
1. Check that the image file exists in drawable folder
2. Check that resource name matches (no spaces, lowercase)
3. Check LogCat for error messages

### "Image loads but looks distorted"
✅ **Solution**: Image must be equirectangular projection (standard 360° format)

---

## Example Code - Full MainActivity.kt

```kotlin
package com.gps.location.a360images

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var panoramaView: Panorama360View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        panoramaView = findViewById(R.id.panorama_view)
        load360Image()
    }

    private fun load360Image() {
        // Option 1: Load from URL (good for testing)
        panoramaView.loadImageFromUrl(
            "https://raw.githubusercontent.com/aframevr/sample-assets/master/assets/360-image-gallery/4.jpg"
        )
        
        // Option 2: Load from drawable (uncomment to use)
        // panoramaView.loadImageFromResources(R.drawable.panorama_image)
    }

    override fun onResume() {
        super.onResume()
        panoramaView.onResume()
    }

    override fun onPause() {
        super.onPause()
        panoramaView.onPause()
    }
}
```

---

## Test Images

Here are some public domain 360 images you can test with:

1. **Beach Scene**:
   ```
   https://raw.githubusercontent.com/aframevr/sample-assets/master/assets/360-image-gallery/4.jpg
   ```

2. **Mountain View**:
   ```
   https://raw.githubusercontent.com/aframevr/sample-assets/master/assets/360-image-gallery/1.jpg
   ```

3. **City Scene**:
   ```
   https://raw.githubusercontent.com/aframevr/sample-assets/master/assets/360-image-gallery/2.jpg
   ```

---

## Features Demonstration

### Adjust Touch Sensitivity

In `Panorama360View.kt`, find the `onTouchEvent` method:

```kotlin
override fun onTouchEvent(event: MotionEvent): Boolean {
    val x = event.x
    val y = event.y

    when (event.action) {
        MotionEvent.ACTION_MOVE -> {
            val dx = x - previousX
            val dy = y - previousY

            // Adjust these values for sensitivity
            renderer.angleX += dx * 0.5f  // Change to 1.0f for faster rotation
            renderer.angleY += dy * 0.5f  // Change to 1.0f for faster rotation

            // Limit Y rotation to prevent flipping
            renderer.angleY = renderer.angleY.coerceIn(-90f, 90f)

            requestRender()
        }
    }

    previousX = x
    previousY = y
    return true
}
```

---

## Performance Tips

1. **Image Size**: Use images between 2048x1024 and 4096x2048 for best balance
2. **Format**: JPEG is recommended (smaller file size than PNG)
3. **Loading**: Images from resources load faster than from URL
4. **Memory**: Large images (>8K) may cause OutOfMemoryError on older devices

---

## Success Checklist

- ✅ Project builds successfully
- ✅ No 16KB alignment errors
- ✅ No native library warnings
- ✅ App installs on device
- ✅ 360 image loads and displays
- ✅ Touch controls work smoothly
- ✅ Can rotate in all directions

---

## Next Steps

Once you have the basic 360 viewer working:

1. **Add multiple images** - Create a gallery
2. **Add UI controls** - Buttons for different views
3. **Add hotspots** - Interactive points in the 360 scene
4. **Add gyroscope** - Look around by moving device
5. **Add VR mode** - Split screen for VR headsets

For these advanced features, check the comments in `Panorama360View.kt` or create custom implementations.

---

**Congratulations! You now have a working 360 image viewer with NO 16KB alignment issues! 🎉**



