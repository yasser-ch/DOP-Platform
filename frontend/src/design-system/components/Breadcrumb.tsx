export interface Crumb {
  label: string
  /** Absent pour la page courante (dernier élément). */
  href?: string
}

// Fil d'Ariane, très courant sur les portails de l'administration.
export function Breadcrumb({ items }: { items: Crumb[] }) {
  return (
    <nav className="ds-breadcrumb" aria-label="Fil d'Ariane">
      <ol className="ds-breadcrumb__list">
        {items.map((item, index) => (
          <li key={item.label}>
            {item.href && index < items.length - 1 ? (
              <a href={item.href}>{item.label}</a>
            ) : (
              <span aria-current="page">{item.label}</span>
            )}
          </li>
        ))}
      </ol>
    </nav>
  )
}
