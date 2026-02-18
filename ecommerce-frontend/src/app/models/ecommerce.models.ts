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
  mainImageUrl?: string;
}

export interface Category {
  id?: number;
  description: string;
  isActive?: boolean;
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
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export interface GenericResponse<T> {
  code: number;
  message: string;
  body: T;
}
