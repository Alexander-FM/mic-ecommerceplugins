# E-Commerce Frontend Application

A professional e-commerce frontend application built with **Angular 19** and **PrimeNG**, designed to connect to the mic-ecommerceplugins backend gateway.

## 🚀 Features

### Implemented Features (Phase 1-2)

**User Features:**
- ✅ **OAuth 2.0 Authentication** with PKCE flow
- ✅ **Professional Navigation Bar** with role-based menu items
- ✅ **Product Catalog** with search and filtering
  - Filter by name/description
  - Filter by brand
  - Price range filtering
  - Category-based navigation (nested subcategories)
- ✅ **Product Detail Page** with:
  - Image carousel gallery
  - Product attributes table
  - Stock availability check
- ✅ **Shopping Cart** with full functionality
  - Add/remove products
  - Update quantities
  - View subtotal and total
  - Persistent cart (LocalStorage)
- ✅ **User Registration** with multi-tab form
  - User account creation
  - Customer profile + address
  - Phone number validation (optional/multiple)
- ✅ **Responsive Design** - Works on desktop, tablet, and mobile

**Admin Features:**
- ✅ **Requisito 8: Product Registration Panel** (ROLE_ADMIN only)
  - **Tab 1:** Product data + main image upload (Google Drive)
  - **Tab 2:** Product attributes selection with custom values
  - **Tab 3:** Product gallery images (up to 4)
  - Secure multi-step workflow with role-based access control
  - Full form validation and error handling

- 🛡️ **Security:**
  - JWT token validation with role checking
  - AuthGuard & AdminGuard for route protection
  - Bearer token in Authorization headers
  - Role-based UI visibility (ROLE_ADMIN only sees admin options)

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

**Products**: `/maintenance/api/products`
- GET `/active` - Get all active products
- GET `/{id}` - Get product by ID
- POST `/` - Create product (ROLE_ADMIN only)
- POST `/attributes` - Save product attributes (ROLE_ADMIN only)
- GET `/attributes` - Get available attributes (ROLE_ADMIN only)
- POST `/images/bulk-upload` - Upload product images (ROLE_ADMIN only)

**Categories**: `/maintenance/api/categories`
- GET `` - Get all categories
- GET `/active` - Get all active categories (with subcategories)
- GET `/{id}` - Get category by ID

**Brands**: `/maintenance/api/brands`
- GET `` - Get all brands
- GET `/active` - Get all active brands
- GET `/{id}` - Get brand by ID

**Authentication**: `/oauth2` (OAuth 2.0 server)
- GET `/authorize` - OAuth authorization endpoint
- POST `/token` - Token exchange endpoint

**Google Drive**: `/maintenance/api/google-drive/images`
- POST `/` - Upload image to Google Drive (ROLE_ADMIN only)

**Users**: `/registration/api/users`
- POST `/` - User registration with customer profile

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
│   ├── guards/                  # Route guards
│   │   ├── auth.guard.ts           # Authentication check
│   │   └── admin.guard.ts          # Admin role check (NEW)
│   ├── models/                  # TypeScript interfaces and models
│   │   ├── ecommerce.models.ts
│   │   └── auth.models.ts
│   ├── services/                # Backend API services
│   │   ├── api.service.ts            # Base HTTP client service
│   │   ├── auth.service.ts           # OAuth & token management
│   │   ├── product.service.ts        # Product operations
│   │   ├── category.service.ts       # Category operations
│   │   ├── brand.service.ts          # Brand operations
│   │   ├── cart.service.ts           # Shopping cart management
│   │   └── admin.service.ts          # Admin operations (NEW)
│   ├── components/              # Reusable components
│   │   └── navbar/              # Navigation component
│   ├── pages/                   # Page components
│   │   ├── products/            # Product listing page
│   │   ├── product-detail/      # Product detail with carousel
│   │   ├── cart/                # Shopping cart page
│   │   ├── login/               # OAuth login page
│   │   ├── auth-callback/       # OAuth redirect handler
│   │   ├── register/            # Registration form (multi-tab)
│   │   └── admin/               # Admin pages
│   │       └── add-product/     # Product registration (NEW)
│   ├── app.component.*          # Main app component
│   ├── app.config.ts            # App configuration (providers, theme)
│   └── app.routes.ts            # Routing configuration
├── environments/                # Environment configurations
├── styles.scss                 # Global styles
└── index.html                  # Main HTML file
```

### New Files for Requisito 8

```
src/app/
├── guards/
│   └── admin.guard.ts                              [NEW]
├── services/
│   └── admin.service.ts                            [NEW]
├── pages/admin/add-product/
│   ├── add-product.component.ts                    [NEW]
│   ├── add-product.component.html                  [NEW]
│   ├── add-product.component.scss                  [NEW]
│   └── add-product.component.spec.ts               [NEW]
└── models/
    ├── auth.models.ts                              [UPDATED - roles array]
    └── ecommerce.models.ts                         [UPDATED - Attribute interface]

Documentation:
├── REQUISITO_8.md                                  [NEW]
└── REQUISITO_8_TESTING.md                          [NEW]
```

## 🎯 Key Components

### Navigation Bar Component
- Displays branding
- Shows user name and logout button (when authenticated)
- **New:** Shows "📦 Registrar Producto" button only for ROLE_ADMIN users
- Responsive menu

### Products Page
- Product grid with cards
- Search and filter functionality
- "Add to Cart" and view details actions
- Category-based filtering
- Responsive design

### Product Detail Page
- Large product image with carousel
- Product attributes in table format
- Stock and price information
- Add to cart functionality
- Related products section

### Shopping Cart Page
- Table view of cart items
- Quantity adjustment
- Remove items
- Subtotal and total calculation
- Continue shopping / Checkout options

### **NEW - Admin Product Registration Page**
- Tab 1: Product data entry form with main image upload
- Tab 2: Product attributes selection and management
- Tab 3: Product gallery images (up to 4)
- Role-based access (ROLE_ADMIN only)
- Multi-step workflow with validation
- Real-time image previews

## 🔄 State Management

The application uses **RxJS BehaviorSubjects** for state management:
- **AuthService:** Manages authentication state and JWT tokens
- **CartService:** Manages shopping cart state
- Cart items are persisted in `localStorage`
- Reactive updates across components using Observables

## 🔐 Security & Authorization

### Authentication Flow
1. User clicks "Conectar" (login)
2. OAuth 2.0 PKCE flow redirects to authorization server
3. User grants permission
4. Browser redirects back with authorization code
5. Frontend exchanges code for JWT tokens (access_token, refresh_token, id_token)
6. Tokens stored in localStorage
7. AuthInterceptor adds Bearer token to all API requests

### Authorization
- **AuthGuard:** Protects routes that require authentication
- **AdminGuard:** Additionally checks for ROLE_ADMIN in JWT
- Role information extracted from JWT payload: `token.roles` array

### Role-Based Access
```typescript
// ROLE_USER (Default)
- Can view products
- Can view product details
- Can manage cart
- Can register account

// ROLE_ADMIN (Special)
- Can do everything ROLE_USER can do
- Can access /admin/products/add
- Can create products
- Can upload images to Google Drive
- Can manage product attributes
```

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

For testing specific features:

### Requisito 8 Testing
See [REQUISITO_8_TESTING.md](./REQUISITO_8_TESTING.md) for comprehensive testing guide for the admin product registration feature.

### Manual Testing Checklist
- [ ] Login with OAuth 2.0 flow
- [ ] View products as regular user
- [ ] Add products to cart
- [ ] (As Admin) Access product registration page
- [ ] (As Admin) Register product with all 3 tabs
- [ ] (As Admin) Verify product appears in listing
- [ ] (As Non-Admin) Verify access denied to /admin routes

## 🚧 Implemented Requisitos

- ✅ **Requisito 1-2:** OAuth 2.0 authentication and navigation
- ✅ **Requisito 3:** Product listing and detail page with carousel
- ✅ **Requisito 4:** User registration with multi-tab form
- ✅ **Requisito 5-6:** Shopping cart functionality
- ✅ **Requisito 8:** Admin product registration panel (NEW)

## 🚧 Future Enhancements

- Order checkout flow
- Payment integration
- Product reviews and ratings
- Wishlist functionality
- Order history
- Advanced user profile management
- Bulk product import
- Product edit/delete for admins

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
