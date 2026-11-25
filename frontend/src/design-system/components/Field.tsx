import { cloneElement, useId } from 'react'
import type { ReactElement } from 'react'

interface FieldProps {
  label: string
  help?: string
  error?: string
  /** Un seul contrôle (Input ou Select) : Field lui injecte id, aria-describedby et aria-invalid. */
  children: ReactElement<{ id?: string; 'aria-describedby'?: string; 'aria-invalid'?: boolean }>
}

// Libellé + aide + erreur, reliés au contrôle pour les lecteurs d'écran.
export function Field({ label, help, error, children }: FieldProps) {
  const id = useId()
  const helpId = `${id}-help`
  const errorId = `${id}-error`
  const describedBy = [help ? helpId : null, error ? errorId : null].filter(Boolean).join(' ')

  return (
    <div className="ds-field">
      <label className="ds-field__label" htmlFor={id}>
        {label}
      </label>
      {help && (
        <p className="ds-field__help" id={helpId}>
          {help}
        </p>
      )}
      {cloneElement(children, {
        id,
        'aria-describedby': describedBy || undefined,
        'aria-invalid': error ? true : undefined,
      })}
      {error && (
        <p className="ds-field__error" id={errorId}>
          {error}
        </p>
      )}
    </div>
  )
}
