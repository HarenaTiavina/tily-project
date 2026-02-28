-- =====================================
-- MODIFICATION : details_fiofanana
-- Ajouter les colonnes pour Ravinala et TP2
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
