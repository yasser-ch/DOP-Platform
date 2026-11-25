import { createContext, useContext } from 'react'

// Aperçu du multilingue : seuls l'en-tête, le pied de page et le titre de l'accueil sont traduits.
// La traduction complète de l'interface relève du ticket DODP-99 (S85).
// Les textes arabes sont une première version à faire relire par un locuteur natif.
export type Lang = 'fr' | 'ar'

const STRINGS = {
  fr: {
    utility: "Projet académique : cette plateforme ne représente aucune institution officielle.",
    tagline: 'Déclaration obligatoire du patrimoine',
    navHome: 'Accueil',
    navDeclarant: 'Espace déclarant',
    navAssujettis: 'Assujettis',
    navLogin: 'Connexion',
    homeTitle: 'Déclarez votre patrimoine en ligne',
    homeSubtitle: 'Plateforme de déclaration obligatoire du patrimoine',
    footerAbout: 'DOP Platform, projet académique de déclaration obligatoire du patrimoine.',
    footerUseful: 'Liens utiles',
    footerInfo: 'Informations',
    footerHelp: 'Aide et contact',
    footerLegal: 'Mentions légales',
    footerNote: 'Les données affichées dans les maquettes sont fictives.',
    seeMore: 'Voir plus',
    a11yTitle: 'Accessibilité',
    a11yOpen: "Ouvrir le menu d'accessibilité",
    a11yTextSize: 'Taille du texte',
    a11yContrast: 'Contraste élevé',
    a11yReset: 'Réinitialiser',
    help: 'Aide',
    skip: 'Aller au contenu principal',
    footerPrivacy: 'Politique de confidentialité',
    footerTerms: "Conditions d'utilisation",
    footerCookies: 'Politique de cookies',
    footerLicenses: 'Licences et crédits',
    footerMyData: 'Mes données',
    footerUnsubscribe: 'Se désabonner des e-mails',
    footerDeletion: 'Supprimer mes données',
    footerManageCookies: 'Gérer les cookies',
    cookieTitle: 'Cookies et stockage',
    cookieText:
      "Ce site n'utilise que le stockage nécessaire à son fonctionnement. Vous pouvez aussi accepter de mémoriser vos préférences d'affichage (taille du texte, contraste). Aucun suivi publicitaire, aucune mesure d'audience.",
    cookieAccept: 'Accepter la mémorisation',
    cookieRefuse: 'Refuser',
    cookieMore: 'En savoir plus',
  },
  ar: {
    utility: 'مشروع أكاديمي: هذه المنصة لا تمثل أي مؤسسة رسمية.',
    tagline: 'التصريح الإجباري بالممتلكات',
    navHome: 'الرئيسية',
    navDeclarant: 'فضاء المصرّح',
    navAssujettis: 'الخاضعون للتصريح',
    navLogin: 'تسجيل الدخول',
    homeTitle: 'صرّح بممتلكاتك عبر الإنترنت',
    homeSubtitle: 'منصة التصريح الإجباري بالممتلكات',
    footerAbout: 'DOP Platform، مشروع أكاديمي للتصريح الإجباري بالممتلكات.',
    footerUseful: 'روابط مفيدة',
    footerInfo: 'معلومات',
    footerHelp: 'المساعدة والتواصل',
    footerLegal: 'الإشعارات القانونية',
    footerNote: 'المعطيات المعروضة في النماذج الأولية وهمية.',
    seeMore: 'المزيد',
    a11yTitle: 'إمكانية الولوج',
    a11yOpen: 'فتح قائمة إمكانية الولوج',
    a11yTextSize: 'حجم النص',
    a11yContrast: 'تباين عالٍ',
    a11yReset: 'إعادة الضبط',
    help: 'المساعدة',
    skip: 'انتقل إلى المحتوى الرئيسي',
    footerPrivacy: 'سياسة الخصوصية',
    footerTerms: 'شروط الاستخدام',
    footerCookies: 'سياسة ملفات تعريف الارتباط',
    footerLicenses: 'التراخيص والاعتمادات',
    footerMyData: 'معطياتي',
    footerUnsubscribe: 'إلغاء الاشتراك في الرسائل الإلكترونية',
    footerDeletion: 'حذف معطياتي',
    footerManageCookies: 'إدارة ملفات تعريف الارتباط',
    cookieTitle: 'ملفات تعريف الارتباط والتخزين',
    cookieText:
      'يستخدم هذا الموقع فقط التخزين الضروري لعمله. يمكنك أيضا قبول حفظ تفضيلات العرض (حجم النص والتباين). لا تتبع إشهاري ولا قياس للجمهور.',
    cookieAccept: 'قبول الحفظ',
    cookieRefuse: 'رفض',
    cookieMore: 'معرفة المزيد',
  },
} as const

export type StringKey = keyof (typeof STRINGS)['fr']

interface LangState {
  lang: Lang
  setLang: (lang: Lang) => void
}

export const LangContext = createContext<LangState>({ lang: 'fr', setLang: () => {} })

export function useLang(): LangState {
  return useContext(LangContext)
}

export function useT(): (key: StringKey) => string {
  const { lang } = useLang()
  return (key) => STRINGS[lang][key]
}
