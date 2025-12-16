-- =====================================
-- MIGRATION : Ajout de la table Dingam-piofanana
-- =====================================
-- Script de migration pour ajouter le support des niveaux de formation (Dingam-piofanana)
-- pour les Mpiandraikitra (Responsables)
-- À exécuter sur une base de données existante

-- =====================================
-- 1. Création de la table dingam_piofanana
-- =====================================
CREATE TABLE IF NOT EXISTS dingam_piofanana (
    idDingamPiofanana SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

-- =====================================
-- 2. Ajout de la colonne à la table personne
-- =====================================
ALTER TABLE personne ADD COLUMN IF NOT EXISTS idDingamPiofanana INTEGER;

-- =====================================
-- 3. Ajout de la contrainte de clé étrangère
-- =====================================
ALTER TABLE personne 
ADD CONSTRAINT fk_dingam_piofanana 
FOREIGN KEY (idDingamPiofanana) 
REFERENCES dingam_piofanana(idDingamPiofanana);

-- =====================================
-- 4. Création de l'index
-- =====================================
CREATE INDEX IF NOT EXISTS idx_personne_dingam_piofanana ON personne(idDingamPiofanana);

-- =====================================
-- 5. Insertion des données de référence
-- =====================================
INSERT INTO dingam_piofanana (nom) VALUES ('Fanomanana');
INSERT INTO dingam_piofanana (nom) VALUES ('Fanaterana');
INSERT INTO dingam_piofanana (nom) VALUES ('Miandry Ravinala');
INSERT INTO dingam_piofanana (nom) VALUES ('Ravinala');
INSERT INTO dingam_piofanana (nom) VALUES ('TP2');

-- =====================================
-- VÉRIFICATION
-- =====================================
-- SELECT * FROM dingam_piofanana;
-- SELECT idDingamPiofanana FROM personne LIMIT 5;

