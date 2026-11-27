import { useEffect, useId, useRef, useState } from 'react'
import type { FormEvent, ReactNode } from 'react'

export interface NavLink {
  href: string
  label: string
}

export interface MenuEntry {
  label: string
  items: NavLink[]
}

export interface HeaderLabels {
  disclaimer: string
  brand: string
  tagline: string
  home: string
  menu: string
  login: string
  search: string
  searchPlaceholder: string
  searchEmpty: string
}

const DEFAULT_LABELS: HeaderLabels = {
  disclaimer: 'Projet académique : cette plateforme ne représente aucune institution officielle.',
  brand: 'Plateforme DOP',
  tagline: 'Déclaration obligatoire du patrimoine',
  home: 'Accueil',
  menu: 'Menu',
  login: 'Connexion',
  search: 'Rechercher',
  searchPlaceholder: 'Rechercher une page ou un service',
  searchEmpty: 'Aucun résultat.',
}

interface AppHeaderProps {
  menu?: MenuEntry[]
  /** href de la page courante (marquée avec aria-current). */
  current?: string
  labels?: HeaderLabels
  /** Emplacement du sélecteur de langue, au-dessus de l'en-tête. */
  languageSwitch?: ReactNode
}

const chevron = (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" aria-hidden="true">
    <path d="M6 9l6 6 6-6" />
  </svg>
)

// En-tête institutionnel : avertissement, langues, identité, menu à rubriques, recherche et connexion.
// Le sceau est propre à la plateforme : aucun emblème ni logo d'institution.
export function AppHeader({ menu = [], current, labels = DEFAULT_LABELS, languageSwitch }: AppHeaderProps) {
  const [openMenu, setOpenMenu] = useState<string | null>(null)
  const [mobileOpen, setMobileOpen] = useState(false)
  const [searching, setSearching] = useState(false)
  const [query, setQuery] = useState('')
  const headerRef = useRef<HTMLElement>(null)
  const id = useId()

  const closeAll = () => {
    setOpenMenu(null)
    setMobileOpen(false)
    setSearching(false)
    setQuery('')
  }

  // Un changement de page referme tout ; Échap et un clic à l'extérieur aussi.
  useEffect(closeAll, [current])
  useEffect(() => {
    const onKey = (event: KeyboardEvent) => {
      if (event.key !== 'Escape') return
      const trigger = headerRef.current?.querySelector<HTMLButtonElement>('.gov-menu button[aria-expanded="true"]')
      trigger?.focus()
      closeAll()
    }
    const onPointer = (event: PointerEvent) => {
      if (!headerRef.current?.contains(event.target as Node)) setOpenMenu(null)
    }
    document.addEventListener('keydown', onKey)
    document.addEventListener('pointerdown', onPointer)
    return () => {
      document.removeEventListener('keydown', onKey)
      document.removeEventListener('pointerdown', onPointer)
    }
  }, [])

  // Recherche de maquette : filtre les pages du menu (pas de moteur côté serveur).
  const pages = [...new Map(menu.flatMap((entry) => entry.items).map((item) => [item.href, item])).values()]
  const needle = query.trim().toLocaleLowerCase()
  const results = needle ? pages.filter((page) => page.label.toLocaleLowerCase().includes(needle)) : []

  function submitSearch(event: FormEvent) {
    event.preventDefault()
    if (results[0]) window.location.hash = results[0].href
  }

  return (
    <>
      <p className="gov-disclaimer">{labels.disclaimer}</p>
      {languageSwitch && (
        <div className="gov-langbar">
          <div className="gov-container">{languageSwitch}</div>
        </div>
      )}
      <header className="gov-header" ref={headerRef}>
        <div className="gov-container gov-header__inner">
          <a className="gov-brand" href="#/" aria-label={`${labels.brand} - ${labels.home}`}>
            <img src="/seal.svg" alt="" width={60} height={60} />
            <span>
              <span className="gov-brand__name">{labels.brand}</span>
              <span className="gov-brand__tag">{labels.tagline}</span>
            </span>
          </a>

          {menu.length > 0 && (
            <nav id={`${id}-nav`} className={`gov-menu${mobileOpen ? ' gov-menu--open' : ''}`} aria-label={labels.menu}>
              <ul>
                {menu.map((entry, index) => (
                  <li key={entry.label}>
                    <button
                      type="button"
                      aria-expanded={openMenu === entry.label}
                      aria-controls={`${id}-sub-${index}`}
                      onClick={() => setOpenMenu(openMenu === entry.label ? null : entry.label)}
                    >
                      {entry.label}
                      {chevron}
                    </button>
                    <ul id={`${id}-sub-${index}`} className="gov-submenu" hidden={openMenu !== entry.label}>
                      {entry.items.map((item) => (
                        <li key={item.label}>
                          <a href={item.href} aria-current={item.href === current ? 'page' : undefined} onClick={closeAll}>
                            {item.label}
                          </a>
                        </li>
                      ))}
                    </ul>
                  </li>
                ))}
              </ul>
            </nav>
          )}

          {menu.length > 0 && (
            <div className="gov-header__actions">
              <button
                type="button"
                className="gov-iconbtn"
                aria-label={labels.search}
                aria-expanded={searching}
                aria-controls={`${id}-search`}
                onClick={() => setSearching(!searching)}
              >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
                  <circle cx="11" cy="11" r="7" />
                  <path d="M20 20l-4-4" />
                </svg>
              </button>
              <a className="ds-btn ds-btn--primary gov-login" href="#/connexion">
                {labels.login}
              </a>
              <button
                type="button"
                className="gov-iconbtn gov-burger"
                aria-label={labels.menu}
                aria-expanded={mobileOpen}
                aria-controls={`${id}-nav`}
                onClick={() => setMobileOpen(!mobileOpen)}
              >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
                  <path d={mobileOpen ? 'M6 6l12 12M18 6L6 18' : 'M4 7h16M4 12h16M4 17h16'} />
                </svg>
              </button>
            </div>
          )}
        </div>

        {searching && (
          <form id={`${id}-search`} className="gov-search" role="search" onSubmit={submitSearch}>
            <div className="gov-container">
              <input
                type="search"
                className="ds-input"
                autoFocus
                aria-label={labels.searchPlaceholder}
                placeholder={labels.searchPlaceholder}
                value={query}
                onChange={(event) => setQuery(event.target.value)}
              />
              <div aria-live="polite">
                {needle && results.length === 0 && <p>{labels.searchEmpty}</p>}
                {results.length > 0 && (
                  <ul>
                    {results.map((page) => (
                      <li key={page.href}>
                        <a href={page.href} onClick={closeAll}>
                          {page.label}
                        </a>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </div>
          </form>
        )}
      </header>
    </>
  )
}
