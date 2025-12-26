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

# Copy built static files
COPY --from=build /app/dist /usr/share/nginx/html

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/health || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
