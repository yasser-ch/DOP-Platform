import type { ReactNode } from 'react'

export type AlertTone = 'info' | 'success' | 'warning' | 'danger'

interface AlertProps {
  tone?: AlertTone
  title?: string
  children: ReactNode
}

// Les erreurs et avertissements sont annoncés immédiatement (role="alert"), le reste poliment (role="status").
export function Alert({ tone = 'info', title, children }: AlertProps) {
  const role = tone === 'danger' || tone === 'warning' ? 'alert' : 'status'
  return (
    <div className={`ds-alert ds-alert--${tone}`} role={role}>
      <div>
        {title && <p className="ds-alert__title">{title}</p>}
        <div>{children}</div>
      </div>
    </div>
  )
}
