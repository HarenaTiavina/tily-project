-- =====================================
-- MIGRATION COMPLÈTE : Modules Fiofanana et Filoha
-- =====================================
-- Ce script regroupe toutes les migrations pour :
-- 1. Module Filoha (type_filoha, rôles Filoha)
-- 2. Module Details Fiofanana (détails de formation pour tous les types)
-- 
-- Ordre d'exécution :
-- 1. Création de type_filoha et modifications personne/utilisateur
-- 2. Insertion du type "Filoha" dans type_personne
-- 3. Création de la table details_fiofanana
-- 4. Ajout de idtypefiofanana si nécessaire
-- 5. Ajout des colonnes pour Diniky ny filoha et Filasiana
-- 6. Ajout des colonnes pour Ravinala et TP2
-- =====================================

-- =====================================
-- PARTIE 1 : MODULE FILOHA
-- =====================================

-- =====================================
-- TABLE : type_filoha
-- Types/rôles de Filoha
-- =====================================
CREATE TABLE IF NOT EXISTS type_filoha (
    idtypefiloha SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_type_filoha_nom ON type_filoha(nom);

-- =====================================
-- MODIFICATION : personne
-- Ajouter la colonne idtypefiloha
-- =====================================
ALTER TABLE IF EXISTS personne ADD COLUMN IF NOT EXISTS idtypefiloha INTEGER;

-- Ajouter la clé étrangère
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_personne_type_filoha'
    ) THEN
        ALTER TABLE personne
        ADD CONSTRAINT fk_personne_type_filoha 
        FOREIGN KEY (idtypefiloha)
        REFERENCES type_filoha(idtypefiloha) ON DELETE SET NULL;
    END IF;
END $$;

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_personne_type_filoha ON personne(idtypefiloha);

-- =====================================
-- MODIFICATION : utilisateur
-- Ajouter la colonne idpersonne pour lier aux Filoha
-- =====================================
ALTER TABLE IF EXISTS utilisateur ADD COLUMN IF NOT EXISTS idpersonne INTEGER;

-- Ajouter la clé étrangère
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_utilisateur_personne'
    ) THEN
        ALTER TABLE utilisateur
        ADD CONSTRAINT fk_utilisateur_personne 
        FOREIGN KEY (idpersonne)
        REFERENCES personne(idpersonne) ON DELETE SET NULL;
    END IF;
END $$;

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_utilisateur_personne ON utilisateur(idpersonne);

-- =====================================
-- INSERTION DU TYPE PERSONNE "Filoha"
-- =====================================
INSERT INTO type_personne (nom)
SELECT 'Filoha'
WHERE NOT EXISTS (
    SELECT 1 FROM type_personne WHERE nom = 'Filoha'
);

-- =====================================
-- PARTIE 2 : MODULE DETAILS FIOFANANA
-- =====================================

-- =====================================
-- TABLE : type_fiofanana
-- Types de formation (fanomababa, fanaterana, ravinala, TP2)
-- =====================================
CREATE TABLE IF NOT EXISTS type_fiofanana (
    idtypefiofanana SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_type_fiofanana_nom ON type_fiofanana(nom);

-- Insertion des types de base (si la table est vide)
-- Note: L'application Java crée automatiquement ces types si la table est vide
-- Cette insertion est optionnelle mais peut être utile pour initialiser la base
INSERT INTO type_fiofanana (nom)
SELECT 'fanomababa'
WHERE NOT EXISTS (SELECT 1 FROM type_fiofanana WHERE nom = 'fanomababa');

INSERT INTO type_fiofanana (nom)
SELECT 'fanaterana'
WHERE NOT EXISTS (SELECT 1 FROM type_fiofanana WHERE nom = 'fanaterana');

INSERT INTO type_fiofanana (nom)
SELECT 'ravinala'
WHERE NOT EXISTS (SELECT 1 FROM type_fiofanana WHERE nom = 'ravinala');

INSERT INTO type_fiofanana (nom)
SELECT 'TP2'
WHERE NOT EXISTS (SELECT 1 FROM type_fiofanana WHERE nom = 'TP2');

-- =====================================
-- TABLE : details_fiofanana
-- Détails de formation (fiofanana) pour les Mpiandraikitra
-- Sections A, B, C pour les types fanomababa et fanaterana
-- =====================================
CREATE TABLE IF NOT EXISTS details_fiofanana (
    iddetailsfiofanana SERIAL PRIMARY KEY,
    idpersonne INTEGER NOT NULL,
    idtypefiofanana INTEGER NOT NULL,
    
    -- Section A: Asan'ny tsirairay
    asan1 VARCHAR(255),
    asan2 VARCHAR(255),
    asan3 VARCHAR(255),
    asan4 VARCHAR(255),
    asan5 VARCHAR(255),
    asan6 VARCHAR(255),
    asan7 VARCHAR(255),
    asanfilohananome VARCHAR(100),
    
    -- Section B: Ezakin'ny mpiofana
    ezaka1 VARCHAR(255),
    ezaka2 VARCHAR(255),
    ezaka3 VARCHAR(255),
    ezaka4 VARCHAR(255),
    ezaka5 VARCHAR(255),
    ezaka6 VARCHAR(255),
    ezaka7 VARCHAR(255),
    ezakafilohananome VARCHAR(100),
    
    -- Section C: Bitsiky ny fivondronana
    bitsikydaty1 DATE,
    bitsikyfivondronana1 INTEGER,
    bitsikydaty2 DATE,
    bitsikyfivondronana2 INTEGER,
    
    CONSTRAINT fk_details_fiofanana_personne FOREIGN KEY (idpersonne)
        REFERENCES personne(idpersonne) ON DELETE CASCADE,
    CONSTRAINT fk_details_fiofanana_type_fiofanana FOREIGN KEY (idtypefiofanana)
        REFERENCES type_fiofanana(idtypefiofanana) ON DELETE CASCADE,
    CONSTRAINT fk_details_fiofanana_fivondronana1 FOREIGN KEY (bitsikyfivondronana1)
        REFERENCES fivondronana(idfivondronana) ON DELETE SET NULL,
    CONSTRAINT fk_details_fiofanana_fivondronana2 FOREIGN KEY (bitsikyfivondronana2)
        REFERENCES fivondronana(idfivondronana) ON DELETE SET NULL,
    CONSTRAINT uk_details_fiofanana_personne_type UNIQUE (idpersonne, idtypefiofanana)
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_details_fiofanana_personne ON details_fiofanana(idpersonne);
CREATE INDEX IF NOT EXISTS idx_details_fiofanana_type_fiofanana ON details_fiofanana(idtypefiofanana);
CREATE INDEX IF NOT EXISTS idx_details_fiofanana_fivondronana1 ON details_fiofanana(bitsikyfivondronana1);
CREATE INDEX IF NOT EXISTS idx_details_fiofanana_fivondronana2 ON details_fiofanana(bitsikyfivondronana2);

-- =====================================
-- Migration : Ajouter idTypeFiofanana à details_fiofanana
-- (Si la table existe déjà sans cette colonne)
-- =====================================

-- Supprimer la contrainte UNIQUE sur idpersonne si elle existe
ALTER TABLE IF EXISTS details_fiofanana DROP CONSTRAINT IF EXISTS details_fiofanana_idpersonne_key;

-- Ajouter la colonne idtypefiofanana si elle n'existe pas
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'details_fiofanana' 
        AND column_name = 'idtypefiofanana'
    ) THEN
        ALTER TABLE details_fiofanana ADD COLUMN idtypefiofanana INTEGER;
    END IF;
END $$;

-- Mettre à jour les enregistrements existants avec le typeFiofanana de la personne
-- (si la colonne idtypefiofanana existe dans la table personne)
UPDATE details_fiofanana df
SET idtypefiofanana = p.idtypefiofanana
FROM personne p
WHERE df.idpersonne = p.idpersonne
  AND p.idtypefiofanana IS NOT NULL
  AND df.idtypefiofanana IS NULL;

-- Ajouter la contrainte NOT NULL après avoir rempli les données (si la colonne existe)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'details_fiofanana' 
        AND column_name = 'idtypefiofanana'
        AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE details_fiofanana ALTER COLUMN idtypefiofanana SET NOT NULL;
    END IF;
END $$;

-- Ajouter la clé étrangère vers type_fiofanana
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_details_fiofanana_type_fiofanana'
    ) THEN
        ALTER TABLE details_fiofanana
        ADD CONSTRAINT fk_details_fiofanana_type_fiofanana 
        FOREIGN KEY (idtypefiofanana)
        REFERENCES type_fiofanana(idtypefiofanana) ON DELETE CASCADE;
    END IF;
END $$;

-- Ajouter la contrainte UNIQUE sur (idpersonne, idtypefiofanana)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'uk_details_fiofanana_personne_type'
    ) THEN
        ALTER TABLE details_fiofanana
        ADD CONSTRAINT uk_details_fiofanana_personne_type 
        UNIQUE (idpersonne, idtypefiofanana);
    END IF;
END $$;

-- Ajouter l'index si nécessaire
CREATE INDEX IF NOT EXISTS idx_details_fiofanana_type_fiofanana 
ON details_fiofanana(idtypefiofanana);

-- =====================================
-- MODIFICATION : details_fiofanana
-- Ajouter les colonnes pour Diniky ny filoha et Filasiana
-- (Sections D et E pour fanomababa et fanaterana)
-- =====================================

-- Section D: Diniky ny filoha (5 thèmes et 5 filoha)
ALTER TABLE IF EXISTS details_fiofanana 
ADD COLUMN IF NOT EXISTS dinikytheme1 VARCHAR(255),
ADD COLUMN IF NOT EXISTS dinikyfiloha1 VARCHAR(100),
ADD COLUMN IF NOT EXISTS dinikytheme2 VARCHAR(255),
ADD COLUMN IF NOT EXISTS dinikyfiloha2 VARCHAR(100),
ADD COLUMN IF NOT EXISTS dinikytheme3 VARCHAR(255),
ADD COLUMN IF NOT EXISTS dinikyfiloha3 VARCHAR(100),
ADD COLUMN IF NOT EXISTS dinikytheme4 VARCHAR(255),
ADD COLUMN IF NOT EXISTS dinikyfiloha4 VARCHAR(100),
ADD COLUMN IF NOT EXISTS dinikytheme5 VARCHAR(255),
ADD COLUMN IF NOT EXISTS dinikyfiloha5 VARCHAR(100);

-- Section E: Filasiana
ALTER TABLE IF EXISTS details_fiofanana 
ADD COLUMN IF NOT EXISTS filasianadaty DATE,
ADD COLUMN IF NOT EXISTS filasianafiloha VARCHAR(100);

-- Commentaires pour documentation
COMMENT ON COLUMN details_fiofanana.dinikytheme1 IS 'Thème 1 pour Diniky ny filoha';
COMMENT ON COLUMN details_fiofanana.dinikyfiloha1 IS 'Filoha 1 pour Diniky ny filoha';
COMMENT ON COLUMN details_fiofanana.dinikytheme2 IS 'Thème 2 pour Diniky ny filoha';
COMMENT ON COLUMN details_fiofanana.dinikyfiloha2 IS 'Filoha 2 pour Diniky ny filoha';
COMMENT ON COLUMN details_fiofanana.dinikytheme3 IS 'Thème 3 pour Diniky ny filoha';
COMMENT ON COLUMN details_fiofanana.dinikyfiloha3 IS 'Filoha 3 pour Diniky ny filoha';
COMMENT ON COLUMN details_fiofanana.dinikytheme4 IS 'Thème 4 pour Diniky ny filoha';
COMMENT ON COLUMN details_fiofanana.dinikyfiloha4 IS 'Filoha 4 pour Diniky ny filoha';
COMMENT ON COLUMN details_fiofanana.dinikytheme5 IS 'Thème 5 pour Diniky ny filoha';
COMMENT ON COLUMN details_fiofanana.dinikyfiloha5 IS 'Filoha 5 pour Diniky ny filoha';
COMMENT ON COLUMN details_fiofanana.filasianadaty IS 'Daty ny Filasiana';
COMMENT ON COLUMN details_fiofanana.filasianafiloha IS 'Filohan''ny lasy';

-- =====================================
-- MODIFICATION : details_fiofanana
-- Ajouter les colonnes pour Ravinala et TP2
-- (Sections F et G)
-- =====================================

-- Section F: Ravinala (ID 3)
ALTER TABLE IF EXISTS details_fiofanana 
ADD COLUMN IF NOT EXISTS lasyravinala VARCHAR(255),
ADD COLUMN IF NOT EXISTS soutenance VARCHAR(255);

-- Section G: TP2 (ID 4)
ALTER TABLE IF EXISTS details_fiofanana 
ADD COLUMN IF NOT EXISTS lasynanoloranatp2 VARCHAR(255);

-- Commentaires pour documentation
COMMENT ON COLUMN details_fiofanana.lasyravinala IS 'Lasy ravinala (pour type ravinala)';
COMMENT ON COLUMN details_fiofanana.soutenance IS 'Soutenance (pour type ravinala)';
COMMENT ON COLUMN details_fiofanana.lasynanoloranatp2 IS 'Lasy nanolorana TP2 (pour type TP2)';

-- =====================================
-- FIN DE LA MIGRATION
-- =====================================
-- Toutes les modifications ont été appliquées avec succès.
-- Les tables et colonnes sont prêtes pour les modules Fiofanana et Filoha.
-- =====================================
