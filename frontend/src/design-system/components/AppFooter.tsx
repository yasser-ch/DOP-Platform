export type FooterItem = { label: string; href: string } | { label: string; onClick: () => void }

export interface FooterColumn {
  title: string
  items: FooterItem[]
}

interface AppFooterProps {
  about: string
  columns: FooterColumn[]
  note: string
}

// Pied de page complet à la manière des portails de l'administration : présentation, listes de liens, mention.
// Un élément peut être un lien (href) ou une action (onClick), par exemple « Gérer les cookies ».
export function AppFooter({ about, columns, note }: AppFooterProps) {
  return (
    <footer className="ds-footer">
      <div className="ds-footer__inner">
        <div className="ds-footer__grid">
          <p className="ds-footer__about">{about}</p>
          {columns.map((column) => (
            <nav key={column.title} aria-label={column.title}>
              <h2 className="ds-footer__title">{column.title}</h2>
              <ul className="ds-footer__links">
                {column.items.map((item) => (
                  <li key={item.label}>
                    {'href' in item ? (
                      <a href={item.href}>{item.label}</a>
                    ) : (
                      <button type="button" className="ds-footer__linkbtn" onClick={item.onClick}>
                        {item.label}
                      </button>
                    )}
                  </li>
                ))}
              </ul>
            </nav>
          ))}
        </div>
        <p className="ds-footer__note">{note}</p>
      </div>
    </footer>
  )
}
