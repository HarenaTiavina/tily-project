-- =====================================
-- INSERTION DU TYPE PERSONNE "Filoha"
-- =====================================
-- Ce script ajoute le type "Filoha" dans la table type_personne
-- si il n'existe pas déjà

INSERT INTO type_personne (nom)
SELECT 'Filoha'
WHERE NOT EXISTS (
    SELECT 1 FROM type_personne WHERE nom = 'Filoha'
);
