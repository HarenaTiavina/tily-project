-- =====================================
-- MIGRATION FAFI ANNUEL
-- =====================================
-- Ce script ajoute la gestion annuelle des FAFI et les prix configurables

-- 1. Ajouter la colonne annee à la table fafi
ALTER TABLE fafi ADD COLUMN IF NOT EXISTS annee INTEGER;

-- 2. Mettre à jour les FAFI existants avec l'année de paiement
UPDATE fafi SET annee = EXTRACT(YEAR FROM datePaiement)::INTEGER WHERE datePaiement IS NOT NULL AND annee IS NULL;

-- 3. Créer la table prix_fafi pour les prix configurables
CREATE TABLE IF NOT EXISTS prix_fafi (
    idPrixFafi SERIAL PRIMARY KEY,
    type_personne VARCHAR(50) NOT NULL, -- 'Mpiandraikitra' ou 'Beazina'
    prix NUMERIC(10,2) NOT NULL,
    annee INTEGER NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(type_personne, annee)
);

-- 4. Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_prix_fafi_type ON prix_fafi(type_personne);
CREATE INDEX IF NOT EXISTS idx_prix_fafi_annee ON prix_fafi(annee);
CREATE INDEX IF NOT EXISTS idx_fafi_annee ON fafi(annee);

-- 5. Insérer les prix par défaut pour l'année courante
INSERT INTO prix_fafi (type_personne, prix, annee, actif)
SELECT 'Mpiandraikitra', 8000.00, EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM prix_fafi 
    WHERE type_personne = 'Mpiandraikitra' 
    AND annee = EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER
);

INSERT INTO prix_fafi (type_personne, prix, annee, actif)
SELECT 'Beazina', 5000.00, EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM prix_fafi 
    WHERE type_personne = 'Beazina' 
    AND annee = EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER
);

-- 6. Commentaires
COMMENT ON TABLE prix_fafi IS 'Prix des FAFI par type de personne et par année';
COMMENT ON COLUMN prix_fafi.type_personne IS 'Type de personne: Mpiandraikitra ou Beazina';
COMMENT ON COLUMN prix_fafi.prix IS 'Prix du FAFI en Ariary';
COMMENT ON COLUMN prix_fafi.annee IS 'Année de validité du prix';
COMMENT ON COLUMN fafi.annee IS 'Année de validité du paiement FAFI';

