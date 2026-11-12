import type { ReactNode } from 'react'
import { Alert, Banner, Breadcrumb } from '../design-system'

interface LegalPageProps {
  title: string
  subtitle: string
  children: ReactNode
  /** Affiche l'avertissement « texte de maquette » (vrai pour tous les textes juridiques pour l'instant). */
  draft?: boolean
}

// Gabarit commun des pages juridiques : fil d'Ariane, bandeau, colonne de lecture étroite.
export function LegalPage({ title, subtitle, children, draft = true }: LegalPageProps) {
  return (
    <>
      <Breadcrumb items={[{ label: 'Accueil', href: '#/' }, { label: title }]} />
      <Banner title={title} subtitle={subtitle} />
      <main className="ds-container" style={{ maxWidth: '48rem' }}>
        {draft && (
          <Alert tone="info" title="Texte de maquette">
            Ce texte est un exemple de structure. Il doit être complété et validé par l'équipe avant toute mise en ligne.
          </Alert>
        )}
        {children}
      </main>
    </>
  )
}
