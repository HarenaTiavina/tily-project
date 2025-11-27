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
-- TABLE : section
-- =====================================
CREATE TABLE IF NOT EXISTS section (
    idSection SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

-- =====================================
-- TABLE : assurance
-- =====================================
CREATE TABLE IF NOT EXISTS assurance (
    idAssurance SERIAL PRIMARY KEY,
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
    idAssurance INTEGER,
    idSecteur INTEGER,
    idSection INTEGER,
    CONSTRAINT fk_type_personne FOREIGN KEY (idTypePersonne)
        REFERENCES type_personne(idTypePersonne),
    CONSTRAINT fk_assurance FOREIGN KEY (idAssurance)
        REFERENCES assurance(idAssurance),
    CONSTRAINT fk_secteur FOREIGN KEY (idSecteur)
        REFERENCES secteur(idSecteur),
    CONSTRAINT fk_section FOREIGN KEY (idSection)
        REFERENCES section(idSection)
);

-- =====================================
-- TABLE : utilisateur
-- =====================================
CREATE TABLE IF NOT EXISTS utilisateur (
    idUtilisateur SERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    motDePasse VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    actif BOOLEAN DEFAULT TRUE,
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    derniereConnexion TIMESTAMP,
    idPersonne INTEGER,
    CONSTRAINT fk_utilisateur_personne FOREIGN KEY (idPersonne)
        REFERENCES personne(idPersonne) ON DELETE SET NULL
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_personne_type ON personne(idTypePersonne);
CREATE INDEX IF NOT EXISTS idx_personne_secteur ON personne(idSecteur);
CREATE INDEX IF NOT EXISTS idx_personne_section ON personne(idSection);
CREATE INDEX IF NOT EXISTS idx_personne_assurance ON personne(idAssurance);
CREATE INDEX IF NOT EXISTS idx_utilisateur_email ON utilisateur(email);
CREATE INDEX IF NOT EXISTS idx_utilisateur_personne ON utilisateur(idPersonne);

