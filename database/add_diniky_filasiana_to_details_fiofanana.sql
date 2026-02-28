-- =====================================
-- MODIFICATION : details_fiofanana
-- Ajouter les colonnes pour Diniky ny filoha et Filasiana
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
