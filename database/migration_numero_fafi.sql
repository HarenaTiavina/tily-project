-- =====================================
-- SCRIPT DE MIGRATION : Ajout du numéro FAFI
-- =====================================
-- Exécuter ce script sur la base de données existante pour ajouter la colonne numeroFafi
-- à la table fafi

-- Ajouter la colonne numeroFafi à la table fafi si elle n'existe pas
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'fafi' AND column_name = 'numerofafi'
    ) THEN
        ALTER TABLE fafi ADD COLUMN numeroFafi VARCHAR(50);
        RAISE NOTICE 'Colonne numeroFafi ajoutée à la table fafi';
    ELSE
        RAISE NOTICE 'Colonne numeroFafi existe déjà dans la table fafi';
    END IF;
END $$;

-- =====================================
-- VÉRIFICATION
-- =====================================
-- SELECT column_name, data_type, character_maximum_length 
-- FROM information_schema.columns 
-- WHERE table_name = 'fafi';

