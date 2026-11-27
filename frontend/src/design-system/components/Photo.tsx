import { useState } from 'react'

interface PhotoProps {
  /** Nom du fichier sans extension, dans public/images/. */
  name: string
  alt: string
  className?: string
  eager?: boolean
}

// Image d'illustration stockée avec le site. Tant que le fichier n'est pas présent, le motif zellige
// du cadre reste visible (le texte alternatif est conservé pour les lecteurs d'écran).
export function Photo({ name, alt, className = '', eager = false }: PhotoProps) {
  const [missing, setMissing] = useState(false)
  return (
    <span className={`gov-photo ${className}`.trim()} role={missing && alt ? 'img' : undefined} aria-label={missing && alt ? alt : undefined}>
      {!missing && (
        <img src={`/images/${name}.webp`} alt={alt} loading={eager ? 'eager' : 'lazy'} width={1200} height={800} onError={() => setMissing(true)} />
      )}
    </span>
  )
}
