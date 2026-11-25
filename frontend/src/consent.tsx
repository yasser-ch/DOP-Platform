import { createContext, useContext, useMemo, useState } from 'react'
import type { ReactNode } from 'react'

// Consentement au stockage non essentiel dans le navigateur.
//  - Essentiel (toujours actif, sans consentement) : le choix de consentement lui-même, et le jeton de connexion (en mémoire).
//  - Sur consentement : la mémorisation des préférences d'affichage (taille du texte, contraste).
// Aucun suivi publicitaire, aucune mesure d'audience, aucun service tiers.
export type ConsentChoice = 'unset' | 'accepted' | 'refused'

const KEY = 'dop.consent'

function load(): ConsentChoice {
  try {
    const value = localStorage.getItem(KEY)
    return value === 'accepted' || value === 'refused' ? value : 'unset'
  } catch {
    return 'unset'
  }
}

interface ConsentState {
  choice: ConsentChoice
  /** La bannière s'affiche tant qu'aucun choix n'a été fait, ou si l'utilisateur veut le modifier. */
  bannerOpen: boolean
  accept: () => void
  refuse: () => void
  reopen: () => void
}

const ConsentContext = createContext<ConsentState>({
  choice: 'unset',
  bannerOpen: false,
  accept: () => {},
  refuse: () => {},
  reopen: () => {},
})

export function ConsentProvider({ children }: { children: ReactNode }) {
  const [choice, setChoice] = useState<ConsentChoice>(load)
  const [reopened, setReopened] = useState(false)

  const value = useMemo<ConsentState>(() => {
    function save(next: ConsentChoice) {
      setChoice(next)
      setReopened(false)
      try {
        localStorage.setItem(KEY, next)
      } catch {
        // Stockage indisponible : le choix vaut pour la session en cours.
      }
    }
    return {
      choice,
      bannerOpen: choice === 'unset' || reopened,
      accept: () => save('accepted'),
      refuse: () => save('refused'),
      reopen: () => setReopened(true),
    }
  }, [choice, reopened])

  return <ConsentContext.Provider value={value}>{children}</ConsentContext.Provider>
}

export function useConsent(): ConsentState {
  return useContext(ConsentContext)
}
