import { useEffect, useId, useRef, useState } from 'react'

export type TextSize = 100 | 112 | 125

export interface AccessibilityLabels {
  title: string
  open: string
  textSize: string
  contrast: string
  reset: string
  help: string
}

interface AccessibilityMenuProps {
  labels: AccessibilityLabels
  helpHref: string
  size: TextSize
  contrast: boolean
  onSizeChange: (size: TextSize) => void
  onContrastChange: (contrast: boolean) => void
  onReset: () => void
}

const SIZES: { value: TextSize; label: string; aria: string }[] = [
  { value: 100, label: 'A', aria: '100 %' },
  { value: 112, label: 'A+', aria: '112 %' },
  { value: 125, label: 'A++', aria: '125 %' },
]

// Boutons flottants à droite (accessibilité, aide) : taille du texte et contraste élevé.
export function AccessibilityMenu({ labels, helpHref, size, contrast, onSizeChange, onContrastChange, onReset }: AccessibilityMenuProps) {
  const [open, setOpen] = useState(false)
  const panelId = useId()
  const buttonRef = useRef<HTMLButtonElement>(null)

  // Échap referme le panneau et rend le focus au bouton.
  useEffect(() => {
    if (!open) return
    const onKey = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setOpen(false)
        buttonRef.current?.focus()
      }
    }
    document.addEventListener('keydown', onKey)
    return () => document.removeEventListener('keydown', onKey)
  }, [open])

  return (
    <>
      <div className="ds-floating">
        <button
          ref={buttonRef}
          type="button"
          className="ds-floating__btn"
          aria-label={labels.open}
          aria-expanded={open}
          aria-controls={panelId}
          onClick={() => setOpen((value) => !value)}
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <circle cx="12" cy="4.5" r="2" />
            <path d="M5 8.5l7 1.5 7-1.5M12 10v5M9 21l3-6 3 6" />
          </svg>
        </button>
        <a className="ds-floating__btn" href={helpHref} aria-label={labels.help}>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <circle cx="12" cy="12" r="9" />
            <path d="M9.5 9.5a2.5 2.5 0 015 .5c0 1.5-2.5 2-2.5 3.5M12 17h.01" />
          </svg>
        </a>
      </div>

      {open && (
        <div id={panelId} className="ds-a11y-panel" role="region" aria-label={labels.title}>
          <h2>{labels.title}</h2>
          <div className="ds-a11y-row">
            <p>{labels.textSize}</p>
            <div className="ds-a11y-sizes">
              {SIZES.map((item) => (
                <button
                  key={item.value}
                  type="button"
                  className="ds-btn ds-btn--secondary"
                  aria-pressed={size === item.value}
                  aria-label={`${labels.textSize} ${item.aria}`}
                  onClick={() => onSizeChange(item.value)}
                >
                  {item.label}
                </button>
              ))}
            </div>
          </div>
          <div className="ds-a11y-row">
            <label className="ds-a11y-check">
              <input type="checkbox" checked={contrast} onChange={(event) => onContrastChange(event.target.checked)} />
              {labels.contrast}
            </label>
          </div>
          <button type="button" className="ds-btn ds-btn--secondary" onClick={onReset}>
            {labels.reset}
          </button>
        </div>
      )}
    </>
  )
}
