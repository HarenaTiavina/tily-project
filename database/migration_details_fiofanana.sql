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
