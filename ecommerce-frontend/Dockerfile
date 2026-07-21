# Etapa 1: Compilación
FROM node:20-alpine AS build
WORKDIR /app

# Copiar archivos de dependencias
COPY package*.json ./
RUN npm install

# Copiar el código fuente y compilar
COPY . .
RUN npm run build -- --configuration production

# Etapa 2: Servidor Web (Nginx)
FROM nginx:alpine

# Copiamos la configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Ajustar la ruta según el nombre de tu proyecto en angular.json
COPY --from=build /app/dist/ecommerce-frontend/browser /usr/share/nginx/html

# Copiar configuración de Nginx para manejar rutas de Angular (SPA)
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
