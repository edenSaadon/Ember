![home](https://github.com/user-attachments/assets/09ef70f9-9e76-4eed-9fbc-65dfa2b106fe)![home](https://github.com/user-attachments/assets/1cb60ce5-03f2-4b5d-8975-6b37c551d4d7)# Ember - Dating App

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
![signup](https://github.com/user-attachments/assets/e687ebfa-1e8a-48ca-8980-31a1b80e8f53)
![signupEmail](https://github.com/user-attachments/assets/d674e07f-dbb5-4d28-989a-b6f3f7efc683)
![signupPhone](https://github.com/user-attachments/assets/a534d7da-97db-4fef-9d5c-316bec51fac0)

### 2. Questionnaire Screen
- **Purpose:** Collects user preferences and interests.
- **Features:**
  - Multiple-choice questions about gender preference, age range, hobbies, and more.
  - Sets the foundation for matching users.
- **Screenshot:**
![qu1](https://github.com/user-attachments/assets/e882b715-b213-4cb8-8712-0bc4e55674a6)
![qu2](https://github.com/user-attachments/assets/e09b1f29-1334-442c-a763-7f8d6ad839f0)

### 3. Home Screen
- **Purpose:** Displays profiles of potential matches.
- **Features:**
  - Swipe left to dislike, swipe right to like.
  - Filters profiles based on user preferences (gender, age, location).
- **Screenshot:**
![home](https://github.com/user-attachments/assets/6934cbcf-5bac-4594-9bd3-d99e06d7c89b)

### 4. Match Notification Screen
- **Purpose:** Informs users when a mutual match occurs.
- **Features:**
  - Displays the matched user's profile picture and name.
  - Option to start a chat immediately.
- **Screenshot:**
![match](https://github.com/user-attachments/assets/543642aa-5d71-402d-9e74-d3f7f3495ea1)

### 5. Chat Screen
- **Purpose:** Allows users to communicate with their matches.
- **Features:**
  - Real-time messaging.
  - Notifications for new messages.
  - Option to send photos and other media.
- **Screenshot:**
![chats](https://github.com/user-attachments/assets/fde47b55-5abd-4ff4-9e68-ac71206fac53)

### 6. Profile Screen
- **Purpose:** Allows users to view and edit their profile information.
- **Features:**
  - Display and update profile picture.
  - Edit personal details like name, age, gender, hobbies, and more.
- **Screenshot:**
![profile](https://github.com/user-attachments/assets/100737ab-6dc5-4afd-88dd-5fd21c4dbdbf)

### 7. Login Screen
- **Purpose:** Allows registered users to log in to their account.
- **Features:**
  - Input fields for email/phone and password.
  - Option to recover forgotten password.
  - Redirection to the home screen after successful login.
- **Screenshot:**
![log1](https://github.com/user-attachments/assets/74c4d326-f50d-4339-ab9c-14fc07dbfa58)
![login](https://github.com/user-attachments/assets/00b6a0a7-d8de-4b08-aa87-28a5046e9b28)

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
