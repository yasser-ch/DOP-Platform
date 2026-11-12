export interface CookieBannerLabels {
  title: string
  text: string
  accept: string
  refuse: string
  more: string
}

interface CookieBannerProps {
  labels: CookieBannerLabels
  policyHref: string
  onAccept: () => void
  onRefuse: () => void
}

// Bannière de consentement : « Accepter » et « Refuser » ont le même poids visuel (refuser doit être aussi simple qu'accepter).
export function CookieBanner({ labels, policyHref, onAccept, onRefuse }: CookieBannerProps) {
  return (
    <section className="ds-cookie" aria-label={labels.title}>
      <div className="ds-cookie__inner">
        <div className="ds-cookie__text">
          <h2>{labels.title}</h2>
          <p>
            {labels.text} <a href={policyHref}>{labels.more}</a>
          </p>
        </div>
        <div className="ds-cookie__actions">
          <button type="button" className="ds-btn ds-btn--secondary" onClick={onRefuse}>
            {labels.refuse}
          </button>
          <button type="button" className="ds-btn ds-btn--secondary" onClick={onAccept}>
            {labels.accept}
          </button>
        </div>
      </div>
    </section>
  )
}
