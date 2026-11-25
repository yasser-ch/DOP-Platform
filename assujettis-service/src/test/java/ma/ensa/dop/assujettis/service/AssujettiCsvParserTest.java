package ma.ensa.dop.assujettis.service;

import jakarta.validation.Validation;
import ma.ensa.dop.assujettis.dto.AssujettiRequest;
import ma.ensa.dop.assujettis.exception.CsvImportException;
import ma.ensa.dop.assujettis.exception.InvalidFileException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** S11 - Tests du parseur CSV (sans base de données). */
class AssujettiCsvParserTest {

    private static final String ENTETE = "cin,nom,prenom,email,fonction,organisme,date_entree_fonction\n";

    private final AssujettiCsvParser parser =
        new AssujettiCsvParser(Validation.buildDefaultValidatorFactory().getValidator(), 100);

    private static MockMultipartFile csv(String contenu) {
        return new MockMultipartFile("file", "liste.csv", "text/csv", contenu.getBytes(StandardCharsets.UTF_8));
    }

    @Test void valid_file_is_parsed_and_normalised() {
        List<AssujettiRequest> r = parser.parse(csv(ENTETE
            + "ab123456,Alaoui,Sara,SARA@Test.ma,Chef de service,Ministère X,2026-03-15\n"
            + "CD654321,Benani,Omar,omar@test.ma,Directeur,Ministère Y,2025-01-10\n"));
        assertThat(r).hasSize(2);
        assertThat(r.get(0).cin()).isEqualTo("AB123456");           // majuscules
        assertThat(r.get(0).email()).isEqualTo("sara@test.ma");     // minuscules
        assertThat(r.get(0).dateEntreeFonction()).isEqualTo(LocalDate.of(2026, 3, 15));
    }

    @Test void excel_style_file_semicolon_bom_and_french_dates() {
        String contenu = "﻿cin;nom;prenom;email;fonction;organisme;date_entree_fonction\n"
            + "AB123456;Alaoui;Sara;sara@test.ma;Chef;Ministère X;15/03/2026\n";
        List<AssujettiRequest> r = parser.parse(csv(contenu));
        assertThat(r).hasSize(1);
        assertThat(r.get(0).dateEntreeFonction()).isEqualTo(LocalDate.of(2026, 3, 15));
    }

    @Test void quoted_field_with_comma_is_kept() {
        List<AssujettiRequest> r = parser.parse(csv(ENTETE
            + "AB123456,Alaoui,Sara,sara@test.ma,\"Chef, division A\",Ministère X,2026-03-15\n"));
        assertThat(r.get(0).fonction()).isEqualTo("Chef, division A");
    }

    @Test void missing_column_is_rejected() {
        assertThatThrownBy(() -> parser.parse(csv("cin,nom\nAB123456,Alaoui\n")))
            .isInstanceOf(InvalidFileException.class).hasMessageContaining("Colonnes manquantes");
    }

    @Test void invalid_line_is_reported_with_its_number_and_nothing_is_returned() {
        assertThatThrownBy(() -> parser.parse(csv(ENTETE
            + "AB123456,Alaoui,Sara,sara@test.ma,Chef,Ministère X,2026-03-15\n"
            + "123,Benani,Omar,pas-un-email,Directeur,Ministère Y,2025-01-10\n")))
            .isInstanceOfSatisfying(CsvImportException.class, e -> {
                assertThat(e.getErreurs()).isNotEmpty();
                assertThat(e.getErreurs()).allMatch(err -> err.ligne() == 2);
                assertThat(e.getErreurs()).extracting("champ").contains("cin", "email");
            });
    }

    @Test void bad_date_format_is_reported() {
        assertThatThrownBy(() -> parser.parse(csv(ENTETE
            + "AB123456,Alaoui,Sara,sara@test.ma,Chef,Ministère X,15-03-2026\n")))
            .isInstanceOfSatisfying(CsvImportException.class, e ->
                assertThat(e.getErreurs()).extracting("champ").contains("date_entree_fonction"));
    }

    @Test void duplicate_cin_in_the_same_file_is_rejected() {
        assertThatThrownBy(() -> parser.parse(csv(ENTETE
            + "AB123456,Alaoui,Sara,sara@test.ma,Chef,Ministère X,2026-03-15\n"
            + "ab123456,Benani,Omar,omar@test.ma,Directeur,Ministère Y,2025-01-10\n")))
            .isInstanceOf(CsvImportException.class);
    }

    @Test void formula_injection_is_rejected() {
        assertThatThrownBy(() -> parser.parse(csv(ENTETE
            + "AB123456,=CMD|' /C calc'!A0,Sara,sara@test.ma,Chef,Ministère X,2026-03-15\n")))
            .isInstanceOf(CsvImportException.class);
    }

    @Test void non_csv_extension_and_empty_file_are_rejected() {
        MockMultipartFile exe = new MockMultipartFile("file", "liste.exe", "text/csv", "x".getBytes());
        assertThatThrownBy(() -> parser.parse(exe)).isInstanceOf(InvalidFileException.class);
        assertThatThrownBy(() -> parser.parse(csv(""))).isInstanceOf(InvalidFileException.class);
    }

    @Test void header_only_file_is_rejected() {
        assertThatThrownBy(() -> parser.parse(csv(ENTETE))).isInstanceOf(InvalidFileException.class);
    }

    @Test void too_many_rows_are_rejected() {
        StringBuilder sb = new StringBuilder(ENTETE);
        for (int i = 0; i < 101; i++)
            sb.append("AB%06d,Nom,Prenom,a%d@test.ma,Chef,Org,2026-03-15\n".formatted(i, i));
        assertThatThrownBy(() -> parser.parse(csv(sb.toString()))).isInstanceOf(InvalidFileException.class);
    }
}
