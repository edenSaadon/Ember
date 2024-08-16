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
![SignUp_Screen](https://github.com/user-attachments/assets/661ac88d-56c9-40ab-a3ca-1f6ecd403855)
![SignUp_Screen_Email](https://github.com/user-attachments/assets/107e1054-c78d-4e45-b7cf-68b5b67bb136)
![SignUp_Screen_Phone](https://github.com/user-attachments/assets/ad52b3ca-99ee-47b6-aa27-a6d04f0b4c27)

### 2. Questionnaire Screen
- **Purpose:** Collects user preferences and interests.
- **Features:**
  - Multiple-choice questions about gender preference, age range, hobbies, and more.
  - Sets the foundation for matching users.
- **Screenshot:**
![Questionerie_Screen1](https://github.com/user-attachments/assets/24e3a962-8234-4b7f-b1fa-98ddc8c517c0)
- **Screenshot:**
![Questionerie_Screen2](https://github.com/user-attachments/assets/5773e4ea-ff68-4fc3-8d2b-31c1723733b4)

### 3. Home Screen
- **Purpose:** Displays profiles of potential matches.
- **Features:**
  - Swipe left to dislike, swipe right to like.
  - Filters profiles based on user preferences (gender, age, location).
- **Screenshot:**
  ![Home_Screen](https://github.com/user-attachments/assets/82d22a63-24ae-4558-ae50-ccdbb44f66ae)

### 4. Match Notification Screen
- **Purpose:** Informs users when a mutual match occurs.
- **Features:**
  - Displays the matched user's profile picture and name.
  - Option to start a chat immediately.
- **Screenshot:**
  ![Match](https://github.com/user-attachments/assets/9decb11a-9fc3-4d53-ab1c-4a397ce965c3)

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
![login_screen](https://github.com/user-attachments/assets/175223ac-f241-498f-a81b-16a2d27db066)
![Login_Screen1](https://github.com/user-attachments/assets/71a89fd8-e7ff-4eba-afda-352eb806d275)
## Technologies Used

### Firebase

Ember uses various Firebase services to power its backend and authentication:

- **Firebase Realtime Database:** Used to store and sync user data in real time.
- **Firebase Authentication:** Handles user registration and login via email/phone.
- **Firebase Storage:** Used for uploading and storing user profile pictures.

### Google Maps

- **Google Maps API:** Integrates with the app to display and filter potential matches based on geographical proximity.
