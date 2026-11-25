import type { ReactNode } from 'react'

interface StatProps {
  value: string
  label: string
}

// Chiffre clé : une grande valeur et son libellé.
export function Stat({ value, label }: StatProps) {
  return (
    <div className="ds-stat">
      <p className="ds-stat__value">{value}</p>
      <p className="ds-stat__label">{label}</p>
    </div>
  )
}

export function StatGrid({ children }: { children: ReactNode }) {
  return <div className="ds-stats">{children}</div>
}
