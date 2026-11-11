import type { ReactNode } from 'react'

interface CardProps {
  title?: string
  children: ReactNode
}

export function Card({ title, children }: CardProps) {
  return (
    <section className="ds-card">
      {title && <h3 className="ds-card__title">{title}</h3>}
      {children}
    </section>
  )
}
