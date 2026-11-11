import type { SelectHTMLAttributes } from 'react'

export function Select({ className = '', ...rest }: SelectHTMLAttributes<HTMLSelectElement>) {
  return <select className={`ds-select ${className}`.trim()} {...rest} />
}
