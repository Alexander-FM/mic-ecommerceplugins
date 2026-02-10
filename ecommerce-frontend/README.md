# E-Commerce Frontend Application

A professional e-commerce frontend application built with **Angular 19** and **PrimeNG**, designed to connect to the mic-ecommerceplugins backend gateway.

## 🚀 Features

### Implemented Features (Phase 1)

- ✅ **Professional Navigation Bar** with PrimeNG MenuBar component
- ✅ **Product Catalog** with search and filtering capabilities
  - Filter by name/description
  - Filter by brand
  - Price range filtering
- ✅ **Nested Categories Menu** with unlimited subcategory levels
- ✅ **Shopping Cart** with full functionality
  - Add/remove products
  - Update quantities
  - View subtotal and total
  - Persistent cart (LocalStorage)
- ✅ **Responsive Design** - Works on desktop, tablet, and mobile
- ✅ **Professional UI** using PrimeNG Lara theme

## 📋 Prerequisites

- Node.js 18.x or higher
- npm 9.x or higher
- Angular CLI 19.x

## 🛠️ Installation

1. Navigate to the frontend directory:
```bash
cd ecommerce-frontend
```

2. Install dependencies:
```bash
npm install
```

## 🚀 Development Server

To start a local development server:

```bash
npm start
# or
ng serve
```

Navigate to `http://localhost:4200/`. The application will automatically reload when you make changes to the source files.

## 🔧 Configuration

### Backend Gateway URL

The application connects to the backend gateway at `http://localhost:9089` by default.

To change this, update the `apiUrl` in:
- `src/environments/environment.ts` (development)
- `src/environments/environment.prod.ts` (production)

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:9089'  // Change this to your gateway URL
};
```

### Available API Endpoints

The application integrates with the following backend services:

**Products**: `/maintenance/api/products`
- GET `/active` - Get all active products
- GET `/{id}` - Get product by ID

**Categories**: `/maintenance/api/categories`
- GET `/active` - Get all active categories (with subcategories)
- GET `/{id}` - Get category by ID

**Brands**: `/maintenance/api/brands`
- GET `/active` - Get all active brands
- GET `/{id}` - Get brand by ID

**Orders**: `/orders/api/orders`
- POST `/` - Create new order
- GET `/` - Get all orders
- GET `/{id}` - Get order by ID

## 📦 Building for Production

To build the project for production:

```bash
npm run build
```

The build artifacts will be stored in the `dist/ecommerce-frontend` directory.

## 🎨 Project Structure

```
src/
├── app/
│   ├── models/              # TypeScript interfaces and models
│   │   └── ecommerce.models.ts
│   ├── services/            # Backend API services
│   │   ├── api.service.ts        # Base HTTP client service
│   │   ├── product.service.ts    # Product operations
│   │   ├── category.service.ts   # Category operations
│   │   ├── brand.service.ts      # Brand operations
│   │   └── cart.service.ts       # Shopping cart management
│   ├── pages/               # Page components
│   │   ├── products/        # Product listing page
│   │   └── cart/            # Shopping cart page
│   ├── app.component.*      # Main app component with MenuBar
│   ├── app.config.ts        # App configuration (providers, theme)
│   └── app.routes.ts        # Routing configuration
├── environments/            # Environment configurations
├── styles.scss             # Global styles
└── index.html              # Main HTML file
```

## 🎯 Key Components

### MenuBar Component
- Displays branding and navigation
- Shows nested categories from backend
- Cart badge with item count
- User info and logout button

### Products Page
- Product grid with cards
- Search and filter functionality
- "Add to Cart" and "Buy Now" actions
- Category-based filtering
- Responsive design

### Shopping Cart Page
- Table view of cart items
- Quantity adjustment
- Remove items
- Subtotal and total calculation
- Continue shopping / Checkout options

## 🔄 State Management

The application uses **RxJS BehaviorSubjects** for state management:
- Cart state is managed by `CartService`
- Cart items are persisted in `localStorage`
- Reactive updates across components using Observables

## 🎨 UI/UX

- **Theme**: PrimeNG Lara Light Blue
- **Icons**: PrimeIcons
- **Components**: PrimeNG component library
- **Styling**: SCSS with component-specific styles

## 📱 Responsive Design

The application is fully responsive with breakpoints for:
- Desktop (1400px+)
- Tablet (768px - 1399px)
- Mobile (< 768px)

## 🧪 Testing

Run unit tests:
```bash
npm test
```

## 🚧 Future Enhancements

- User authentication and authorization
- Order checkout flow
- Payment integration
- Product reviews and ratings
- Wishlist functionality
- Order history
- User profile management

## 📚 Technologies Used

- **Angular 19** - Frontend framework
- **PrimeNG 19** - UI component library
- **PrimeIcons** - Icon library
- **RxJS** - Reactive programming
- **TypeScript** - Programming language
- **SCSS** - Styling

## 📝 Additional Resources

- [Angular Documentation](https://angular.dev)
- [PrimeNG Documentation](https://primeng.org)
- [Angular CLI Reference](https://angular.dev/tools/cli)

## 🤝 Contributing

This project is part of the mic-ecommerceplugins monorepo. Please follow the repository's contribution guidelines.

## 📄 License

This project is licensed under the same license as the parent repository.
