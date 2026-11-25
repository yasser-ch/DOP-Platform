import type { ReactNode } from 'react'
import { LogoMark } from './LogoMark'

export interface NavLink {
  href: string
  label: string
}

interface AppHeaderProps {
  links?: NavLink[]
  /** href du lien actif (marqué avec aria-current). */
  current?: string
  utilityMessage?: string
  tagline?: string
  /** Emplacement du sélecteur de langue, à droite de la barre utilitaire. */
  languageSwitch?: ReactNode
}

// En-tête commun : barre utilitaire (précise le statut du projet), identité de la plateforme, navigation.
// Aucun emblème ni logo d'institution : la plateforme ne représente pas un organisme officiel.
export function AppHeader({
  links = [],
  current,
  utilityMessage = 'Projet académique : cette plateforme ne représente aucune institution officielle.',
  tagline = 'Déclaration obligatoire du patrimoine',
  languageSwitch,
}: AppHeaderProps) {
  return (
    <>
      <div className="ds-utility">
        <div className="ds-utility__inner">
          <p>{utilityMessage}</p>
          {languageSwitch}
        </div>
      </div>
      <header className="ds-header">
        <div className="ds-header__inner">
          <a className="ds-header__brand" href={links[0]?.href ?? '/'}>
            <LogoMark className="ds-header__logo" />
            <span>
              <span className="ds-header__name">DOP Platform</span>
              <span className="ds-header__tag">{tagline}</span>
            </span>
          </a>
          {links.length > 0 && (
            <nav className="ds-nav" aria-label="Navigation principale">
              {links.map((link) => (
                <a key={link.href} href={link.href} aria-current={link.href === current ? 'page' : undefined}>
                  {link.label}
                </a>
              ))}
            </nav>
          )}
        </div>
      </header>
    </>
  )
}
