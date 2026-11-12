import type { ReactNode } from 'react'

interface BannerProps {
  title: string
  subtitle?: string
  /** Contenu à l'autre bout du bandeau (par exemple le logo animé de l'accueil). Masqué sur petit écran. */
  aside?: ReactNode
}

// Bandeau de titre de page : bordeaux, coins du bas arrondis, filet safran, motif zellige dans le coin.
export function Banner({ title, subtitle, aside }: BannerProps) {
  return (
    <section className="ds-banner">
      <div className="ds-banner__inner">
        <div>
          <h1 className="ds-banner__title">{title}</h1>
          {subtitle && <p className="ds-banner__subtitle">{subtitle}</p>}
        </div>
        {aside && <div className="ds-banner__aside">{aside}</div>}
      </div>
    </section>
  )
}
