package tily.mg.service;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tily.mg.entity.*;
import tily.mg.repository.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional
public class ExcelImportService {

    @Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private TypePersonneRepository typePersonneRepository;

    @Autowired
    private SecteurRepository secteurRepository;

    @Autowired
    private FizaranaRepository fizaranaRepository;

    @Autowired
    private AndraikitraRepository andraikitraRepository;

    @Autowired
    private FivondronanaRepository fivondronanaRepository;

    // Fivondronana courant pour l'import (null = admin sans restriction)
    private Integer currentFivondronanaId;

    public static class ImportResult {
        private int totalRows;
        private int successCount;
        private int errorCount;
        private List<String> errors = new ArrayList<>();
        private List<String> successes = new ArrayList<>();

        public int getTotalRows() { return totalRows; }
        public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getErrorCount() { return errorCount; }
        public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
        public List<String> getErrors() { return errors; }
        public List<String> getSuccesses() { return successes; }
    }

    /**
     * Importer des Beazina depuis un fichier Excel ou CSV
     * @param fivondronanaId L'ID du Fivondronana pour les personnes importées (null pour admin)
     */
    public ImportResult importBeazina(MultipartFile file, Integer fivondronanaId) throws IOException {
        this.currentFivondronanaId = fivondronanaId;
        ImportResult result = new ImportResult();
        
        String fileName = file.getOriginalFilename().toLowerCase();
        
        // Vérifier si c'est un fichier CSV
        if (fileName.endsWith(".csv")) {
            return importBeazinaFromCSV(file);
        }
        
        // Sinon, traiter comme Excel
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            result.setTotalRows(sheet.getLastRowNum());
            
            // Lire l'en-tête (première ligne)
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                result.getErrors().add("Le fichier Excel est vide");
                return result;
            }
            
            Map<String, Integer> columnMap = mapColumns(headerRow);
            
            // Valider les colonnes requises pour Beazina
            List<String> requiredColumns = Arrays.asList("nom", "prenom", "secteur", "sampana");
            for (String col : requiredColumns) {
                if (!columnMap.containsKey(col)) {
                    result.getErrors().add("Colonne manquante: " + col);
                }
            }
            
            if (!result.getErrors().isEmpty()) {
                return result;
            }
            
            // Traiter chaque ligne de données
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                // Vérifier si la ligne est vide (toutes les cellules sont vides)
                boolean isEmptyRow = true;
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        String value = getCellValueAsString(row, j);
                        if (value != null && !value.trim().isEmpty()) {
                            isEmptyRow = false;
                            break;
                        }
                    }
                }
                if (isEmptyRow) continue;
                
                try {
                    Personne personne = parseBeazinaRow(row, columnMap, i + 1);
                    if (personne != null) {
                        personneRepository.save(personne);
                        result.setSuccessCount(result.getSuccessCount() + 1);
                        result.getSuccesses().add("Ligne " + (i + 1) + ": " + personne.getNomComplet() + " - Importé avec succès");
                    }
                } catch (Exception e) {
                    result.setErrorCount(result.getErrorCount() + 1);
                    String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                    result.getErrors().add("Ligne " + (i + 1) + ": " + errorMsg);
                }
            }
        }
        
        return result;
    }

    /**
     * Importer des Mpiandraikitra depuis un fichier Excel ou CSV
     * @param fivondronanaId L'ID du Fivondronana pour les personnes importées (null pour admin)
     */
    public ImportResult importMpiandraikitra(MultipartFile file, Integer fivondronanaId) throws IOException {
        this.currentFivondronanaId = fivondronanaId;
        ImportResult result = new ImportResult();
        
        String fileName = file.getOriginalFilename().toLowerCase();
        
        // Vérifier si c'est un fichier CSV
        if (fileName.endsWith(".csv")) {
            return importMpiandraikitraFromCSV(file);
        }
        
        // Sinon, traiter comme Excel
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            result.setTotalRows(sheet.getLastRowNum());
            
            // Lire l'en-tête (première ligne)
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                result.getErrors().add("Le fichier Excel est vide");
                return result;
            }
            
            Map<String, Integer> columnMap = mapColumns(headerRow);
            
            // Valider les colonnes requises pour Mpiandraikitra
            List<String> requiredColumns = Arrays.asList("nom", "prenom", "secteur", "andraikitra");
            List<String> missingColumns = new ArrayList<>();
            for (String col : requiredColumns) {
                if (!columnMap.containsKey(col)) {
                    missingColumns.add(col);
                }
            }
            
            if (!missingColumns.isEmpty()) {
                result.getErrors().add("Colonnes manquantes: " + String.join(", ", missingColumns));
                result.setErrorCount(missingColumns.size());
                return result;
            }
            
            // Traiter chaque ligne de données
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                // Vérifier si la ligne est vide (toutes les cellules sont vides)
                boolean isEmptyRow = true;
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        String value = getCellValueAsString(row, j);
                        if (value != null && !value.trim().isEmpty()) {
                            isEmptyRow = false;
                            break;
                        }
                    }
                }
                if (isEmptyRow) continue;
                
                try {
                    Personne personne = parseMpiandraikitraRow(row, columnMap, i + 1);
                    if (personne != null) {
                        personneRepository.save(personne);
                        result.setSuccessCount(result.getSuccessCount() + 1);
                        result.getSuccesses().add("Ligne " + (i + 1) + ": " + personne.getNomComplet() + " - Importé avec succès");
                    }
                } catch (Exception e) {
                    result.setErrorCount(result.getErrorCount() + 1);
                    String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                    result.getErrors().add("Ligne " + (i + 1) + ": " + errorMsg);
                }
            }
        }
        
        return result;
    }

    private Map<String, Integer> mapColumns(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        for (Cell cell : headerRow) {
            if (cell != null) {
                String columnName = getCellValueAsString(headerRow, cell.getColumnIndex());
                if (columnName != null) {
                    // Normaliser le nom de colonne : minuscule, supprimer espaces et accents
                    columnName = columnName.trim().toLowerCase()
                        .replace(" ", "")
                        .replace("_", "")
                        .replace("-", "")
                        .replace("é", "e")
                        .replace("è", "e")
                        .replace("ê", "e")
                        .replace("à", "a")
                        .replace("â", "a")
                        .replace("ô", "o")
                        .replace("ù", "u")
                        .replace("û", "u");
                    columnMap.put(columnName, cell.getColumnIndex());
                }
            }
        }
        return columnMap;
    }

    private Personne parseBeazinaRow(Row row, Map<String, Integer> columnMap, int rowNum) throws Exception {
        Personne personne = new Personne();
        
        // Champs obligatoires
        String nom = getCellValueAsString(row, columnMap.get("nom"));
        String prenom = getCellValueAsString(row, columnMap.get("prenom"));
        String secteurNom = getCellValueAsString(row, columnMap.get("secteur"));
        String sampanaNom = getCellValueAsString(row, columnMap.get("sampana"));
        
        if (nom == null || nom.trim().isEmpty()) {
            throw new Exception("Le nom est obligatoire");
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new Exception("Le prénom est obligatoire");
        }
        if (secteurNom == null || secteurNom.trim().isEmpty()) {
            throw new Exception("Le secteur est obligatoire");
        }
        if (sampanaNom == null || sampanaNom.trim().isEmpty()) {
            throw new Exception("La sampana (section) est obligatoire");
        }
        
        personne.setNom(nom.trim());
        personne.setPrenom(prenom.trim());
        
        // Type Personne = Eleve (Beazina)
        TypePersonne typeEleve = typePersonneRepository.findByNom("Eleve")
            .orElseThrow(() -> new Exception("Type 'Eleve' non trouvé dans la base"));
        personne.setTypePersonne(typeEleve);
        
        // Secteur
        Secteur secteur = secteurRepository.findByNom(secteurNom.trim())
            .orElseThrow(() -> new Exception("Secteur '" + secteurNom + "' non trouvé"));
        personne.setSecteur(secteur);
        
        // Fizarana (Sampana)
        Fizarana fizarana = fizaranaRepository.findByNom(sampanaNom.trim())
            .orElseThrow(() -> new Exception("Sampana '" + sampanaNom + "' non trouvé"));
        personne.setFizarana(fizarana);
        
        // Ambaratonga (Niveau) - obligatoire pour Beazina
        String ambaratonga = getCellValueAsString(row, columnMap.get("ambaratonga"));
        if (ambaratonga != null && !ambaratonga.trim().isEmpty()) {
            personne.setAmbaratonga(ambaratonga.trim());
        }
        
        // Champs optionnels
        String totem = getCellValueAsString(row, columnMap.get("totem"));
        if (totem != null && !totem.trim().isEmpty()) {
            personne.setTotem(totem.trim());
        }
        
        LocalDate dateNaissance = getCellValueAsDate(row, columnMap.get("datenaissance"));
        if (dateNaissance != null) {
            personne.setDateNaissance(dateNaissance);
        }
        
        String numeroTelephone = getCellValueAsString(row, columnMap.get("numerotelephone"));
        if (numeroTelephone != null && !numeroTelephone.trim().isEmpty()) {
            personne.setNumeroTelephone(numeroTelephone.trim());
        }
        
        String numeroCin = getCellValueAsString(row, columnMap.get("numerocin"));
        if (numeroCin != null && !numeroCin.trim().isEmpty()) {
            personne.setNumeroCin(numeroCin.trim());
        }
        
        String nomPere = getCellValueAsString(row, columnMap.get("nompere"));
        if (nomPere != null && !nomPere.trim().isEmpty()) {
            personne.setNomPere(nomPere.trim());
        }
        
        String nomMere = getCellValueAsString(row, columnMap.get("nommere"));
        if (nomMere != null && !nomMere.trim().isEmpty()) {
            personne.setNomMere(nomMere.trim());
        }
        
        LocalDate dateFanekena = getCellValueAsDate(row, columnMap.get("datefanekena"));
        if (dateFanekena != null) {
            personne.setDateFanekena(dateFanekena);
        }
        
        // FAFI n'est PAS importé depuis Excel - ne pas créer de FAFI
        // Le FAFI sera géré séparément via l'interface d'administration
        personne.setFafi(null);
        
        // S'assurer qu'andraikitra est null pour Beazina
        personne.setAndraikitra(null);
        
        // Affecter le Fivondronana si défini
        if (currentFivondronanaId != null) {
            fivondronanaRepository.findById(currentFivondronanaId)
                .ifPresent(personne::setFivondronana);
        }
        
        // Validation finale
        if (personne.getTypePersonne() == null) {
            throw new Exception("Type de personne non défini");
        }
        if (personne.getSecteur() == null) {
            throw new Exception("Secteur non défini");
        }
        if (personne.getFizarana() == null) {
            throw new Exception("Fizarana (Sampana) non défini");
        }
        
        return personne;
    }

    private Personne parseMpiandraikitraRow(Row row, Map<String, Integer> columnMap, int rowNum) throws Exception {
        Personne personne = new Personne();
        
        // Champs obligatoires
        String nom = getCellValueAsString(row, columnMap.get("nom"));
        String prenom = getCellValueAsString(row, columnMap.get("prenom"));
        String secteurNom = getCellValueAsString(row, columnMap.get("secteur"));
        String andraikitraNom = getCellValueAsString(row, columnMap.get("andraikitra"));
        
        if (nom == null || nom.trim().isEmpty()) {
            throw new Exception("Le nom est obligatoire");
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new Exception("Le prénom est obligatoire");
        }
        if (secteurNom == null || secteurNom.trim().isEmpty()) {
            throw new Exception("Le secteur est obligatoire");
        }
        if (andraikitraNom == null || andraikitraNom.trim().isEmpty()) {
            throw new Exception("L'andraikitra (poste) est obligatoire");
        }
        
        personne.setNom(nom.trim());
        personne.setPrenom(prenom.trim());
        
        // Type Personne = Responsable (Mpiandraikitra)
        TypePersonne typeResponsable = typePersonneRepository.findByNom("Responsable")
            .orElseThrow(() -> new Exception("Type 'Responsable' non trouvé dans la base"));
        personne.setTypePersonne(typeResponsable);
        
        // Secteur
        Secteur secteur = secteurRepository.findByNom(secteurNom.trim())
            .orElseThrow(() -> new Exception("Secteur '" + secteurNom + "' non trouvé"));
        personne.setSecteur(secteur);
        
        // Andraikitra
        Andraikitra andraikitra = andraikitraRepository.findByNom(andraikitraNom.trim())
            .orElseThrow(() -> new Exception("Andraikitra '" + andraikitraNom + "' non trouvé"));
        personne.setAndraikitra(andraikitra);
        
        // S'assurer qu'ambaratonga et fizarana sont null pour Mpiandraikitra
        personne.setAmbaratonga(null);
        personne.setFizarana(null);
        
        // Champs optionnels
        String totem = getCellValueAsString(row, columnMap.get("totem"));
        if (totem != null && !totem.trim().isEmpty()) {
            personne.setTotem(totem.trim());
        }
        
        LocalDate dateNaissance = getCellValueAsDate(row, columnMap.get("datenaissance"));
        if (dateNaissance != null) {
            personne.setDateNaissance(dateNaissance);
        }
        
        String numeroTelephone = getCellValueAsString(row, columnMap.get("numerotelephone"));
        if (numeroTelephone != null && !numeroTelephone.trim().isEmpty()) {
            personne.setNumeroTelephone(numeroTelephone.trim());
        }
        
        String numeroCin = getCellValueAsString(row, columnMap.get("numerocin"));
        if (numeroCin != null && !numeroCin.trim().isEmpty()) {
            personne.setNumeroCin(numeroCin.trim());
        }
        
        String nomPere = getCellValueAsString(row, columnMap.get("nompere"));
        if (nomPere != null && !nomPere.trim().isEmpty()) {
            personne.setNomPere(nomPere.trim());
        }
        
        String nomMere = getCellValueAsString(row, columnMap.get("nommere"));
        if (nomMere != null && !nomMere.trim().isEmpty()) {
            personne.setNomMere(nomMere.trim());
        }
        
        LocalDate dateFanekena = getCellValueAsDate(row, columnMap.get("datefanekena"));
        if (dateFanekena != null) {
            personne.setDateFanekena(dateFanekena);
        }
        
        // FAFI n'est PAS importé depuis Excel - ne pas créer de FAFI
        // Le FAFI sera géré séparément via l'interface d'administration
        personne.setFafi(null);
        
        // Affecter le Fivondronana si défini
        if (currentFivondronanaId != null) {
            fivondronanaRepository.findById(currentFivondronanaId)
                .ifPresent(personne::setFivondronana);
        }
        
        // Validation finale
        if (personne.getTypePersonne() == null) {
            throw new Exception("Type de personne non défini");
        }
        if (personne.getSecteur() == null) {
            throw new Exception("Secteur non défini");
        }
        if (personne.getAndraikitra() == null) {
            throw new Exception("Andraikitra non défini");
        }
        
        return personne;
    }

    private String getCellValueAsString(Row row, Integer columnIndex) {
        if (columnIndex == null) return null;
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return null;
        
        try {
            switch (cell.getCellType()) {
                case STRING:
                    String strValue = cell.getStringCellValue();
                    return (strValue != null && !strValue.trim().isEmpty()) ? strValue.trim() : null;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        // Éviter les décimales inutiles pour les entiers
                        double numValue = cell.getNumericCellValue();
                        if (numValue == (long) numValue) {
                            return String.valueOf((long) numValue);
                        } else {
                            return String.valueOf(numValue);
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    // Pour les formules, essayer d'obtenir la valeur évaluée
                    try {
                        switch (cell.getCachedFormulaResultType()) {
                            case STRING:
                                return cell.getStringCellValue();
                            case NUMERIC:
                                double numValue = cell.getNumericCellValue();
                                if (numValue == (long) numValue) {
                                    return String.valueOf((long) numValue);
                                } else {
                                    return String.valueOf(numValue);
                                }
                            case BOOLEAN:
                                return String.valueOf(cell.getBooleanCellValue());
                            default:
                                return null;
                        }
                    } catch (Exception e) {
                        return null;
                    }
                case BLANK:
                case _NONE:
                    return null;
                default:
                    return null;
            }
        } catch (Exception e) {
            // En cas d'erreur, retourner null plutôt que de planter
            return null;
        }
    }

    private LocalDate getCellValueAsDate(Row row, Integer columnIndex) {
        if (columnIndex == null) return null;
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return null;
        
        try {
            CellType cellType = cell.getCellType();
            
            // Gérer les formules
            if (cellType == CellType.FORMULA) {
                cellType = cell.getCachedFormulaResultType();
                if (cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                } else if (cellType == CellType.STRING) {
                    String dateStr = cell.getStringCellValue().trim();
                    if (dateStr.isEmpty()) return null;
                    return parseDateString(dateStr);
                }
            }
            
            // Gérer les dates numériques
            if (cellType == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                }
            }
            
            // Gérer les dates en texte
            if (cellType == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();
                if (dateStr.isEmpty()) return null;
                return parseDateString(dateStr);
            }
        } catch (Exception e) {
            // Retourner null si la conversion échoue
        }
        return null;
    }
    
    private LocalDate parseDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        dateStr = dateStr.trim();
        
        // Essayer différents formats de date
        String[] formats = {"yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy", "yyyy/MM/dd", "d/M/yyyy", "d-M-yyyy"};
        for (String format : formats) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
                sdf.setLenient(false);
                Date date = sdf.parse(dateStr);
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (Exception e) {
                // Continuer avec le format suivant
            }
        }
        return null;
    }

    /**
     * Importer des Beazina depuis un fichier CSV
     */
    private ImportResult importBeazinaFromCSV(MultipartFile file) throws IOException {
        ImportResult result = new ImportResult();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine();
            if (line == null) {
                result.getErrors().add("Le fichier CSV est vide");
                return result;
            }
            
            // Lire l'en-tête
            String[] headers = parseCSVLine(line);
            Map<String, Integer> columnMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].trim().toLowerCase()
                    .replace(" ", "")
                    .replace("_", "")
                    .replace("-", "")
                    .replace("é", "e")
                    .replace("è", "e")
                    .replace("ê", "e")
                    .replace("à", "a")
                    .replace("â", "a")
                    .replace("ô", "o")
                    .replace("ù", "u")
                    .replace("û", "u");
                columnMap.put(header, i);
            }
            
            // Valider les colonnes requises
            List<String> requiredColumns = Arrays.asList("nom", "prenom", "secteur", "sampana");
            List<String> missingColumns = new ArrayList<>();
            for (String col : requiredColumns) {
                if (!columnMap.containsKey(col)) {
                    missingColumns.add(col);
                }
            }
            
            if (!missingColumns.isEmpty()) {
                result.getErrors().add("Colonnes manquantes: " + String.join(", ", missingColumns));
                result.setErrorCount(missingColumns.size());
                return result;
            }
            
            // Traiter chaque ligne
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                
                try {
                    String[] values = parseCSVLine(line);
                    Personne personne = parseBeazinaRowFromArray(values, columnMap, lineNumber);
                    if (personne != null) {
                        personneRepository.save(personne);
                        result.setSuccessCount(result.getSuccessCount() + 1);
                        result.getSuccesses().add("Ligne " + lineNumber + ": " + personne.getNomComplet() + " - Importé avec succès");
                    }
                } catch (Exception e) {
                    result.setErrorCount(result.getErrorCount() + 1);
                    String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                    result.getErrors().add("Ligne " + lineNumber + ": " + errorMsg);
                }
            }
            
            result.setTotalRows(lineNumber - 1);
        }
        
        return result;
    }

    /**
     * Importer des Mpiandraikitra depuis un fichier CSV
     */
    private ImportResult importMpiandraikitraFromCSV(MultipartFile file) throws IOException {
        ImportResult result = new ImportResult();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine();
            if (line == null) {
                result.getErrors().add("Le fichier CSV est vide");
                return result;
            }
            
            // Lire l'en-tête
            String[] headers = parseCSVLine(line);
            Map<String, Integer> columnMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].trim().toLowerCase()
                    .replace(" ", "")
                    .replace("_", "")
                    .replace("-", "")
                    .replace("é", "e")
                    .replace("è", "e")
                    .replace("ê", "e")
                    .replace("à", "a")
                    .replace("â", "a")
                    .replace("ô", "o")
                    .replace("ù", "u")
                    .replace("û", "u");
                columnMap.put(header, i);
            }
            
            // Valider les colonnes requises
            List<String> requiredColumns = Arrays.asList("nom", "prenom", "secteur", "andraikitra");
            List<String> missingColumns = new ArrayList<>();
            for (String col : requiredColumns) {
                if (!columnMap.containsKey(col)) {
                    missingColumns.add(col);
                }
            }
            
            if (!missingColumns.isEmpty()) {
                result.getErrors().add("Colonnes manquantes: " + String.join(", ", missingColumns));
                result.setErrorCount(missingColumns.size());
                return result;
            }
            
            // Traiter chaque ligne
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                
                try {
                    String[] values = parseCSVLine(line);
                    Personne personne = parseMpiandraikitraRowFromArray(values, columnMap, lineNumber);
                    if (personne != null) {
                        personneRepository.save(personne);
                        result.setSuccessCount(result.getSuccessCount() + 1);
                        result.getSuccesses().add("Ligne " + lineNumber + ": " + personne.getNomComplet() + " - Importé avec succès");
                    }
                } catch (Exception e) {
                    result.setErrorCount(result.getErrorCount() + 1);
                    String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                    result.getErrors().add("Ligne " + lineNumber + ": " + errorMsg);
                }
            }
            
            result.setTotalRows(lineNumber - 1);
        }
        
        return result;
    }

    /**
     * Parser une ligne CSV en tenant compte des guillemets
     */
    private String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(currentValue.toString().trim());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        values.add(currentValue.toString().trim());
        
        return values.toArray(new String[0]);
    }

    /**
     * Parser une ligne Beazina depuis un tableau de valeurs CSV
     */
    private Personne parseBeazinaRowFromArray(String[] values, Map<String, Integer> columnMap, int rowNum) throws Exception {
        Personne personne = new Personne();
        
        // Champs obligatoires
        String nom = getArrayValue(values, columnMap.get("nom"));
        String prenom = getArrayValue(values, columnMap.get("prenom"));
        String secteurNom = getArrayValue(values, columnMap.get("secteur"));
        String sampanaNom = getArrayValue(values, columnMap.get("sampana"));
        
        if (nom == null || nom.trim().isEmpty()) {
            throw new Exception("Le nom est obligatoire");
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new Exception("Le prénom est obligatoire");
        }
        if (secteurNom == null || secteurNom.trim().isEmpty()) {
            throw new Exception("Le secteur est obligatoire");
        }
        if (sampanaNom == null || sampanaNom.trim().isEmpty()) {
            throw new Exception("La sampana (section) est obligatoire");
        }
        
        personne.setNom(nom.trim());
        personne.setPrenom(prenom.trim());
        
        // Type Personne = Eleve (Beazina)
        TypePersonne typeEleve = typePersonneRepository.findByNom("Eleve")
            .orElseThrow(() -> new Exception("Type 'Eleve' non trouvé dans la base"));
        personne.setTypePersonne(typeEleve);
        
        // Secteur
        Secteur secteur = secteurRepository.findByNom(secteurNom.trim())
            .orElseThrow(() -> new Exception("Secteur '" + secteurNom + "' non trouvé"));
        personne.setSecteur(secteur);
        
        // Fizarana (Sampana)
        Fizarana fizarana = fizaranaRepository.findByNom(sampanaNom.trim())
            .orElseThrow(() -> new Exception("Sampana '" + sampanaNom + "' non trouvé"));
        personne.setFizarana(fizarana);
        
        // Ambaratonga (Niveau)
        String ambaratonga = getArrayValue(values, columnMap.get("ambaratonga"));
        if (ambaratonga != null && !ambaratonga.trim().isEmpty()) {
            personne.setAmbaratonga(ambaratonga.trim());
        }
        
        // Champs optionnels
        String totem = getArrayValue(values, columnMap.get("totem"));
        if (totem != null && !totem.trim().isEmpty()) {
            personne.setTotem(totem.trim());
        }
        
        LocalDate dateNaissance = parseDate(getArrayValue(values, columnMap.get("datenaissance")));
        if (dateNaissance != null) {
            personne.setDateNaissance(dateNaissance);
        }
        
        String numeroTelephone = getArrayValue(values, columnMap.get("numerotelephone"));
        if (numeroTelephone != null && !numeroTelephone.trim().isEmpty()) {
            personne.setNumeroTelephone(numeroTelephone.trim());
        }
        
        String numeroCin = getArrayValue(values, columnMap.get("numerocin"));
        if (numeroCin != null && !numeroCin.trim().isEmpty()) {
            personne.setNumeroCin(numeroCin.trim());
        }
        
        String nomPere = getArrayValue(values, columnMap.get("nompere"));
        if (nomPere != null && !nomPere.trim().isEmpty()) {
            personne.setNomPere(nomPere.trim());
        }
        
        String nomMere = getArrayValue(values, columnMap.get("nommere"));
        if (nomMere != null && !nomMere.trim().isEmpty()) {
            personne.setNomMere(nomMere.trim());
        }
        
        LocalDate dateFanekena = parseDate(getArrayValue(values, columnMap.get("datefanekena")));
        if (dateFanekena != null) {
            personne.setDateFanekena(dateFanekena);
        }
        
        // FAFI n'est PAS importé depuis Excel - ne pas créer de FAFI
        // Le FAFI sera géré séparément via l'interface d'administration
        personne.setFafi(null);
        
        personne.setAndraikitra(null);
        
        // Affecter le Fivondronana si défini
        if (currentFivondronanaId != null) {
            fivondronanaRepository.findById(currentFivondronanaId)
                .ifPresent(personne::setFivondronana);
        }
        
        // Validation finale
        if (personne.getTypePersonne() == null) {
            throw new Exception("Type de personne non défini");
        }
        if (personne.getSecteur() == null) {
            throw new Exception("Secteur non défini");
        }
        if (personne.getFizarana() == null) {
            throw new Exception("Fizarana (Sampana) non défini");
        }
        
        return personne;
    }

    /**
     * Parser une ligne Mpiandraikitra depuis un tableau de valeurs CSV
     */
    private Personne parseMpiandraikitraRowFromArray(String[] values, Map<String, Integer> columnMap, int rowNum) throws Exception {
        Personne personne = new Personne();
        
        // Champs obligatoires
        String nom = getArrayValue(values, columnMap.get("nom"));
        String prenom = getArrayValue(values, columnMap.get("prenom"));
        String secteurNom = getArrayValue(values, columnMap.get("secteur"));
        String andraikitraNom = getArrayValue(values, columnMap.get("andraikitra"));
        
        if (nom == null || nom.trim().isEmpty()) {
            throw new Exception("Le nom est obligatoire");
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new Exception("Le prénom est obligatoire");
        }
        if (secteurNom == null || secteurNom.trim().isEmpty()) {
            throw new Exception("Le secteur est obligatoire");
        }
        if (andraikitraNom == null || andraikitraNom.trim().isEmpty()) {
            throw new Exception("L'andraikitra (poste) est obligatoire");
        }
        
        personne.setNom(nom.trim());
        personne.setPrenom(prenom.trim());
        
        // Type Personne = Responsable (Mpiandraikitra)
        TypePersonne typeResponsable = typePersonneRepository.findByNom("Responsable")
            .orElseThrow(() -> new Exception("Type 'Responsable' non trouvé dans la base"));
        personne.setTypePersonne(typeResponsable);
        
        // Secteur
        Secteur secteur = secteurRepository.findByNom(secteurNom.trim())
            .orElseThrow(() -> new Exception("Secteur '" + secteurNom + "' non trouvé"));
        personne.setSecteur(secteur);
        
        // Andraikitra
        Andraikitra andraikitra = andraikitraRepository.findByNom(andraikitraNom.trim())
            .orElseThrow(() -> new Exception("Andraikitra '" + andraikitraNom + "' non trouvé"));
        personne.setAndraikitra(andraikitra);
        
        personne.setAmbaratonga(null);
        personne.setFizarana(null);
        
        // Champs optionnels
        String totem = getArrayValue(values, columnMap.get("totem"));
        if (totem != null && !totem.trim().isEmpty()) {
            personne.setTotem(totem.trim());
        }
        
        LocalDate dateNaissance = parseDate(getArrayValue(values, columnMap.get("datenaissance")));
        if (dateNaissance != null) {
            personne.setDateNaissance(dateNaissance);
        }
        
        String numeroTelephone = getArrayValue(values, columnMap.get("numerotelephone"));
        if (numeroTelephone != null && !numeroTelephone.trim().isEmpty()) {
            personne.setNumeroTelephone(numeroTelephone.trim());
        }
        
        String numeroCin = getArrayValue(values, columnMap.get("numerocin"));
        if (numeroCin != null && !numeroCin.trim().isEmpty()) {
            personne.setNumeroCin(numeroCin.trim());
        }
        
        String nomPere = getArrayValue(values, columnMap.get("nompere"));
        if (nomPere != null && !nomPere.trim().isEmpty()) {
            personne.setNomPere(nomPere.trim());
        }
        
        String nomMere = getArrayValue(values, columnMap.get("nommere"));
        if (nomMere != null && !nomMere.trim().isEmpty()) {
            personne.setNomMere(nomMere.trim());
        }
        
        LocalDate dateFanekena = parseDate(getArrayValue(values, columnMap.get("datefanekena")));
        if (dateFanekena != null) {
            personne.setDateFanekena(dateFanekena);
        }
        
        // FAFI n'est PAS importé depuis Excel - ne pas créer de FAFI
        // Le FAFI sera géré séparément via l'interface d'administration
        personne.setFafi(null);
        
        // Affecter le Fivondronana si défini
        if (currentFivondronanaId != null) {
            fivondronanaRepository.findById(currentFivondronanaId)
                .ifPresent(personne::setFivondronana);
        }
        
        // Validation finale
        if (personne.getTypePersonne() == null) {
            throw new Exception("Type de personne non défini");
        }
        if (personne.getSecteur() == null) {
            throw new Exception("Secteur non défini");
        }
        if (personne.getAndraikitra() == null) {
            throw new Exception("Andraikitra non défini");
        }
        
        return personne;
    }

    private String getArrayValue(String[] array, Integer index) {
        if (index == null || index < 0 || index >= array.length) {
            return null;
        }
        String value = array[index];
        return (value == null || value.trim().isEmpty()) ? null : value;
    }

    private LocalDate parseDate(String dateStr) {
        return parseDateString(dateStr);
    }
}

