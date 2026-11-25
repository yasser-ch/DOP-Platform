import type { ReactNode } from 'react'

interface TileProps {
  title: string
  children: ReactNode
  /** Icône au trait (SVG utilisant currentColor), affichée dans une pastille. */
  icon?: ReactNode
  /** Si fourni, la carte devient un lien avec une ligne « Voir plus ». */
  href?: string
  moreLabel?: string
}

// Carte de service à la manière des portails de l'administration : pastille d'icône, titre, description.
export function Tile({ title, children, icon, href, moreLabel = 'Voir plus' }: TileProps) {
  const content = (
    <>
      {icon && (
        <span className="ds-tile__icon" aria-hidden="true">
          {icon}
        </span>
      )}
      <h3 className="ds-tile__title">{title}</h3>
      <p className="ds-tile__text">{children}</p>
      {href && (
        <span className="ds-tile__more">
          {moreLabel}
          <svg className="ds-tile__arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <path d="M4 12h16M14 6l6 6-6 6" />
          </svg>
        </span>
      )}
    </>
  )
  return href ? (
    <a className="ds-tile" href={href}>
      {content}
    </a>
  ) : (
    <div className="ds-tile">{content}</div>
  )
}

export function TileGrid({ children }: { children: ReactNode }) {
  return <div className="ds-tiles">{children}</div>
}
