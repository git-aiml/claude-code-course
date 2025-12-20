import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Backend URL: use 'backend' hostname in Docker, localhost otherwise
const backendUrl = process.env.VITE_BACKEND_URL || 'http://backend:8081'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5171,
    host: true, // Listen on all addresses (required for Docker)
    proxy: {
      '/api': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
