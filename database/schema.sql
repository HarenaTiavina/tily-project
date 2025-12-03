-- =====================================
-- SCRIPT DE CRÉATION DE LA BASE DE DONNÉES TILY
-- =====================================
-- Exécuter ce script en tant qu'utilisateur postgres

-- Création de la base de données (si elle n'existe pas)
-- CREATE DATABASE tily;

-- Se connecter à la base tily avant d'exécuter le reste

-- =====================================
-- TABLE : type_personne
-- =====================================
CREATE TABLE IF NOT EXISTS type_personne (
    idTypePersonne SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

-- =====================================
-- TABLE : secteur
-- =====================================
CREATE TABLE IF NOT EXISTS secteur (
    idSecteur SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

-- =====================================
-- TABLE : section (Fizarana en malgache - pour les Beazina)
-- =====================================
CREATE TABLE IF NOT EXISTS section (
    idSection SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

-- =====================================
-- TABLE : andraikitra (Andraikitra en malgache - postes pour les Mpiandraikitra)
-- =====================================
CREATE TABLE IF NOT EXISTS andraikitra (
    idAndraikitra SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

-- =====================================
-- TABLE : vondrona (Vondrona en malgache - groupe pour les Beazina et Mpiandraikitra)
-- =====================================
CREATE TABLE IF NOT EXISTS vondrona (
    idVondrona SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

-- =====================================
-- TABLE : fivondronana (District - pour la gestion des accès)
-- =====================================
CREATE TABLE IF NOT EXISTS fivondronana (
    idFivondronana SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    code VARCHAR(20)
);

-- =====================================
-- TABLE : fafi (FAFI en malgache - remplace assurance)
-- =====================================
CREATE TABLE IF NOT EXISTS fafi (
    idFafi SERIAL PRIMARY KEY,
    datePaiement DATE,
    montant NUMERIC(10,2),
    statut VARCHAR(20)
);

-- =====================================
-- TABLE : personne
-- =====================================
CREATE TABLE IF NOT EXISTS personne (
    idPersonne SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    totem VARCHAR(50),
    dateNaissance DATE,
    niveau VARCHAR(50),
    numeroTelephone VARCHAR(20),
    image TEXT,
    numeroCin VARCHAR(30),
    nomPere VARCHAR(100),
    nomMere VARCHAR(100),
    dateFanekena DATE,
    idTypePersonne INTEGER,
    idFafi INTEGER,
    idSecteur INTEGER,
    idSection INTEGER,
    idAndraikitra INTEGER,
    idVondrona INTEGER,
    idFivondronana INTEGER,
    CONSTRAINT fk_type_personne FOREIGN KEY (idTypePersonne)
        REFERENCES type_personne(idTypePersonne),
    CONSTRAINT fk_fafi FOREIGN KEY (idFafi)
        REFERENCES fafi(idFafi),
    CONSTRAINT fk_secteur FOREIGN KEY (idSecteur)
        REFERENCES secteur(idSecteur),
    CONSTRAINT fk_section FOREIGN KEY (idSection)
        REFERENCES section(idSection),
    CONSTRAINT fk_andraikitra FOREIGN KEY (idAndraikitra)
        REFERENCES andraikitra(idAndraikitra),
    CONSTRAINT fk_vondrona FOREIGN KEY (idVondrona)
        REFERENCES vondrona(idVondrona),
    CONSTRAINT fk_fivondronana FOREIGN KEY (idFivondronana)
        REFERENCES fivondronana(idFivondronana)
);

-- =====================================
-- TABLE : utilisateur
-- Lié à un Fivondronana (sauf admin qui a fivondronana = NULL)
-- =====================================
CREATE TABLE IF NOT EXISTS utilisateur (
    idUtilisateur SERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    motDePasse VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    actif BOOLEAN DEFAULT TRUE,
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    derniereConnexion TIMESTAMP,
    idFivondronana INTEGER,
    CONSTRAINT fk_utilisateur_fivondronana FOREIGN KEY (idFivondronana)
        REFERENCES fivondronana(idFivondronana) ON DELETE SET NULL
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_personne_type ON personne(idTypePersonne);
CREATE INDEX IF NOT EXISTS idx_personne_secteur ON personne(idSecteur);
CREATE INDEX IF NOT EXISTS idx_personne_section ON personne(idSection);
CREATE INDEX IF NOT EXISTS idx_personne_andraikitra ON personne(idAndraikitra);
CREATE INDEX IF NOT EXISTS idx_personne_fafi ON personne(idFafi);
CREATE INDEX IF NOT EXISTS idx_personne_vondrona ON personne(idVondrona);
CREATE INDEX IF NOT EXISTS idx_personne_fivondronana ON personne(idFivondronana);
CREATE INDEX IF NOT EXISTS idx_utilisateur_email ON utilisateur(email);
CREATE INDEX IF NOT EXISTS idx_utilisateur_fivondronana ON utilisateur(idFivondronana);

-- =====================================
-- TABLES SPRING SESSION
-- =====================================
CREATE TABLE IF NOT EXISTS spring_session (
    primary_id CHAR(36) NOT NULL,
    session_id CHAR(36) NOT NULL,
    creation_time BIGINT NOT NULL,
    last_access_time BIGINT NOT NULL,
    max_inactive_interval INTEGER NOT NULL,
    expiry_time BIGINT NOT NULL,
    principal_name VARCHAR(100),
    CONSTRAINT spring_session_pk PRIMARY KEY (primary_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS spring_session_ix1 ON spring_session (session_id);
CREATE INDEX IF NOT EXISTS spring_session_ix2 ON spring_session (expiry_time);
CREATE INDEX IF NOT EXISTS spring_session_ix3 ON spring_session (principal_name);

CREATE TABLE IF NOT EXISTS spring_session_attributes (
    session_primary_id CHAR(36) NOT NULL,
    attribute_name VARCHAR(200) NOT NULL,
    attribute_bytes BYTEA NOT NULL,
    CONSTRAINT spring_session_attributes_pk PRIMARY KEY (session_primary_id, attribute_name),
    CONSTRAINT spring_session_attributes_fk FOREIGN KEY (session_primary_id) REFERENCES spring_session(primary_id) ON DELETE CASCADE
);
