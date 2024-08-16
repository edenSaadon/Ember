# Ember - Dating App

**Ember** is a modern and innovative dating application designed to connect people based on their preferences and interests. This README provides an overview of the application’s features, the flow of screens, and setup instructions.

## Features

- **User Registration:** New users can sign up using their email or phone number.
- **Questionnaire:** After registration, users complete a questionnaire to help match them with compatible partners.
- **Home Screen:** Users can view profiles of potential matches based on their preferences.
- **Match Notification:** When two users like each other, a match notification is displayed.
- **Chats:** Users can communicate with their matches via the chat feature.
- **Profile Management:** Users can view and edit their profile, including updating their profile picture.
- **Login:** Existing users can log in to access their account.

## Screens Overview

### 1. Registration Screen
- **Purpose:** Allows new users to sign up using email or phone.
- **Features:** 
  - Input fields for email/phone number.
  - Redirection to the questionnaire screen after successful registration.
- **Screenshot:**
  ![Registration Screen](path/to/your/image.png)

### 2. Questionnaire Screen
- **Purpose:** Collects user preferences and interests.
- **Features:**
  - Multiple-choice questions about gender preference, age range, hobbies, and more.
  - Sets the foundation for matching users.
- **Screenshot:**
  ![Questionnaire Screen](path/to/your/image.png)

### 3. Home Screen
- **Purpose:** Displays profiles of potential matches.
- **Features:**
  - Swipe left to dislike, swipe right to like.
  - Filters profiles based on user preferences (gender, age, location).
- **Screenshot:**
  ![Home Screen](path/to/your/image.png)

### 4. Match Notification Screen
- **Purpose:** Informs users when a mutual match occurs.
- **Features:**
  - Displays the matched user's profile picture and name.
  - Option to start a chat immediately.
- **Screenshot:**
  ![Match Notification Screen](path/to/your/image.png)

### 5. Chat Screen
- **Purpose:** Allows users to communicate with their matches.
- **Features:**
  - Real-time messaging.
  - Notifications for new messages.
  - Option to send photos and other media.
- **Screenshot:**
  ![Chat Screen](path/to/your/image.png)

### 6. Profile Screen
- **Purpose:** Allows users to view and edit their profile information.
- **Features:**
  - Display and update profile picture.
  - Edit personal details like name, age, gender, hobbies, and more.
- **Screenshot:**
  ![Profile Screen](path/to/your/image.png)

### 7. Login Screen
- **Purpose:** Allows registered users to log in to their account.
- **Features:**
  - Input fields for email/phone and password.
  - Option to recover forgotten password.
  - Redirection to the home screen after successful login.
- **Screenshot:**
  ![Login Screen](path/to/your/image.png)

## Technologies Used

### Firebase

Ember uses various Firebase services to power its backend and authentication:

- **Firebase Realtime Database:** Used to store and sync user data in real time.
- **Firebase Authentication:** Handles user registration and login via email/phone.
- **Firebase Storage:** Used for uploading and storing user profile pictures.

### Google Maps

- **Google Maps API:** Integrates with the app to display and filter potential matches based on geographical proximity.

## Installation & Setup

To run Ember locally or deploy it, follow these steps:

### Prerequisites

- **Android Studio:** Ensure you have the latest version installed.
- **Firebase:** Set up a Firebase project and integrate it with the app for authentication, database, and storage functionality.
- **Google Maps API Key:** Obtain a key from Google Cloud Console and add it to your project.

### Steps

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/ember.git
