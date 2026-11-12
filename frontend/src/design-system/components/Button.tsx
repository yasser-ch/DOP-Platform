import type { AnchorHTMLAttributes, ButtonHTMLAttributes } from 'react'

export type ButtonVariant = 'primary' | 'secondary' | 'danger'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant
}

// type="button" par défaut : évite l'envoi involontaire d'un formulaire.
export function Button({ variant = 'primary', className = '', type = 'button', ...rest }: ButtonProps) {
  return <button type={type} className={`ds-btn ds-btn--${variant} ${className}`.trim()} {...rest} />
}

interface ButtonLinkProps extends AnchorHTMLAttributes<HTMLAnchorElement> {
  variant?: ButtonVariant
}

// Un lien qui a l'apparence d'un bouton (navigation, pas action).
export function ButtonLink({ variant = 'primary', className = '', ...rest }: ButtonLinkProps) {
  return <a className={`ds-btn ds-btn--${variant} ${className}`.trim()} {...rest} />
}
