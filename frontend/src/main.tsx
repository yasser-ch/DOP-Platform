import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App'
import UiGuide from './pages/UiGuide'

const root = document.getElementById('root')
if (!root) {
  throw new Error('Élément #root introuvable dans index.html')
}

// Pas encore de routeur (ticket DODP-95) : /ui affiche le guide de style, le reste l'application.
const Page = window.location.pathname === '/ui' ? UiGuide : App

createRoot(root).render(
  <StrictMode>
    <Page />
  </StrictMode>,
)
