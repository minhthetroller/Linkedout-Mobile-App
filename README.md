# LinkedOut - Job Recruitment Android App

A modern job recruitment application built with **Kotlin** and **Jetpack Compose**, following the latest Android development best practices.

## 🎯 Features

### For Job Seekers
- **Browse Jobs**: Search and filter through available job postings
- **Recommended Jobs**: AI-powered job recommendations based on your preferences
- **Job Details**: View comprehensive job information including company details
- **Profile Setup**: Complete 3-step registration with personalized preferences

### For Recruiters
- **Post Jobs**: Create detailed job postings with automatic AI tag generation
- **Manage Jobs**: Edit, update, or close job postings
- **Track Applications**: View all your posted jobs in one place

## 🏗️ Architecture

### Modern Android Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Navigation**: Navigation Compose
- **Networking**: Retrofit + OkHttp
- **Local Storage**: DataStore (Preferences)
- **Image Loading**: Coil
- **Async Operations**: Kotlin Coroutines + Flow

### Project Structure
```
com.example.linkedout/
├── data/
│   ├── local/              # DataStore for local preferences
│   ├── model/              # Data models
│   ├── remote/             # API service and interceptors
│   └── repository/         # Repository pattern implementation
├── di/                     # Dependency injection modules
├── ui/
│   ├── auth/               # Authentication screens
│   ├── jobs/
│   │   ├── seeker/         # Job seeker screens
│   │   └── recruiter/      # Recruiter screens
│   ├── navigation/         # Navigation setup
│   └── theme/              # Material 3 theming
├── util/                   # Utility classes
└── MainActivity.kt         # Entry point
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK 26 (Android 8.0) or higher
- Backend API server running (see backend documentation)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Linkedout
   ```

2. **Configure API Base URL**
   
   Open `app/src/main/java/com/example/linkedout/di/AppModule.kt` and update the BASE_URL:
   
   ```kotlin
   private const val BASE_URL = "http://YOUR_SERVER_IP:3000/api/"
   ```
   
   For Android Emulator use: `http://10.0.2.2:3000/api/`
   For Physical Device use: `http://YOUR_LOCAL_IP:3000/api/`

3. **Sync Gradle**
   ```bash
   ./gradlew build
   ```

4. **Run the App**
   - Open in Android Studio
   - Select your device/emulator
   - Click Run ▶️

## 📱 User Flow

### Registration (3-Step Process)

#### Step 1: Basic Information
- Email and password
- User type selection (Seeker/Recruiter)
- Full name and birth date

#### Step 2: Profile Details
**For Seekers:**
- Current job (optional)
- Years of experience (optional)
- Location (optional)
- Phone number (optional)

**For Recruiters:**
- Company name (required)
- Company size (optional)
- Company website (optional)
- Phone number (optional)

#### Step 3: Preferences (Optional)
**For Seekers:**
- Preferred job titles
- Preferred industries
- Preferred locations
- Salary expectations
- Can skip this step

**For Recruiters:**
- Just confirmation to complete setup

### Main Features

#### Job Seekers
1. **Home Screen**: Browse all available jobs
2. **Search & Filter**: Filter by location, salary, employment type, tags
3. **Recommended Jobs**: View personalized job matches with match scores
4. **Job Details**: Complete job information with company details

#### Recruiters
1. **Dashboard**: View all your posted jobs
2. **Create Job**: Post new job with automatic AI tag generation
3. **Edit Job**: Update existing job postings
4. **Delete Job**: Close job postings

## 🔐 Authentication

- JWT-based authentication
- Token stored securely in DataStore
- Automatic token injection via OkHttp interceptor
- Session management with auto-logout

## 🛠️ Dependencies

### Core
```kotlin
- Kotlin 2.0.21
- Compose BOM 2024.09.00
- Material 3
```

### Networking
```kotlin
- Retrofit 2.11.0
- OkHttp 4.12.0
- Gson Converter
```

### DI & Architecture
```kotlin
- Hilt 2.52
- Navigation Compose 2.8.5
- Lifecycle Runtime KTX 2.9.4
```

### Storage & Utils
```kotlin
- DataStore Preferences 1.1.1
- Coil Compose 2.7.0
```

## 🎨 UI/UX Features

- **Material 3 Design**: Modern, adaptive UI components
- **Dynamic Theming**: System-wide dark/light theme support
- **Responsive Layouts**: Adapts to different screen sizes
- **Loading States**: Proper loading indicators
- **Error Handling**: User-friendly error messages
- **Form Validation**: Client-side validation

## 📝 API Integration

The app integrates with the LinkedOut Backend API. Key endpoints:

- **POST** `/auth/signup/step1` - Create account
- **POST** `/auth/signup/step2` - Complete profile
- **POST** `/auth/signup/step3` - Add preferences
- **POST** `/auth/login` - User login
- **GET** `/auth/me` - Get current user
- **GET** `/jobs` - Browse jobs
- **GET** `/jobs/recommended` - Get recommendations
- **POST** `/recruiter/jobs` - Create job
- **PUT** `/recruiter/jobs/:id` - Update job
- **DELETE** `/recruiter/jobs/:id` - Delete job

See `API_DOCUMENTATION.md` for complete API reference.

## 🧪 Testing

Run tests:
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## 📦 Build & Deploy

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/`

## 🔧 Configuration

### Build Configuration
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Java Version**: 11

### ProGuard
Enable minification in `app/build.gradle.kts`:
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        proguardFiles(...)
    }
}
```

## 🐛 Troubleshooting

### Common Issues

**1. Network Error**
- Check if backend server is running
- Verify BASE_URL in AppModule.kt
- Check network permissions in AndroidManifest.xml

**2. Build Errors**
- Sync Gradle: `./gradlew --refresh-dependencies`
- Clean build: `./gradlew clean build`
- Invalidate caches in Android Studio

**3. Authentication Issues**
- Clear app data
- Verify token storage in DataStore
- Check API response format

## 📄 License

This project is for educational purposes.

## 👥 Contributing

1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Open Pull Request

## 📧 Support

For issues or questions, please create an issue in the repository.

---

**Built with ❤️ using Modern Android Development practices**

