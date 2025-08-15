# Firebase Authentication Android App

A complete Android mobile application with Firebase authentication featuring login and signup functionality.

## Features

- **User Registration**: Sign up with email and password
- **User Login**: Secure login with email and password
- **Firebase Integration**: Powered by Firebase Authentication
- **Session Management**: Automatic login for returning users
- **Error Handling**: Comprehensive error messages and validation
- **Modern UI**: Material Design components with clean interface

## Project Structure

```
app/
├── src/main/java/com/example/firebaseauthapp/
│   ├── MainActivity.java          # Splash screen and navigation
│   ├── LoginActivity.java         # Login screen
│   ├── SignupActivity.java        # Registration screen
│   ├── HomeActivity.java          # Home screen after login
│   └── FirebaseAuthHelper.java    # Firebase authentication helper
├── src/main/res/
│   ├── layout/                    # XML layouts for activities
│   ├── values/                    # Colors, strings, themes
│   └── drawable/                  # App icons and graphics
├── google-services.json           # Firebase configuration
└── build.gradle                   # Gradle configuration
```

## Setup Instructions

### 1. Firebase Configuration

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select an existing one
3. Add an Android app to your project
4. Download the `google-services.json` file
5. Replace the placeholder `google-services.json` in the `app/` directory

### 2. Enable Authentication

1. In Firebase Console, go to Authentication
2. Click "Get started"
3. Enable "Email/Password" sign-in method
4. Save the changes

### 3. Build and Run

```bash
# Open in Android Studio
# Sync project with Gradle files
# Run on Android device or emulator
```

## Usage

1. **Sign Up**: New users can create an account with email and password
2. **Login**: Existing users can login with their credentials
3. **Home**: After successful login, users are redirected to the home screen
4. **Logout**: Users can logout from the home screen

## Dependencies

- **Firebase Authentication**: For user authentication
- **Material Components**: For modern UI design
- **AndroidX**: For backward compatibility

## Screenshots

- **Login Screen**: Clean interface with email and password fields
- **Signup Screen**: Registration form with validation
- **Home Screen**: Welcome message with user email display

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is open source and available under the [MIT License](LICENSE).
