-- =====================================
-- SCRIPT DE MIGRATION - AJOUT DE VONDRONA
-- =====================================
-- Exécuter ce script sur la base de données existante pour ajouter la table vondrona
-- et la colonne idVondrona à la table personne

-- =====================================
-- CRÉATION DE LA TABLE VONDRONA
-- =====================================
CREATE TABLE IF NOT EXISTS vondrona (
    idVondrona SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

-- =====================================
-- AJOUT DE LA COLONNE idVondrona À LA TABLE PERSONNE
-- =====================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'personne' AND column_name = 'idvondrona'
    ) THEN
        ALTER TABLE personne ADD COLUMN idVondrona INTEGER;
        ALTER TABLE personne ADD CONSTRAINT fk_vondrona 
            FOREIGN KEY (idVondrona) REFERENCES vondrona(idVondrona);
    END IF;
END $$;

-- =====================================
-- CRÉATION DE L'INDEX
-- =====================================
CREATE INDEX IF NOT EXISTS idx_personne_vondrona ON personne(idVondrona);

-- =====================================
-- INSERTION DES DONNÉES DE VONDRONA
-- =====================================
INSERT INTO vondrona (nom) VALUES ('Vondrona 1') ON CONFLICT DO NOTHING;
INSERT INTO vondrona (nom) VALUES ('Vondrona 2') ON CONFLICT DO NOTHING;
INSERT INTO vondrona (nom) VALUES ('Vondrona 3') ON CONFLICT DO NOTHING;
INSERT INTO vondrona (nom) VALUES ('Vondrona 4') ON CONFLICT DO NOTHING;
INSERT INTO vondrona (nom) VALUES ('Vondrona 5') ON CONFLICT DO NOTHING;

-- =====================================
-- VÉRIFICATION
-- =====================================
-- SELECT * FROM vondrona;
-- SELECT column_name FROM information_schema.columns WHERE table_name = 'personne';

