import { useEffect, useMemo, useRef, useState } from 'react'
import type { ComponentType } from 'react'
import { useAccessibility } from './accessibility'
import { ConsentProvider, useConsent } from './consent'
import { AccessibilityMenu, AppFooter, AppHeader, CookieBanner, LanguageSwitch, SkipLink } from './design-system'
import type { FooterColumn, NavLink } from './design-system'
import { LangContext, useT } from './i18n'
import type { Lang } from './i18n'
import Aide from './pages/Aide'
import Assujettis from './pages/Assujettis'
import Conditions from './pages/Conditions'
import Confidentialite from './pages/Confidentialite'
import Cookies from './pages/Cookies'
import Declarant from './pages/Declarant'
import Desabonnement from './pages/Desabonnement'
import Home from './pages/Home'
import Licences from './pages/Licences'
import Login from './pages/Login'
import Mentions from './pages/Mentions'
import NotFound from './pages/NotFound'
import Register from './pages/Register'
import SuppressionDonnees from './pages/SuppressionDonnees'

// Navigation provisoire par adresse "#/..." : le vrai routage (React Router) arrive avec DODP-95.
const ROUTES: Record<string, ComponentType> = {
  '/': Home,
  '/espace-declarant': Declarant,
  '/assujettis': Assujettis,
  '/connexion': Login,
  '/inscription': Register,
  '/aide': Aide,
  '/mentions-legales': Mentions,
  '/confidentialite': Confidentialite,
  '/conditions': Conditions,
  '/cookies': Cookies,
  '/licences': Licences,
  '/desabonnement': Desabonnement,
  '/suppression-donnees': SuppressionDonnees,
}

function currentPath(): string {
  return window.location.hash.replace(/^#/, '') || '/'
}

function Shell({ path, lang, onLangChange }: { path: string; lang: Lang; onLangChange: (lang: Lang) => void }) {
  const t = useT()
  const consent = useConsent()
  const a11y = useAccessibility(consent.choice === 'accepted')
  const contentRef = useRef<HTMLDivElement>(null)
  const previousPath = useRef(path)

  // Après un changement de page, le focus passe au contenu : les utilisateurs du clavier et des lecteurs d'écran
  // ne restent pas bloqués dans le menu.
  useEffect(() => {
    if (previousPath.current !== path) {
      contentRef.current?.focus({ preventScroll: true })
      previousPath.current = path
    }
  }, [path])

  const links: NavLink[] = [
    { href: '#/', label: t('navHome') },
    { href: '#/espace-declarant', label: t('navDeclarant') },
    { href: '#/assujettis', label: t('navAssujettis') },
    { href: '#/connexion', label: t('navLogin') },
  ]
  const columns: FooterColumn[] = [
    { title: t('footerUseful'), items: links.slice(0, 3) },
    {
      title: t('footerInfo'),
      items: [
        { href: '#/aide', label: t('footerHelp') },
        { href: '#/mentions-legales', label: t('footerLegal') },
        { href: '#/confidentialite', label: t('footerPrivacy') },
        { href: '#/conditions', label: t('footerTerms') },
        { href: '#/cookies', label: t('footerCookies') },
        { href: '#/licences', label: t('footerLicenses') },
      ],
    },
    {
      title: t('footerMyData'),
      items: [
        { href: '#/desabonnement', label: t('footerUnsubscribe') },
        { href: '#/suppression-donnees', label: t('footerDeletion') },
        { label: t('footerManageCookies'), onClick: consent.reopen },
      ],
    },
  ]

  const Page = ROUTES[path] ?? NotFound

  return (
    <>
      <SkipLink href={`#${path}`} onActivate={() => contentRef.current?.focus()}>
        {t('skip')}
      </SkipLink>
      {consent.bannerOpen && (
        <CookieBanner
          labels={{
            title: t('cookieTitle'),
            text: t('cookieText'),
            accept: t('cookieAccept'),
            refuse: t('cookieRefuse'),
            more: t('cookieMore'),
          }}
          policyHref="#/cookies"
          onAccept={consent.accept}
          onRefuse={consent.refuse}
        />
      )}
      <AppHeader
        links={links}
        current={`#${path}`}
        utilityMessage={t('utility')}
        tagline={t('tagline')}
        languageSwitch={<LanguageSwitch lang={lang} onChange={onLangChange} />}
      />
      <div id="contenu" ref={contentRef} tabIndex={-1} className="ds-content">
        <Page />
      </div>
      <AppFooter about={t('footerAbout')} columns={columns} note={t('footerNote')} />
      <AccessibilityMenu
        labels={{
          title: t('a11yTitle'),
          open: t('a11yOpen'),
          textSize: t('a11yTextSize'),
          contrast: t('a11yContrast'),
          reset: t('a11yReset'),
          help: t('help'),
        }}
        helpHref="#/aide"
        size={a11y.size}
        contrast={a11y.contrast}
        onSizeChange={a11y.setSize}
        onContrastChange={a11y.setContrast}
        onReset={a11y.reset}
      />
    </>
  )
}

function App() {
  const [path, setPath] = useState(currentPath)
  const [lang, setLang] = useState<Lang>('fr')

  useEffect(() => {
    const onChange = () => {
      setPath(currentPath())
      window.scrollTo(0, 0)
    }
    window.addEventListener('hashchange', onChange)
    return () => window.removeEventListener('hashchange', onChange)
  }, [])

  // L'arabe s'écrit de droite à gauche : on met à jour la langue et le sens de lecture du document.
  useEffect(() => {
    document.documentElement.lang = lang
    document.documentElement.dir = lang === 'ar' ? 'rtl' : 'ltr'
  }, [lang])

  const value = useMemo(() => ({ lang, setLang }), [lang])

  return (
    <ConsentProvider>
      <LangContext.Provider value={value}>
        <Shell path={path} lang={lang} onLangChange={setLang} />
      </LangContext.Provider>
    </ConsentProvider>
  )
}

export default App
