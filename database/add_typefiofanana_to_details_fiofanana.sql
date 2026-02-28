-- =====================================
-- Migration : Ajouter idTypeFiofanana à details_fiofanana
-- Pour permettre de stocker des données différentes par type de fiofanana
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

-- Ajouter la contrainte NOT NULL après avoir rempli les données
ALTER TABLE details_fiofanana ALTER COLUMN idtypefiofanana SET NOT NULL;

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
