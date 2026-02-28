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
