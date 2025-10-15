# Panorama360 - Android 360° Image Viewer Library

[![](https://jitpack.io/v/yourusername/panorama360.svg)](https://jitpack.io/#yourusername/panorama360)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)

A lightweight, pure Kotlin Android library for displaying 360-degree panoramic images using OpenGL ES 2.0. **No native libraries, no 16KB alignment issues** - works perfectly on all Android devices including Android 15+.

## 🌟 Features

- ✅ **Pure Kotlin/Java** - No native (.so) libraries
- ✅ **No 16KB Alignment Issues** - Works on Android 15+ without crashes
- ✅ **Smooth Touch Navigation** - Intuitive swipe-to-rotate controls
- ✅ **Multiple Image Sources** - Load from resources, URLs, or Bitmaps
- ✅ **Lightweight** - Minimal dependencies
- ✅ **Easy Integration** - Single view component
- ✅ **High Performance** - Optimized OpenGL ES 2.0 rendering
- ✅ **Supports All Architectures** - ARM, ARM64, x86, x86_64

## 📱 Demo

<p align="center">
  <img src="demo/screenshot.gif" width="300" alt="Demo"/>
</p>

## 🚀 Installation

### Step 1: Add JitPack repository

In your root `build.gradle` or `settings.gradle`:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }  // Add this line
    }
}
```

### Step 2: Add the dependency

In your app module's `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.yourusername:panorama360:1.0.0'
}
```

Or if using Kotlin DSL (`build.gradle.kts`):

```kotlin
dependencies {
    implementation("com.github.yourusername:panorama360:1.0.0")
}
```

## 📖 Usage

### Basic Implementation

#### 1. Add the view to your layout XML

```xml
<com.gps.location.a360images.Panorama360View
    android:id="@+id/panorama_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

#### 2. Load an image in your Activity/Fragment

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var panoramaView: Panorama360View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        panoramaView = findViewById(R.id.panorama_view)
        
        // Load from drawable resources
        panoramaView.loadImageFromResources(R.drawable.my_360_image)
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

### Loading Images from Different Sources

#### From Drawable Resources
```kotlin
panoramaView.loadImageFromResources(R.drawable.my_panorama)
```

#### From URL
```kotlin
panoramaView.loadImageFromUrl("https://example.com/panorama.jpg")
```

#### From Bitmap
```kotlin
val bitmap: Bitmap = // ... your bitmap
panoramaView.loadImageFromBitmap(bitmap)
```

## 🎨 Customization

### Adjust Touch Sensitivity

The default touch sensitivity is optimized for most use cases, but you can modify it by extending the `Panorama360View` class:

```kotlin
class CustomPanoramaView(context: Context, attrs: AttributeSet? = null) 
    : Panorama360View(context, attrs) {
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Modify sensitivity by changing the multiplier (default is 0.2f)
        // Higher value = more sensitive
        // Lower value = less sensitive
        return super.onTouchEvent(event)
    }
}
```

## 🖼️ Image Requirements

For best results, your 360° images should be:

- **Format**: Equirectangular projection (spherical panorama)
- **Aspect Ratio**: 2:1 (e.g., 4096x2048, 8192x4096)
- **File Format**: JPG, PNG, or WebP
- **Quality**: Higher resolution = better quality (but larger file size)

### Where to Find 360° Images

- [Flickr 360° Photos](https://www.flickr.com/groups/equirectangular/)
- [Wikimedia Commons Panoramas](https://commons.wikimedia.org/wiki/Category:Equirectangular_projection)
- Free sample: `https://raw.githubusercontent.com/aframevr/sample-assets/master/assets/360-image-gallery/4.jpg`

## 🔧 Technical Details

### Architecture

- **Rendering Engine**: OpenGL ES 2.0
- **Sphere Generation**: Dynamic mesh with configurable resolution
- **Texture Mapping**: Optimized UV mapping for equirectangular images
- **Touch Handling**: Custom gesture processing for smooth rotation

### Performance

- Smooth 60 FPS on most modern devices
- Efficient texture loading and caching
- Minimal memory footprint
- No blocking of the main thread

## 🆚 Why This Library?

### The Problem with Other 360 Libraries

Many popular 360° panorama libraries (like PanoramaGL) contain native libraries (`.so` files) that have **16KB alignment issues** on newer Android versions (Android 15+), causing:

- App crashes on devices with 16KB page size
- Build failures
- Google Play Console rejections

### Our Solution

This library uses **pure Kotlin/Java** with Android's built-in OpenGL ES support:

| Feature | Other Libraries | Panorama360 |
|---------|----------------|-------------|
| Native Libraries (.so) | ❌ Yes | ✅ No |
| 16KB Alignment Issues | ❌ Yes | ✅ No |
| Works on Android 15+ | ❌ May crash | ✅ Perfect |
| Cross-architecture | ❌ Need all .so files | ✅ Works everywhere |
| App Size | ❌ Larger | ✅ Smaller |
| Maintenance | ❌ Often abandoned | ✅ Active |

## 📋 Requirements

- **Min SDK**: 24 (Android 7.0)
- **Compile SDK**: 36+
- **Language**: Kotlin/Java
- **OpenGL ES**: 2.0+

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

```
MIT License

Copyright (c) 2025 [Your Name]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🐛 Troubleshooting

### Image appears black
- Ensure your image is in equirectangular format
- Check that the image file is not corrupted
- Verify the resource ID or URL is correct

### Touch controls not working
- Make sure you're testing on a real device or emulator with touch support
- Ensure `onResume()` and `onPause()` are called properly

### Build errors
- Ensure you're using the correct Gradle version
- Run `./gradlew clean build`
- Check that JitPack repository is added

## 📧 Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/yourusername/panorama360/issues) page
2. Create a new issue with detailed information
3. Check the [documentation](https://github.com/yourusername/panorama360/wiki)

## ⭐ Show Your Support

If you find this library helpful, please consider giving it a ⭐️ on GitHub!

---

Made with ❤️ for the Android community

