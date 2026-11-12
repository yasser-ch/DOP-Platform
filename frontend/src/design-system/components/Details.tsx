import type { ReactNode } from 'react'

interface DetailsProps {
  summary: string
  children: ReactNode
}

// Question repliable (élément <details> natif : clavier et lecteurs d'écran gérés par le navigateur).
export function Details({ summary, children }: DetailsProps) {
  return (
    <details className="ds-details">
      <summary>{summary}</summary>
      <div className="ds-details__body">{children}</div>
    </details>
  )
}
