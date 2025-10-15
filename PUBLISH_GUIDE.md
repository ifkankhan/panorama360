# Publishing Guide for Panorama360 Library

This guide will walk you through the process of publishing your library to GitHub and making it available via JitPack.

## ✅ What's Already Done

- ✅ Converted project from application to library
- ✅ Added Maven publishing configuration
- ✅ Created comprehensive README.md
- ✅ Set up .gitignore
- ✅ Initialized Git repository and committed files

## 📋 Next Steps

### Step 1: Create a GitHub Repository

1. Go to [GitHub](https://github.com) and log in
2. Click the **"+"** icon in the top right corner
3. Select **"New repository"**
4. Fill in the details:
   - **Repository name**: `panorama360` (or any name you prefer)
   - **Description**: `A lightweight Android library for displaying 360-degree panoramic images with no native dependencies`
   - **Visibility**: ✅ **Public** (required for JitPack)
   - **DO NOT** initialize with README, .gitignore, or license (we already have these)
5. Click **"Create repository"**

### Step 2: Push Your Code to GitHub

After creating the repository, run these commands in your terminal:

```bash
cd /Users/macbook/AndroidStudioProjects/360images

# Add your GitHub repository as remote (replace with your actual GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/panorama360.git

# Push your code to GitHub
git push -u origin main
```

**Example** (replace `YOUR_USERNAME` with your actual GitHub username):
```bash
git remote add origin https://github.com/johndoe/panorama360.git
git push -u origin main
```

### Step 3: Create a Release Tag

After pushing to GitHub, create a release tag:

```bash
cd /Users/macbook/AndroidStudioProjects/360images

# Create and push a tag for version 1.0.0
git tag -a 1.0.0 -m "Release version 1.0.0"
git push origin 1.0.0
```

### Step 4: Publish on JitPack

1. Go to [JitPack.io](https://jitpack.io)
2. In the "Git repo url" field, paste your repository URL:
   ```
   https://github.com/YOUR_USERNAME/panorama360
   ```
3. Click **"Look up"**
4. JitPack will automatically detect your releases
5. Click **"Get it"** next to version `1.0.0`
6. Wait for the build to complete (usually takes 1-3 minutes)
7. Once successful, you'll see a green checkmark ✅

### Step 5: Update README with Your GitHub Username

Before or after publishing, update the README.md file:

**Replace** `yourusername` with your actual GitHub username in:

1. Badge links:
   ```markdown
   [![](https://jitpack.io/v/YOUR_USERNAME/panorama360.svg)](https://jitpack.io/#YOUR_USERNAME/panorama360)
   ```

2. Installation instructions:
   ```groovy
   implementation 'com.github.YOUR_USERNAME:panorama360:1.0.0'
   ```

3. Other references to `yourusername` throughout the README

Then commit and push the changes:
```bash
git add README.md
git commit -m "Update README with GitHub username"
git push origin main
```

### Step 6: Update build.gradle with Your GitHub Username

Update the groupId in `app/build.gradle`:

**Find this section:**
```groovy
afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release
                groupId = 'com.github.yourusername'  // Update this
                artifactId = 'panorama360'
                version = '1.0.0'
            }
        }
    }
}
```

**Change to:**
```groovy
groupId = 'com.github.YOUR_USERNAME'
```

Then commit and push:
```bash
git add app/build.gradle
git commit -m "Update groupId with GitHub username"
git push origin main
```

## 🎉 You're Done!

Once JitPack successfully builds your library, anyone can use it by adding:

### In their `settings.gradle` or root `build.gradle`:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

### In their app's `build.gradle`:
```groovy
dependencies {
    implementation 'com.github.YOUR_USERNAME:panorama360:1.0.0'
}
```

## 📦 Publishing New Versions

When you want to release a new version:

1. Update the version in `app/build.gradle`:
   ```groovy
   version = '1.0.1'  // or 1.1.0, 2.0.0, etc.
   ```

2. Commit your changes:
   ```bash
   git add .
   git commit -m "Version 1.0.1: Description of changes"
   git push origin main
   ```

3. Create a new tag:
   ```bash
   git tag -a 1.0.1 -m "Release version 1.0.1"
   git push origin 1.0.1
   ```

4. JitPack will automatically build the new version

## 🔍 Verifying the Build

To check if your library was built successfully:

1. Go to `https://jitpack.io/#YOUR_USERNAME/panorama360`
2. Look for the green checkmark ✅ next to your version
3. Click on the version to see the build log
4. If there's a red ❌, click on it to see what went wrong

## 🐛 Common Issues

### Issue: "Could not find com.github.username:panorama360:1.0.0"

**Solution:**
1. Make sure your repository is **public**
2. Verify the tag exists: `git tag -l`
3. Check JitPack build status at `https://jitpack.io/#YOUR_USERNAME/panorama360`
4. Wait a few minutes after creating the tag

### Issue: JitPack build fails

**Solutions:**
1. Check the build log on JitPack for specific errors
2. Ensure `gradle/wrapper/gradle-wrapper.jar` is committed to Git
3. Make sure your project builds locally: `./gradlew clean build`
4. Verify all dependencies are available

### Issue: "ERROR: No build artifacts found"

**Solution:**
This is fixed! We've already:
- Changed from `android.application` to `android.library`
- Added Maven publishing configuration
- Removed `applicationId` from build.gradle

## 📚 Additional Resources

- [JitPack Documentation](https://jitpack.io/docs/)
- [GitHub Releases Guide](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository)
- [Semantic Versioning](https://semver.org/)

## 💡 Quick Command Reference

```bash
# Check current repository status
git status

# View commit history
git log --oneline

# List all tags
git tag -l

# Push all tags
git push origin --tags

# Undo last commit (keep changes)
git reset --soft HEAD~1

# View remote URL
git remote -v
```

## 🎯 Example Complete Workflow

Here's a complete example assuming GitHub username is `johndoe`:

```bash
# 1. Create GitHub repo at https://github.com/new

# 2. Link local repo to GitHub
cd /Users/macbook/AndroidStudioProjects/360images
git remote add origin https://github.com/johndoe/panorama360.git
git push -u origin main

# 3. Create release tag
git tag -a 1.0.0 -m "Release version 1.0.0"
git push origin 1.0.0

# 4. Go to https://jitpack.io
# 5. Enter: https://github.com/johndoe/panorama360
# 6. Click "Look up" and wait for build

# Done! Users can now use:
# implementation 'com.github.johndoe:panorama360:1.0.0'
```

---

Need help? Create an issue on GitHub or check the [JitPack documentation](https://jitpack.io/docs/).

