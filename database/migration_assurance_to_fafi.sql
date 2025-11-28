-- =====================================
-- SCRIPT DE MIGRATION : Assurance -> FAFI
-- =====================================
-- Exécuter ce script dans la base de données tily pour migrer les données

-- Renommer la table assurance en fafi
ALTER TABLE IF EXISTS assurance RENAME TO fafi;

-- Renommer la colonne idAssurance en idFafi
ALTER TABLE IF EXISTS fafi RENAME COLUMN idassurance TO idfafi;

-- Renommer la colonne idAssurance en idFafi dans la table personne
ALTER TABLE IF EXISTS personne RENAME COLUMN idassurance TO idfafi;

-- Renommer la contrainte de clé étrangère
ALTER TABLE IF EXISTS personne DROP CONSTRAINT IF EXISTS fk_assurance;
ALTER TABLE IF EXISTS personne ADD CONSTRAINT fk_fafi FOREIGN KEY (idfafi)
    REFERENCES fafi(idfafi);

-- Renommer l'index
DROP INDEX IF EXISTS idx_personne_assurance;
CREATE INDEX IF NOT EXISTS idx_personne_fafi ON personne(idfafi);

