interface SpinnerProps {
  /** Texte lu par les lecteurs d'écran. */
  label?: string
}

export function Spinner({ label = 'Chargement…' }: SpinnerProps) {
  return (
    <span role="status">
      <span className="ds-spinner" aria-hidden="true" />
      <span className="ds-visually-hidden">{label}</span>
    </span>
  )
}
