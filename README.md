# Rakhesly

Rakhesly is a modern Android application that connects users with supermarkets, allowing them to browse, compare, and order products from multiple stores in Lebanon. The app provides a seamless grocery shopping experience, enabling users to view supermarkets, add products to their cart, compare prices, and track their orders in real-time.

## Features

- **Supermarket Discovery:**
  - Browse a curated list of local supermarkets such as Spinneys, Carrefour, and Le Charcutier.
  - View detailed information for each supermarket, including ratings, address, delivery fees, and opening hours.

- **Product Catalog:**
  - Explore products by category (Dairy, Produce, Meat, Snacks, etc.) with high-quality images and descriptions.
  - View product prices across different supermarkets and compare deals.

- **Cart & Order Management:**
  - Add products to your cart from any supermarket.
  - View and edit your cart, see total prices, and proceed to checkout.
  - Compare the total cost of your cart across all available supermarkets before placing an order.
  - Place orders and view order history.

- **Authentication & Profile:**
  - Register and log in with email and password.
  - User session management with secure authentication (Firebase Auth).
  - Profile management (name, email, phone, address).

- **Order Tracking:**
  - Track your order delivery in real-time on a map.
  - See estimated delivery times and driver progress.

- **UI/UX:**
  - Intuitive navigation with bottom navigation bar and smooth transitions.
  - Responsive layouts for different device sizes.
  - Modern design using Material Components.

## Technology Stack

- **Language:** Java
- **Framework:** Android SDK
- **Architecture:** MVVM (Model-View-ViewModel)
- **Database:** Firebase Firestore (cloud data storage)
- **Authentication:** Firebase Auth
- **Other:** Google Maps SDK, Glide (image loading), Material Design

## Project Structure

```
rakhesly/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/rakhesly/
│   │   │   │   ├── data/        # Data models, repositories, session management
│   │   │   │   ├── ui/          # Activities, fragments, adapters for UI
│   │   │   │   ├── utils/       # Utility classes and constants
│   │   │   │   └── RakheslyApp.java # Application entry point
│   │   │   ├── res/             # Layouts, drawables, values
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── ...
├── build.gradle.kts
└── README.md
```

## Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/rakhesly.git
   ```
2. **Open in Android Studio:**
   - Import the project as an existing Android project.
3. **Configure Firebase:**
   - Add your `google-services.json` file to `app/` for Firebase integration.
4. **Build & Run:**
   - Sync Gradle and build the project.
   - Run the app on an emulator or physical device.

## Contribution

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

1. Fork the repository
2. Create a new branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

- Supermarkets and product images are for demonstration purposes.
- Built using open-source libraries and APIs including Firebase, Google Maps, and Glide.

---

For any questions or support, please contact the repository maintainer.
