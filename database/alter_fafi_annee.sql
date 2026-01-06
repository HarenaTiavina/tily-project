-- =====================================
-- ALTER TABLE FAFI - Ajout colonne annee
-- =====================================

-- Ajouter la colonne annee à la table fafi
ALTER TABLE fafi ADD COLUMN IF NOT EXISTS annee INTEGER;

-- Mettre à jour les FAFI existants avec l'année de paiement
UPDATE fafi SET annee = EXTRACT(YEAR FROM datePaiement)::INTEGER 
WHERE datePaiement IS NOT NULL AND annee IS NULL;

