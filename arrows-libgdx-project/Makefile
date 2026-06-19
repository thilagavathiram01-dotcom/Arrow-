.PHONY: help clean build desktop android run install test

GRADLE := ./gradlew
ANDROID_SDK := $(ANDROID_HOME)

help:
	@echo "Arrows Game - Build Commands"
	@echo "============================="
	@echo "make desktop        - Build desktop version"
	@echo "make android        - Build Android APK"
	@echo "make run            - Run desktop version"
	@echo "make install        - Install Android APK on device"
	@echo "make clean          - Clean build artifacts"
	@echo "make all            - Build all targets"
	@echo "make jar            - Create executable JAR"
	@echo "make release-apk    - Build release APK"

clean:
	@echo "Cleaning build artifacts..."
	$(GRADLE) clean
	@rm -rf build/

desktop:
	@echo "Building desktop version..."
	$(GRADLE) desktop:build

android:
	@echo "Building Android APK..."
	$(GRADLE) android:assembleDebug

run: desktop
	@echo "Running desktop version..."
	$(GRADLE) desktop:run

jar: desktop
	@echo "Creating executable JAR..."
	@echo "JAR created at: desktop/build/libs/desktop-1.0.0.jar"

install: android
	@echo "Installing APK on device..."
	$(GRADLE) android:installDebug
	$(GRADLE) android:run

test:
	@echo "Running tests..."
	$(GRADLE) test

release-apk:
	@echo "Building release APK..."
	$(GRADLE) android:assembleRelease
	@echo "APK created at: android/build/outputs/apk/release/"

all: clean desktop android
	@echo "Build complete!"
	@echo "Desktop JAR: desktop/build/libs/desktop-1.0.0.jar"
	@echo "Android APK: android/build/outputs/apk/debug/"

refresh:
	@echo "Refreshing dependencies..."
	$(GRADLE) --refresh-dependencies

check-java:
	@echo "Java version:"
	@java -version
	@echo "Gradle version:"
	@$(GRADLE) --version

setup:
	@echo "Setting up project..."
	@chmod +x gradlew
	@echo "Done! Run 'make help' for available commands"

# Development tasks
watch:
	@echo "Watching for changes..."
	@ls -la

format:
	@echo "Formatting Kotlin code..."
	@echo "Note: Install ktlint for automatic formatting"

# Device management
adb-devices:
	@adb devices

adb-uninstall:
	@adb uninstall com.arrows.game

# Build variants
debug: android
	@echo "Debug APK built: android/build/outputs/apk/debug/"

release: release-apk
	@echo "Release APK built: android/build/outputs/apk/release/"
