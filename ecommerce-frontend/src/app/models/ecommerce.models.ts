export interface Product {
  id?: number;
  barCode?: string;
  name: string;
  description?: string;
  price: number;
  stock: number;
  categoryName?: string;
  brandName?: string;
  isRecommended?: boolean;
  isActive?: boolean;
  mainImageUrl?: string;
  attributes?: ProductAttribute[];
  images?: ProductImage[];
}

export interface Attribute {
  id?: number;
  name: string;
}

export interface ProductAttribute {
  attributeId: number;
  name: string;
  value: string;
}

export interface ProductImage {
  id: number;
  imageUrl: string;
  productId: number;
}

export interface Role {
  id: number;
  description: string;
  isActive: boolean;
}

export interface UserRequest {
  username: string;
  password: string;
  isActive: boolean;
  roles: Role[];
}

export interface UserResponse {
  id: number;
}

export interface UserResponseDto {
  id: number;
  username: string;
  password: string;
  isActive: boolean;
  roles: Role[];
}

export interface AddressRequest {
  type: string | null;
  addressName: string | null;
  residenceNumber: string | null;
  department: string | null;
  province: string | null;
  district: string | null;
  placeReference: string | null;
  postalCode: string | null;
}

export interface CustomerRequest {
  name: string;
  lastName: string;
  gender: string;
  birthdate: string | null;
  email: string;
  phoneNumberOne: string | null;
  phoneNumberTwo: string | null;
  phoneNumberThree: string | null;
  address: AddressRequest;
  isActive: boolean;
  userId?: number;
}

export interface RegisterRequestDto {
  username: string;
  password: string;
  customer: CustomerRequest;
}

export interface CustomerResponseDto {
  id: number;
  name: string;
  lastName: string;
  gender: string;
  birthdate: string | null;
  email: string;
  phoneNumberOne: string | null;
  phoneNumberTwo: string | null;
  phoneNumberThree: string | null;
  addressName: string | null;
  isActive: boolean;
  userRegistration: string | null;
  registrationDate: string | null;
  userModification: string | null;
  modificationDate: string | null;
  userResponseDto: UserResponseDto;
}

export interface Category {
  id?: number;
  description: string;
  isActive?: boolean;
  parentCategory?: number | null;
  subCategories?: Category[];
}

export interface Brand {
  id?: number;
  description: string;
  isActive?: boolean;
}

export interface Order {
  id?: number;
  customerId?: number;
  orderDate?: Date;
  totalAmount: number;
  orderDetails: OrderDetail[];
}

export interface OrderDetail {
  id?: number;
  orderId?: number;
  productId: number;
  productName?: string;
  productImageUrl?: string;
  quantity: number;
  unitPrice: number;
  subtotal?: number;
  totalPrice?: number;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export interface GenericResponse<T> {
  rpta?: number;
  message: string;
  body: T;
}

export interface OrderRequest {
  customerId: number;
  employeeId: number | null;
  orderStatus: {
    id: number;
  };
  totalAmount: number;
  orderNotes: string;
  orderDetails: OrderDetailRequest[];
}

export interface OrderDetailRequest {
  productId: number;
  quantity: number;
  unitPrice: number;
}

export interface OrderResponse {
  id: number;
  orderDate: string;
  customerId: number;
  employeeId: number | null;
  orderStatusName: string;
  totalAmount: number;
  orderNotes: string;
}
