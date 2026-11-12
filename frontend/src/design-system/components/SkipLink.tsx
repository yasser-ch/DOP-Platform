import type { ReactNode } from 'react'

interface SkipLinkProps {
  /** href du lien (l'adresse courante, pour ne pas changer de page). */
  href: string
  onActivate: () => void
  children: ReactNode
}

// Premier élément focalisable de la page : permet aux utilisateurs du clavier de passer l'en-tête.
export function SkipLink({ href, onActivate, children }: SkipLinkProps) {
  return (
    <a
      className="ds-skip"
      href={href}
      onClick={(event) => {
        event.preventDefault()
        onActivate()
      }}
    >
      {children}
    </a>
  )
}
