import { useEffect, useState } from 'react'
import type { TextSize } from './design-system'

// Préférences d'affichage de l'utilisateur (taille du texte, contraste élevé).
// Elles ne sont mémorisées dans le navigateur (localStorage) que si l'utilisateur y a consenti ;
// sinon elles valent pour la session en cours uniquement.
interface Prefs {
  size: TextSize
  contrast: boolean
}

const KEY = 'dop.accessibility'
const DEFAULTS: Prefs = { size: 100, contrast: false }

function load(): Prefs {
  try {
    const raw = localStorage.getItem(KEY)
    if (!raw) return DEFAULTS
    const parsed = JSON.parse(raw) as Partial<Prefs>
    const size = parsed.size === 112 || parsed.size === 125 ? parsed.size : 100
    return { size, contrast: parsed.contrast === true }
  } catch {
    // Stockage indisponible (navigation privée, données bloquées) : valeurs par défaut.
    return DEFAULTS
  }
}

export function useAccessibility(persist: boolean) {
  const [prefs, setPrefs] = useState<Prefs>(() => (persist ? load() : DEFAULTS))

  useEffect(() => {
    const root = document.documentElement
    root.style.fontSize = `${prefs.size}%`
    if (prefs.contrast) root.dataset.contrast = 'high'
    else delete root.dataset.contrast
    try {
      if (persist) localStorage.setItem(KEY, JSON.stringify(prefs))
      else localStorage.removeItem(KEY) // consentement refusé ou retiré : on n'en garde rien
    } catch {
      // Ignoré : l'affichage reste appliqué pour la session.
    }
  }, [prefs, persist])

  return {
    size: prefs.size,
    contrast: prefs.contrast,
    setSize: (size: TextSize) => setPrefs((p) => ({ ...p, size })),
    setContrast: (contrast: boolean) => setPrefs((p) => ({ ...p, contrast })),
    reset: () => setPrefs(DEFAULTS),
  }
}
