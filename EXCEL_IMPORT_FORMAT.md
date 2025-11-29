# Format Excel pour l'import de données

## Format pour Beazina (Élèves)

### Colonnes obligatoires :
- **nom** : Nom de famille (obligatoire)
- **prenom** : Prénom (obligatoire)
- **secteur** : Nom du secteur (doit exister dans la base de données)
- **sampana** : Nom de la section/sampana (doit exister dans la base de données)

### Colonnes optionnelles :
- **ambaratonga** : Niveau (ex: Louveteau, Éclaireur, Routier)
- **totem** : Totem
- **datenaissance** : Date de naissance (format: yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy, ou yyyy/MM/dd)
- **numerotelephone** : Numéro de téléphone
- **numerocin** : Numéro CIN
- **nompere** : Nom du père
- **nommere** : Nom de la mère
- **datefanekena** : Date de fanekena (format: yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy, ou yyyy/MM/dd)
- **fafidatepaiement** : Date de paiement FAFI (format: yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy, ou yyyy/MM/dd)
- **fafimontant** : Montant FAFI (nombre décimal)
- **fafistatut** : Statut FAFI (Active, Inactive)

### Exemple de fichier Excel pour Beazina :

| nom | prenom | secteur | sampana | ambaratonga | totem | datenaissance | numerotelephone | fafistatut |
|-----|--------|---------|---------|-------------|-------|---------------|-----------------|------------|
| RAKOTO | Aina | Antananarivo | Section 1 | Louveteau | Akanga | 2010-03-15 | 034 12 345 67 | Active |
| RASOA | Marie | Antananarivo | Section 2 | Éclaireur | Fosa | 2008-05-20 | 032 11 222 33 | Inactive |

---

## Format pour Mpiandraikitra (Responsables)

### Colonnes obligatoires :
- **nom** : Nom de famille (obligatoire)
- **prenom** : Prénom (obligatoire)
- **secteur** : Nom du secteur (doit exister dans la base de données)
- **andraikitra** : Nom du poste/responsabilité (doit exister dans la base de données)

### Colonnes optionnelles :
- **totem** : Totem
- **datenaissance** : Date de naissance (format: yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy, ou yyyy/MM/dd)
- **numerotelephone** : Numéro de téléphone
- **numerocin** : Numéro CIN
- **nompere** : Nom du père
- **nommere** : Nom de la mère
- **datefanekena** : Date de fanekena (format: yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy, ou yyyy/MM/dd)
- **fafidatepaiement** : Date de paiement FAFI (format: yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy, ou yyyy/MM/dd)
- **fafimontant** : Montant FAFI (nombre décimal)
- **fafistatut** : Statut FAFI (Active, Inactive)

**Note importante** : Les Mpiandraikitra n'ont PAS de champ "ambaratonga" (niveau) ni "sampana" (section).

### Exemple de fichier Excel pour Mpiandraikitra :

| nom | prenom | secteur | andraikitra | totem | datenaissance | numerotelephone | fafistatut |
|-----|--------|---------|-------------|-------|---------------|-----------------|------------|
| RAKOTO | Jean | Antananarivo | Mpitandrina lehibe | Akanga | 1990-03-15 | 034 12 345 67 | Active |
| RASOA | Pierre | Antananarivo | Mpitandrina faharoa | Fosa | 1985-05-20 | 032 11 222 33 | Inactive |

---

## Règles de validation

1. **Colonnes obligatoires** : Toutes les colonnes obligatoires doivent être présentes dans le fichier Excel
2. **Valeurs existantes** : Les valeurs pour "secteur", "sampana" (Beazina), et "andraikitra" (Mpiandraikitra) doivent exister dans la base de données
3. **Format des dates** : Les dates peuvent être au format :
   - yyyy-MM-dd (ex: 2010-03-15)
   - dd/MM/yyyy (ex: 15/03/2010)
   - dd-MM-yyyy (ex: 15-03-2010)
   - yyyy/MM/dd (ex: 2010/03/15)
4. **Noms de colonnes** : Les noms de colonnes sont insensibles à la casse et aux accents. Par exemple :
   - "nom", "Nom", "NOM" sont équivalents
   - "sampana", "Sampana", "SAMPANA" sont équivalents
   - "dateNaissance", "datenaissance", "Date Naissance" sont équivalents

## Messages d'erreur

En cas d'erreur lors de l'import :
- Les lignes avec erreurs ne seront pas importées
- Un message détaillé indiquera la ligne et la raison de l'erreur
- Les lignes valides seront importées avec succès

## Utilisation

1. Préparez votre fichier Excel avec les colonnes appropriées
2. Sur la page Beazina ou Mpiandraikitra, cliquez sur "Ampidirina avy amin'ny Excel"
3. Sélectionnez votre fichier Excel (.xlsx ou .xls)
4. L'import se fera automatiquement et vous verrez les résultats

