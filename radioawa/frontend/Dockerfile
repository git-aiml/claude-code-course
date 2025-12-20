# Multi-stage Dockerfile for RadioAwa Frontend
# Supports both development and production builds

# Stage 2: Development stage
FROM node:20-alpine AS development
WORKDIR /app

# Upgrade npm to fix package resolution bugs
RUN npm install -g npm@latest

# Copy package files (including lock file from host)
COPY package.json package-lock.json ./

# Install dependencies using the lock file from host system
RUN npm install --legacy-peer-deps

# Copy source code
COPY . .

# Expose Vite dev server port
EXPOSE 5171

# Run Vite dev server with hot reload
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]

# Stage 3: Build stage for production
FROM node:20-alpine AS build
WORKDIR /app

# Upgrade npm to fix package resolution bugs
RUN npm install -g npm@latest

# Copy package files (including lock file from host)
COPY package.json package-lock.json ./

# Install dependencies using the lock file from host system
RUN npm install --legacy-peer-deps

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Stage 4: Production stage with nginx
FROM nginx:alpine AS production

# Copy custom nginx config
COPY --from=build /app/dist /usr/share/nginx/html

# Create nginx configuration for SPA routing
RUN echo 'server { \
    listen 80; \
    server_name _; \
    root /usr/share/nginx/html; \
    index index.html; \
    location / { \
        try_files $uri $uri/ /index.html; \
    } \
    location /api { \
        proxy_pass http://backend:8081; \
        proxy_http_version 1.1; \
        proxy_set_header Upgrade $http_upgrade; \
        proxy_set_header Connection "upgrade"; \
        proxy_set_header Host $host; \
        proxy_cache_bypass $http_upgrade; \
    } \
}' > /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80 || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
