interface LanguageSwitchProps {
  lang: 'fr' | 'ar'
  onChange: (lang: 'fr' | 'ar') => void
}

// Sélecteur de langue de la barre utilitaire.
export function LanguageSwitch({ lang, onChange }: LanguageSwitchProps) {
  return (
    <div className="ds-lang" role="group" aria-label="Langue">
      <button type="button" lang="fr" aria-pressed={lang === 'fr'} onClick={() => onChange('fr')}>
        FR
      </button>
      <button type="button" lang="ar" aria-pressed={lang === 'ar'} onClick={() => onChange('ar')}>
        العربية
      </button>
    </div>
  )
}
