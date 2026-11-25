import { useId } from 'react'
import type { InputHTMLAttributes, ReactNode } from 'react'

interface CheckboxFieldProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'type'> {
  /** Texte du libellé ; peut contenir des liens. */
  label: ReactNode
  error?: string
}

// Case à cocher accessible : le libellé est cliquable, l'erreur est reliée à la case pour les lecteurs d'écran.
// Jamais cochée par défaut : un consentement doit être un acte volontaire.
export function CheckboxField({ label, error, id, ...rest }: CheckboxFieldProps) {
  const autoId = useId()
  const inputId = id ?? autoId
  const errorId = `${inputId}-error`
  return (
    <div className="ds-check">
      <label className="ds-check__row" htmlFor={inputId}>
        <input id={inputId} type="checkbox" aria-invalid={error ? true : undefined} aria-describedby={error ? errorId : undefined} {...rest} />
        <span>{label}</span>
      </label>
      {error && (
        <p className="ds-field__error" id={errorId}>
          {error}
        </p>
      )}
    </div>
  )
}
