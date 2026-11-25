package ma.ensa.dop.assujettis.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import ma.ensa.dop.assujettis.dto.AssujettiRequest;
import ma.ensa.dop.assujettis.dto.RowError;
import ma.ensa.dop.assujettis.exception.CsvImportException;
import ma.ensa.dop.assujettis.exception.InvalidFileException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;

/**
 * S11 - Lit un fichier CSV et le transforme en liste de AssujettiRequest VALIDÉS.
 * Format attendu (en-tête obligatoire, séparateur "," ou ";" détecté automatiquement) :
 *   cin,nom,prenom,email,fonction,organisme,date_entree_fonction
 * Règle "tout ou rien" : si une seule ligne est invalide, rien n'est importé.
 */
@Component
public class AssujettiCsvParser {

    static final List<String> COLONNES =
        List.of("cin", "nom", "prenom", "email", "fonction", "organisme", "date_entree_fonction");

    private static final DateTimeFormatter DATE_FR =
        DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final Validator validator;
    private final int maxLignes;

    public AssujettiCsvParser(Validator validator,
                              @Value("${dop.assujettis.import-max-rows:5000}") int maxLignes) {
        this.validator = validator;
        this.maxLignes = maxLignes;
    }

    public List<AssujettiRequest> parse(MultipartFile file) {
        verifierFichier(file);
        String texte = lire(file);

        List<AssujettiRequest> valides = new ArrayList<>();
        List<RowError> erreurs = new ArrayList<>();
        Set<String> cinVus = new HashSet<>();

        CSVFormat format = CSVFormat.DEFAULT.builder()
            .setDelimiter(detecterSeparateur(texte))
            .setHeader()                 // la 1re ligne est l'en-tête
            .setSkipHeaderRecord(true)
            .setIgnoreHeaderCase(true)
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .build();

        try (CSVParser parser = CSVParser.parse(texte, format)) {
            verifierEntete(parser.getHeaderMap());
            for (CSVRecord rec : parser) {
                int ligne = (int) rec.getRecordNumber();
                if (ligne > maxLignes)
                    throw new InvalidFileException("Trop de lignes (maximum " + maxLignes + ")");

                int avant = erreurs.size();
                LocalDate date = lireDate(rec, ligne, erreurs);
                boolean dateMalFormee = erreurs.size() > avant;   // déjà signalée, inutile de la signaler 2 fois

                AssujettiRequest req = new AssujettiRequest(
                    val(rec, "cin"), val(rec, "nom"), val(rec, "prenom"), val(rec, "email"),
                    val(rec, "fonction"), val(rec, "organisme"), date);

                for (ConstraintViolation<AssujettiRequest> v : validator.validate(req)) {
                    String champ = nomColonne(v.getPropertyPath().toString());
                    if (dateMalFormee && champ.equals("date_entree_fonction")) continue;
                    erreurs.add(new RowError(ligne, champ, v.getMessage()));
                }

                if (erreurs.size() == avant) {
                    AssujettiRequest norm = req.normalise();
                    if (!cinVus.add(norm.cin()))
                        erreurs.add(new RowError(ligne, "cin", "CIN en double dans le fichier"));
                    else
                        valides.add(norm);
                }
            }
        } catch (IOException | UncheckedIOException | IllegalStateException | IllegalArgumentException e) {
            throw new InvalidFileException("Fichier CSV illisible");
        }

        if (!erreurs.isEmpty()) throw new CsvImportException(erreurs);
        if (valides.isEmpty()) throw new InvalidFileException("Le fichier ne contient aucune ligne de données");
        return valides;
    }

    // ---------- helpers ----------

    private static void verifierFichier(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new InvalidFileException("Fichier vide ou absent");
        String nom = file.getOriginalFilename();
        if (nom == null || !nom.toLowerCase(Locale.ROOT).endsWith(".csv"))
            throw new InvalidFileException("Seuls les fichiers .csv sont acceptés");
    }

    private static String lire(MultipartFile file) {
        try {
            String s = new String(file.getBytes(), StandardCharsets.UTF_8);
            return s.startsWith("﻿") ? s.substring(1) : s;   // retire le BOM ajouté par Excel
        } catch (IOException e) {
            throw new InvalidFileException("Fichier CSV illisible");
        }
    }

    /** Excel en français exporte avec ";" ; les autres outils avec ",". */
    private static char detecterSeparateur(String texte) {
        int fin = texte.indexOf('\n');
        String premiere = fin < 0 ? texte : texte.substring(0, fin);
        long pv = premiere.chars().filter(c -> c == ';').count();
        long v = premiere.chars().filter(c -> c == ',').count();
        return pv > v ? ';' : ',';
    }

    private static void verifierEntete(Map<String, Integer> header) {
        Set<String> presentes = new HashSet<>();
        header.keySet().forEach(k -> presentes.add(k.trim().toLowerCase(Locale.ROOT)));
        List<String> manquantes = COLONNES.stream().filter(c -> !presentes.contains(c)).toList();
        if (!manquantes.isEmpty())
            throw new InvalidFileException("Colonnes manquantes : " + String.join(", ", manquantes));
    }

    /** Nom de la propriété Java -> nom de la colonne CSV (pour des messages d'erreur parlants). */
    private static String nomColonne(String propriete) {
        return "dateEntreeFonction".equals(propriete) ? "date_entree_fonction" : propriete;
    }

    private static String val(CSVRecord rec, String colonne) {
        return rec.isSet(colonne) ? rec.get(colonne) : null;
    }

    /** Accepte 2026-03-15 (ISO) ou 15/03/2026 (format français). */
    private static LocalDate lireDate(CSVRecord rec, int ligne, List<RowError> erreurs) {
        String brut = val(rec, "date_entree_fonction");
        if (brut == null || brut.isBlank()) return null;   // @NotNull signalera l'erreur
        try {
            return LocalDate.parse(brut);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(brut, DATE_FR);
            } catch (DateTimeParseException e2) {
                erreurs.add(new RowError(ligne, "date_entree_fonction", "format attendu : yyyy-MM-dd ou dd/MM/yyyy"));
                return null;
            }
        }
    }
}
