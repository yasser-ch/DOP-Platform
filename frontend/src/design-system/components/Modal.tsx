import { useEffect, useId, useRef } from 'react'
import type { ReactNode } from 'react'

interface ModalProps {
  open: boolean
  onClose: () => void
  title: string
  children: ReactNode
  /** Boutons d'action, alignés à droite. */
  footer?: ReactNode
}

// Basé sur <dialog> : le navigateur gère le focus piégé, l'inertie du fond et la touche Échap.
export function Modal({ open, onClose, title, children, footer }: ModalProps) {
  const ref = useRef<HTMLDialogElement>(null)
  const titleId = useId()

  useEffect(() => {
    const dialog = ref.current
    if (!dialog) return
    if (open && !dialog.open) dialog.showModal()
    if (!open && dialog.open) dialog.close()
  }, [open])

  return (
    <dialog ref={ref} className="ds-modal" aria-labelledby={titleId} onClose={onClose}>
      <h2 id={titleId}>{title}</h2>
      <div>{children}</div>
      {footer && <div className="ds-modal__footer">{footer}</div>}
    </dialog>
  )
}
