[app]

# (str) Title of your application
title = Tradutor PJ Offline

# (str) Package name
package.name = tradutorpj

# (str) Package domain (needed for android/ios packaging)
package.domain = org.pjruf

# (str) Source code where the main.py lives
source.dir = .

# (list) Source files to include (let empty to include all the files)
source.include_exts = py,png,jpg,kv,atlas,bin,json,model,txt,argosmodel

# (list) List of directory to include
source.include_patterns = modelos/*,modelos/**/*

# (str) Application versioning
version = 1.0.0

# (list) Application requirements
requirements = python3,kivy,argostranslate,ctranslate2,sentencepiece

# (str) Supported orientation (one of landscape, sensorLandscape, portrait or all)
orientation = portrait

# (bool) Indicate if the application should be fullscreen or not
fullscreen = 0

# (int) Target Android API
android.api = 33

# (int) Minimum API your APK will support.
android.minapi = 24

# (int) Android SDK version to use
android.sdk = 33

# (int) Android NDK version to use
android.ndk = 25b

# (bool) Use --private data storage (True) or --dir public storage (False)
android.private_storage = True

# (list) Permissions
android.permissions = INTERNET

[buildozer]

# (int) Log level (0 = error only, 1 = info, 2 = debug (with command output))
log_level = 2

# (int) Display warning if buildozer is run as root (0 = False, 1 = True)
warn_on_root = 1
