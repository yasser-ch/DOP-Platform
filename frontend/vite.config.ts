import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// En développement, le navigateur ne parle qu'à une seule origine (le serveur Vite).
// Vite relaie /api/... vers l'API Gateway (localhost:8080) en retirant le préfixe /api :
// aucune configuration CORS n'est nécessaire côté backend.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
})
