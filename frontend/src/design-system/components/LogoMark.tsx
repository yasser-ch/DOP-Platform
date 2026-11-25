interface LogoMarkProps {
  /** Joue l'animation d'entrée (désactivée si l'utilisateur a demandé de réduire les animations). */
  animated?: boolean
  className?: string
}

// Étoile à huit branches : deux carrés superposés (rayon intérieur = R * cos45 / cos22.5).
function star(cx: number, cy: number, outer: number): string {
  const inner = (outer * Math.cos(Math.PI / 4)) / Math.cos(Math.PI / 8)
  const points: string[] = []
  for (let k = 0; k < 16; k++) {
    const angle = (k * Math.PI) / 8 - Math.PI / 2
    const radius = k % 2 === 0 ? outer : inner
    points.push(`${(cx + radius * Math.cos(angle)).toFixed(1)},${(cy + radius * Math.sin(angle)).toFixed(1)}`)
  }
  return points.join(' ')
}

const STAR_OUTER = star(627, 230, 168)
const STAR_INNER = star(627, 230, 112)

// Logo de la plateforme en SVG : arche, livre ouvert et étoile. Les couleurs viennent des tokens.
export function LogoMark({ animated = false, className = '' }: LogoMarkProps) {
  return (
    <svg
      className={`ds-logo ${animated ? 'ds-logo--animated' : ''} ${className}`.trim()}
      viewBox="147 40 960 1180"
      aria-hidden="true"
      focusable="false"
    >
      <path
        className="ds-logo__arch"
        pathLength={1}
        d="M224 1155 L224 905 C224 870 250 845 270 808 C240 770 218 710 218 650 C218 450 400 300 627 295 C854 300 1036 450 1036 650 C1036 710 1014 770 984 808 C1004 845 1030 870 1030 905 L1030 1155 Z"
      />
      <g className="ds-logo__star">
        <polygon className="ds-logo__star-outer" points={STAR_OUTER} />
        <polygon className="ds-logo__star-inner" points={STAR_INNER} />
      </g>
      <g className="ds-logo__book">
        <path className="ds-logo__gold" d="M360 632 C470 628 585 668 615 722 L615 1012 C575 962 470 938 360 937 Z" />
        <path className="ds-logo__gold" transform="translate(1254 0) scale(-1 1)" d="M360 632 C470 628 585 668 615 722 L615 1012 C575 962 470 938 360 937 Z" />
        <path className="ds-logo__gold" d="M302 912 L336 903 L336 992 C450 962 565 985 614 1045 L627 1100 L598 1100 C545 1035 430 1015 302 1032 Z" />
        <path className="ds-logo__gold" transform="translate(1254 0) scale(-1 1)" d="M302 912 L336 903 L336 992 C450 962 565 985 614 1045 L627 1100 L598 1100 C545 1035 430 1015 302 1032 Z" />
      </g>
    </svg>
  )
}
