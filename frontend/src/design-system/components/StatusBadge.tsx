import type { ReactNode } from 'react'

export type StatusTone = 'success' | 'warning' | 'danger' | 'info' | 'neutral'

interface StatusBadgeProps {
  tone?: StatusTone
  /** Toujours un texte : la couleur seule ne doit pas porter l'information. */
  children: ReactNode
}

export function StatusBadge({ tone = 'neutral', children }: StatusBadgeProps) {
  return <span className={`ds-badge ds-badge--${tone}`}>{children}</span>
}
