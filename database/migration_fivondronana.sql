-- =====================================
-- MIGRATION: Ajout du système Fivondronana
-- =====================================
-- Ce script migre une base existante vers le nouveau système
-- basé sur les Fivondronana (districts) pour la gestion des accès

-- 1. Créer la table Fivondronana
CREATE TABLE IF NOT EXISTS fivondronana (
    idFivondronana SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    code VARCHAR(20)
);

-- 2. Ajouter la colonne idFivondronana à la table personne
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'personne' AND column_name = 'idfivondronana') THEN
        ALTER TABLE personne ADD COLUMN idFivondronana INTEGER;
    END IF;
END $$;

-- 3. Ajouter la contrainte de clé étrangère pour personne.idFivondronana
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_fivondronana' AND conrelid = 'personne'::regclass) THEN
        ALTER TABLE personne
        ADD CONSTRAINT fk_fivondronana FOREIGN KEY (idFivondronana)
        REFERENCES fivondronana(idFivondronana);
    END IF;
END $$;

-- 4. Créer l'index pour personne.idFivondronana
CREATE INDEX IF NOT EXISTS idx_personne_fivondronana ON personne(idFivondronana);

-- 5. Modifier la table utilisateur pour utiliser idFivondronana au lieu de idPersonne
-- D'abord, supprimer l'ancienne contrainte et colonne
DO $$
BEGIN
    -- Supprimer la contrainte existante si elle existe
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_utilisateur_personne') THEN
        ALTER TABLE utilisateur DROP CONSTRAINT fk_utilisateur_personne;
    END IF;
    
    -- Supprimer la colonne idPersonne si elle existe
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'utilisateur' AND column_name = 'idpersonne') THEN
        ALTER TABLE utilisateur DROP COLUMN idPersonne;
    END IF;
    
    -- Ajouter la colonne idFivondronana si elle n'existe pas
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'utilisateur' AND column_name = 'idfivondronana') THEN
        ALTER TABLE utilisateur ADD COLUMN idFivondronana INTEGER;
    END IF;
END $$;

-- 6. Ajouter la contrainte de clé étrangère pour utilisateur.idFivondronana
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_utilisateur_fivondronana') THEN
        ALTER TABLE utilisateur
        ADD CONSTRAINT fk_utilisateur_fivondronana FOREIGN KEY (idFivondronana)
        REFERENCES fivondronana(idFivondronana) ON DELETE SET NULL;
    END IF;
END $$;

-- 7. Créer l'index pour utilisateur.idFivondronana
CREATE INDEX IF NOT EXISTS idx_utilisateur_fivondronana ON utilisateur(idFivondronana);

-- 8. Supprimer l'ancien index sur idPersonne si existant
DROP INDEX IF EXISTS idx_utilisateur_personne;

-- 9. Insérer des Fivondronana par défaut si la table est vide
INSERT INTO fivondronana (nom, code)
SELECT 'Antananarivo Renivohitra', 'ANT-R' WHERE NOT EXISTS (SELECT 1 FROM fivondronana WHERE code = 'ANT-R');
INSERT INTO fivondronana (nom, code)
SELECT 'Antananarivo Atsimondrano', 'ANT-A' WHERE NOT EXISTS (SELECT 1 FROM fivondronana WHERE code = 'ANT-A');
INSERT INTO fivondronana (nom, code)
SELECT 'Antananarivo Avaradrano', 'ANT-V' WHERE NOT EXISTS (SELECT 1 FROM fivondronana WHERE code = 'ANT-V');
INSERT INTO fivondronana (nom, code)
SELECT 'Antsirabe I', 'ATS-1' WHERE NOT EXISTS (SELECT 1 FROM fivondronana WHERE code = 'ATS-1');
INSERT INTO fivondronana (nom, code)
SELECT 'Antsirabe II', 'ATS-2' WHERE NOT EXISTS (SELECT 1 FROM fivondronana WHERE code = 'ATS-2');

-- 10. (Optionnel) Affecter les personnes existantes au premier Fivondronana
-- Décommentez cette ligne si vous voulez affecter toutes les personnes existantes à un Fivondronana par défaut
-- UPDATE personne SET idFivondronana = 1 WHERE idFivondronana IS NULL;

-- =====================================
-- NOTES IMPORTANTES
-- =====================================
-- 1. Les utilisateurs avec role='ADMIN' n'ont pas besoin de Fivondronana (idFivondronana = NULL)
-- 2. Les utilisateurs avec role='USER' doivent avoir un Fivondronana assigné
-- 3. Les personnes créées par un utilisateur USER sont automatiquement affectées à son Fivondronana
-- 4. L'admin peut voir et modifier toutes les personnes de tous les Fivondronana
-- 5. Un utilisateur USER ne peut voir que les personnes de son Fivondronana

-- Exemple pour créer un utilisateur pour un Fivondronana:
-- INSERT INTO utilisateur (email, motdepasse, role, actif, datecreation, idfivondronana)
-- VALUES ('email@example.com', '<bcrypt_hash>', 'USER', TRUE, NOW(), 1);

