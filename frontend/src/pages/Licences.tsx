import { Table } from '../design-system'
import { LegalPage } from './LegalPage'

function Licences() {
  return (
    <LegalPage title="Licences et crédits" subtitle="Les polices, images et bibliothèques utilisées" draft={false}>
      <h2>Polices</h2>
      <p>Les deux polices sont hébergées avec le site (aucun service externe) et distribuées sous licence libre.</p>
      <Table caption="Polices utilisées">
        <thead>
          <tr>
            <th scope="col">Police</th>
            <th scope="col">Usage</th>
            <th scope="col">Auteurs</th>
            <th scope="col">Licence</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Amiri</td>
            <td>Titres</td>
            <td>The Amiri Project Authors</td>
            <td>
              <a href="/licenses/OFL-Amiri.txt" target="_blank" rel="noopener noreferrer">
                SIL Open Font License 1.1
              </a>
            </td>
          </tr>
          <tr>
            <td>IBM Plex Sans Arabic</td>
            <td>Texte</td>
            <td>IBM Corp.</td>
            <td>
              <a href="/licenses/OFL-IBM-Plex-Sans-Arabic.txt" target="_blank" rel="noopener noreferrer">
                SIL Open Font License 1.1
              </a>
            </td>
          </tr>
        </tbody>
      </Table>

      <h2>Images</h2>
      <ul>
        <li>
          <strong>Logo DOP</strong> : créé par l'équipe du projet avec un outil de génération d'images par IA, puis redessiné
          en SVG. À compléter avant publication : nom de l'outil et vérification que ses conditions d'utilisation autorisent
          l'usage prévu.
        </li>
        <li>
          <strong>Motifs en étoile à huit branches (zellige) et icônes</strong> : dessinés pour le projet, sous la licence du
          projet.
        </li>
        <li>Le site n'utilise aucune photographie ni image de tiers.</li>
      </ul>

      <h2>Bibliothèques</h2>
      <p>
        L'interface est construite avec React (licence MIT), embarqué dans le site. Les outils de développement (Vite,
        TypeScript) ne sont pas distribués avec le site.
      </p>
    </LegalPage>
  )
}

export default Licences
