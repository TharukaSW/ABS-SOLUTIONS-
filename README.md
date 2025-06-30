# 🏋️ ABS Solutions - Fitness App

A modern Android fitness application built with Kotlin and Firebase, designed to help users track their workouts, manage their fitness profiles, and purchase fitness equipment and supplements.

## ✨ Features

### 🏃‍♂️ **Workout Management**
- Browse and view workout exercises from Firestore database
- Modern card-based exercise display with emoji indicators
- Real-time exercise data synchronization

### 👤 **User Profile Management**
- Complete user profile creation and management
- Personal health information tracking (height, weight, BMI)
- Profile image upload and storage
- Edit profile functionality with real-time updates

### 🛒 **Fitness Store**
- Browse fitness equipment and supplements
- Product search and filtering capabilities
- Modern product cards with pricing
- Firestore integration for product management
- Sample products for demonstration

### 🔐 **Authentication System**
- User registration and login functionality
- Firebase Authentication integration
- Secure user data management

## 🛠️ Technology Stack

- **Language**: Kotlin
- **Platform**: Android (API 24+)
- **Architecture**: MVVM with Repository Pattern
- **Database**: Firebase Firestore & Realtime Database
- **Authentication**: Firebase Auth
- **UI Framework**: Material Design 3
- **Image Loading**: Glide
- **Build System**: Gradle with Kotlin DSL

## 📱 Screenshots

### Main Features
- **Profile Screen**: User information display with health metrics
- **Workout Screen**: Exercise browsing with modern UI
- **Store Screen**: Product catalog with search functionality
- **Bottom Navigation**: Seamless navigation between features

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 24+
- Firebase project setup

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/abs-solutions.git
   cd abs-solutions
   ```

2. **Firebase Setup**
   - Create a new Firebase project
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Firestore Database and Authentication in Firebase Console

3. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

## 📊 Database Structure

### Firestore Collections

#### Users Collection
```json
{
  "users": {
    "user_email": {
      "name": "string",
      "user_type": "string",
      "gender": "string",
      "weight": "number",
      "height": "number",
      "bmi_rate": "number",
      "address": "string",
      "contact_no": "string",
      "profile_image_base64": "string"
    }
  }
}
```

#### Products Collection
```json
{
  "products": {
    "product_id": {
      "name": "string",
      "description": "string",
      "price": "number",
      "imageUrl": "string",
      "category": "string",
      "inStock": "boolean",
      "rating": "number",
      "reviewCount": "number"
    }
  }
}
```

#### Exercises Collection
```json
{
  "exercises": {
    "exercise_id": {
      "name": "string",
      "sets": "string"
    }
  }
}
```

## 🎨 UI/UX Features

- **Dark Theme**: Modern dark interface with yellow accents
- **Material Design**: Following Material Design 3 guidelines
- **Responsive Layout**: Adapts to different screen sizes
- **Smooth Navigation**: Bottom navigation with modern styling
- **Search Functionality**: Real-time product search
- **Loading States**: Progress indicators for better UX

## 🔧 Configuration

### Firebase Configuration
1. Enable Firestore Database
2. Set up Authentication (Email/Password)
3. Configure security rules for collections
4. Add sample data for testing

### Build Configuration
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 35 (Android 15)
- Compile SDK: API 35

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/example/abssolutions/
│   │   ├── data/                    # Database helpers
│   │   ├── activities/              # Main activities
│   │   └── adapters/                # RecyclerView adapters
│   ├── res/
│   │   ├── layout/                  # UI layouts
│   │   ├── drawable/                # Icons and graphics
│   │   ├── values/                  # Colors, strings, themes
│   │   └── menu/                    # Navigation menus
│   └── AndroidManifest.xml
├── build.gradle.kts                 # App-level build config
└── google-services.json             # Firebase config
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Firebase for backend services
- Material Design for UI components
- Android community for best practices

## 📞 Support

For support and questions:
- Create an issue in this repository
- Contact: [your-email@example.com]

---

**Built with ❤️ for the fitness community** 