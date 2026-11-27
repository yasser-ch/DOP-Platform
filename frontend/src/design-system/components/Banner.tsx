import type { ReactNode } from 'react'

interface BannerProps {
  title: string
  subtitle?: string
  /** Contenu à l'autre bout du bandeau (par exemple le logo animé de l'accueil). Masqué sur petit écran. */
  aside?: ReactNode
  /** Grand format « campagne » (accueil). */
  campaign?: boolean
  children?: ReactNode
}

// Bandeau de titre : fond vert à motif zellige et bandes obliques.
export function Banner({ title, subtitle, aside, campaign = false, children }: BannerProps) {
  return (
    <section className={campaign ? 'ds-banner ds-banner--campaign' : 'ds-banner'}>
      <div className="ds-banner__inner">
        <div>
          <h1 className="ds-banner__title">{title}</h1>
          {subtitle && <p className="ds-banner__subtitle">{subtitle}</p>}
          {children}
        </div>
        {aside && <div className="ds-banner__aside">{aside}</div>}
      </div>
    </section>
  )
}
