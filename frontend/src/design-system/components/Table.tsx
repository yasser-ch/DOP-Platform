import type { ReactNode } from 'react'

interface TableProps {
  /** Titre lu par les lecteurs d'écran, affiché au-dessus du tableau. */
  caption: string
  children: ReactNode
}

// Enveloppe stylée : le contenu (thead, tbody, th scope="col") reste du HTML sémantique standard.
export function Table({ caption, children }: TableProps) {
  return (
    <div className="ds-table-wrap">
      <table className="ds-table">
        <caption>{caption}</caption>
        {children}
      </table>
    </div>
  )
}
