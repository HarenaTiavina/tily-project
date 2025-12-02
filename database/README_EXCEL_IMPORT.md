# Fichiers Excel d'exemple pour l'import

## Fichiers disponibles

1. **exemple_beazina.csv** - Exemple pour importer des Beazina (Élèves)
2. **exemple_mpiandraikitra.csv** - Exemple pour importer des Mpiandraikitra (Responsables)

## Comment utiliser ces fichiers

### Option 1 : Utiliser directement les fichiers CSV
Les fichiers CSV peuvent être ouverts dans Excel et sauvegardés au format .xlsx ou .xls.

### Option 2 : Convertir en Excel
1. Ouvrez le fichier CSV dans Excel
2. Faites "Enregistrer sous" (Save As)
3. Choisissez le format "Excel Workbook (.xlsx)" ou "Excel 97-2003 Workbook (.xls)"
4. Enregistrez le fichier

### Option 3 : Générer avec le script Java
Si vous avez Maven installé, vous pouvez exécuter :
```bash
mvn compile exec:java -Dexec.mainClass="tily.mg.util.ExcelExampleGenerator" -Dexec.classpathScope=compile
```

Cela générera automatiquement :
- `database/exemple_beazina.xlsx`
- `database/exemple_mpiandraikitra.xlsx`

## Notes importantes

- Les valeurs pour **secteur**, **sampana** (Beazina), et **andraikitra** (Mpiandraikitra) doivent exister dans la base de données
- Les secteurs disponibles dans la base : Avo, Ira, Miandra, Orimbato, Sompitra
- Les sections (sampana) disponibles : Mavo, Maintso, Mena, Menafify, Sompitra
- Les andraikitra disponibles : Mpitandrina lehibe, Mpitandrina faharoa, Mpitandrina fizarana, Mpitandrina ambaratonga, etc.

## Format des dates

Les dates peuvent être au format :
- yyyy-MM-dd (ex: 2010-03-15)
- dd/MM/yyyy (ex: 15/03/2010)
- dd-MM-yyyy (ex: 15-03-2010)
- yyyy/MM/dd (ex: 2010/03/15)

