package ma.ensa.dop.assujettis.service;

import ma.ensa.dop.assujettis.dto.AssujettiRequest;
import ma.ensa.dop.assujettis.dto.AssujettiResponse;
import ma.ensa.dop.assujettis.dto.ImportResultResponse;
import ma.ensa.dop.assujettis.entity.Assujetti;
import ma.ensa.dop.assujettis.entity.ListeImport;
import ma.ensa.dop.assujettis.repository.AssujettiRepository;
import ma.ensa.dop.assujettis.repository.ListeImportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/** S11 (import + liste horodatée) et S13 (liste des échéances à notifier). */
@Service
public class AssujettiService {
    private final AssujettiRepository assujettis;
    private final ListeImportRepository imports;
    private final AssujettiCsvParser parser;
    private final Clock clock;

    public AssujettiService(AssujettiRepository assujettis, ListeImportRepository imports,
                            AssujettiCsvParser parser, Clock clock) {
        this.assujettis = assujettis; this.imports = imports; this.parser = parser; this.clock = clock;
    }

    // ---------- S11 : import ----------

    @Transactional
    public ImportResultResponse importerCsv(MultipartFile file, String par) {
        List<AssujettiRequest> lignes = parser.parse(file);       // lève une exception si invalide
        return enregistrer(lignes, "CSV:" + nomFichierSur(file), par);
    }

    @Transactional
    public ImportResultResponse ajouterParFormulaire(AssujettiRequest req, String par) {
        return enregistrer(List.of(req.normalise()), "FORMULAIRE", par);
    }

    /** Insère les nouveaux CIN, met à jour ceux qui existent déjà (upsert), et trace l'import. */
    private ImportResultResponse enregistrer(List<AssujettiRequest> lignes, String source, String par) {
        Instant maintenant = clock.instant();
        ListeImport imp = imports.save(new ListeImport(source, maintenant, par, lignes.size()));

        int crees = 0, misAJour = 0;
        for (AssujettiRequest l : lignes) {
            Assujetti a = assujettis.findByCin(l.cin()).orElse(null);
            if (a == null) { a = new Assujetti(l.cin()); crees++; } else { misAJour++; }
            a.remplir(l.nom(), l.prenom(), l.email(), l.fonction(), l.organisme(),
                      l.dateEntreeFonction(), maintenant, par, imp.getId());   // recalcule aussi les échéances (S13)
            assujettis.save(a);
        }
        imp.bilan(crees, misAJour);
        return ImportResultResponse.from(imp);
    }

    // ---------- S11 : consultation ----------

    @Transactional(readOnly = true)
    public List<AssujettiResponse> lister() {
        LocalDate today = aujourdHui();
        return assujettis.findAllByOrderByNomAscPrenomAsc().stream()
            .map(a -> AssujettiResponse.from(a, today)).toList();
    }

    @Transactional(readOnly = true)
    public List<ImportResultResponse> historiqueImports() {
        return imports.findAllByOrderByImportedAtDesc().stream().map(ImportResultResponse::from).toList();
    }

    // ---------- S13 : échéances à notifier ----------

    /** Assujettis dont l'échéance de déclaration tombe dans les N prochains jours (ou est déjà dépassée). */
    @Transactional(readOnly = true)
    public List<AssujettiResponse> echeancesAvant(int jours) {
        LocalDate today = aujourdHui();
        return assujettis.findByEcheanceDeclarationLessThanEqualOrderByEcheanceDeclarationAsc(today.plusDays(jours))
            .stream().map(a -> AssujettiResponse.from(a, today)).toList();
    }

    private LocalDate aujourdHui() { return LocalDate.now(clock); }

    /** Nom de fichier nettoyé avant stockage/affichage (pas de caractères exotiques, 100 car. max). */
    private static String nomFichierSur(MultipartFile file) {
        String nom = file.getOriginalFilename() == null ? "import.csv" : file.getOriginalFilename();
        nom = nom.replaceAll("[^A-Za-z0-9._-]", "_");
        return nom.length() > 100 ? nom.substring(nom.length() - 100) : nom;
    }
}
