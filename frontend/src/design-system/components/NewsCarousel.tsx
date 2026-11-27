import { useEffect, useState } from 'react'
import type { KeyboardEvent } from 'react'
import { Photo } from './Photo'

export interface Slide {
  date: string
  title: string
  summary: string
  href: string
  image: string
  alt: string
}

const INTERVAL_MS = 7000

const arrow = (direction: 'prev' | 'next') => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" aria-hidden="true">
    <path d={direction === 'prev' ? 'M15 5l-7 7 7 7' : 'M9 5l7 7-7 7'} />
  </svg>
)

// Carrousel d'actualités : panneau sombre à gauche, image à droite. Pause possible, flèches du clavier,
// aucun défilement automatique si l'utilisateur demande de réduire les animations (WCAG 2.2.2).
export function NewsCarousel({ slides }: { slides: Slide[] }) {
  const [index, setIndex] = useState(0)
  const [playing, setPlaying] = useState(() => !window.matchMedia('(prefers-reduced-motion: reduce)').matches)
  const [hovered, setHovered] = useState(false)

  useEffect(() => {
    if (!playing || hovered) return
    const timer = window.setInterval(() => setIndex((value) => (value + 1) % slides.length), INTERVAL_MS)
    return () => window.clearInterval(timer)
  }, [playing, hovered, slides.length])

  // Toute action manuelle arrête le défilement automatique.
  const go = (next: number) => {
    setPlaying(false)
    setIndex((next + slides.length) % slides.length)
  }

  function onKeyDown(event: KeyboardEvent) {
    if (event.key === 'ArrowLeft') go(index - 1)
    if (event.key === 'ArrowRight') go(index + 1)
  }

  const slide = slides[index]
  if (!slide) return null

  return (
    <section
      className="gov-carousel"
      aria-roledescription="carrousel"
      aria-label="Actualités à la une"
      onKeyDown={onKeyDown}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
    >
      <div className="gov-carousel__frame">
        <button type="button" className="gov-carousel__arrow gov-carousel__arrow--prev" aria-label="Actualité précédente" onClick={() => go(index - 1)}>
          {arrow('prev')}
        </button>
        <div className="gov-carousel__slide" aria-live={playing ? 'off' : 'polite'}>
          <div className="gov-carousel__panel">
            <p className="gov-carousel__date">{slide.date}</p>
            <h2>{slide.title}</h2>
            <p>{slide.summary}</p>
            <a className="ds-btn ds-btn--primary" href={slide.href}>
              Lire la suite
            </a>
          </div>
          <Photo key={slide.image} name={slide.image} alt={slide.alt} className="gov-carousel__image" eager />
        </div>
        <button type="button" className="gov-carousel__arrow gov-carousel__arrow--next" aria-label="Actualité suivante" onClick={() => go(index + 1)}>
          {arrow('next')}
        </button>
      </div>
      <div className="gov-carousel__controls">
        <button type="button" className="gov-carousel__pause" onClick={() => setPlaying(!playing)}>
          {playing ? 'Pause' : 'Lecture'}
        </button>
        {slides.map((item, n) => (
          <button
            key={item.title}
            type="button"
            className="gov-carousel__dot"
            aria-label={`Afficher l'actualité ${n + 1} sur ${slides.length}`}
            aria-current={n === index}
            onClick={() => go(n)}
          />
        ))}
      </div>
    </section>
  )
}
