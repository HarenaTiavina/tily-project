-- =====================================
-- SCRIPT SQL DE PRODUCTION - TILY
-- =====================================
-- Ce script contient toutes les données de référence nécessaires pour la production
-- Exécuter ce script APRÈS avoir exécuté schema.sql et spring_session.sql
--
-- IMPORTANT : 
-- - Changer le mot de passe admin après le premier login
-- - Le mot de passe par défaut est : Admin@Tily2024!
-- - Pour générer un nouveau hash BCrypt, utilisez BCryptGenerator.java

-- =====================================
-- INSERTION DES TYPES DE PERSONNE
-- =====================================
-- Types de base nécessaires pour le fonctionnement de l'application
INSERT INTO type_personne (nom) VALUES ('Responsable');
INSERT INTO type_personne (nom) VALUES ('Eleve');

-- =====================================
-- INSERTION DES SECTEURS (FARITRA)
-- =====================================
-- Secteurs géographiques pour organiser les personnes
INSERT INTO secteur (nom) VALUES ('Avo');
INSERT INTO secteur (nom) VALUES ('Ira');
INSERT INTO secteur (nom) VALUES ('Miandra');
INSERT INTO secteur (nom) VALUES ('Orimbato');
INSERT INTO secteur (nom) VALUES ('Sompitra');

-- =====================================
-- INSERTION DES SECTIONS (FIZARANA)
-- =====================================
-- Sections pour les Beazina (Élèves)
INSERT INTO section (nom) VALUES ('Mavo');
INSERT INTO section (nom) VALUES ('Maintso');
INSERT INTO section (nom) VALUES ('Mena');
INSERT INTO section (nom) VALUES ('Menafify');
INSERT INTO section (nom) VALUES ('Sompitra');

-- =====================================
-- INSERTION DES ANDRAIKITRA
-- =====================================
-- Postes/responsabilités pour les Mpiandraikitra (Responsables)
INSERT INTO andraikitra (nom) VALUES ('Mpitandrina lehibe');
INSERT INTO andraikitra (nom) VALUES ('Mpitandrina faharoa');
INSERT INTO andraikitra (nom) VALUES ('Mpitandrina fizarana');
INSERT INTO andraikitra (nom) VALUES ('Mpitandrina ambaratonga');
INSERT INTO andraikitra (nom) VALUES ('Mpitandrina sekoly');
INSERT INTO andraikitra (nom) VALUES ('Mpitandrina fikambanana');
INSERT INTO andraikitra (nom) VALUES ('Mpitandrina mpanampy');
INSERT INTO andraikitra (nom) VALUES ('Mpitandrina mpanara-maso');
INSERT INTO andraikitra (nom) VALUES ('Mpitandrina mpanampy faharoa');
INSERT INTO andraikitra (nom) VALUES ('Mpitandrina mpanampy fahatelo');

-- =====================================
-- CRÉATION D'UN COMPTE ADMINISTRATEUR
-- =====================================
-- Compte admin pour la gestion de l'application
-- IMPORTANT : Changer ce mot de passe après le premier login !
-- 
-- Email : admin@tily.mg
-- Mot de passe : Admin@Tily2024!
--
-- Pour générer un nouveau hash BCrypt :
-- 1. Exécutez: mvn compile exec:java -Dexec.mainClass="tily.mg.util.BCryptGenerator"
-- 2. Ou utilisez un outil en ligne: https://bcrypt-generator.com/
-- 3. Remplacez le hash ci-dessous par votre hash généré
--
-- Hash BCrypt pour "Admin@Tily2024!" (généré avec BCryptPasswordEncoder)
-- IMPORTANT: Pour générer un nouveau hash, exécutez:
-- mvn compile exec:java -Dexec.mainClass="tily.mg.util.BCryptGenerator"
-- Puis remplacez le hash ci-dessous par celui généré
--
-- Hash BCrypt valide pour "Admin@Tily2024!":
-- ATTENTION: Le hash ci-dessous doit être généré avec BCryptGenerator.java
-- Le hash actuel est temporaire - GÉNÉREZ-EN UN NOUVEAU pour la production !
-- 
-- Pour générer un nouveau hash:
-- 1. Exécutez: mvn compile exec:java -Dexec.mainClass="tily.mg.util.BCryptGenerator"
-- 2. Copiez le hash généré et remplacez-le ci-dessous
-- 3. Ou utilisez: https://bcrypt-generator.com/ avec 10 rounds
--
-- Hash temporaire (à remplacer):
INSERT INTO utilisateur (email, motdepasse, role, actif, datecreation, idpersonne) 
VALUES (
    'admin@tily.mg',
    '$2a$10$z89dW1lU6u6Dc6m280iVxuuQ7S/.xHcWSzJcqYa8JONDrOEHXMm0.',
    'ADMIN',
    TRUE,
    NOW(),
    NULL
);

-- =====================================
-- VÉRIFICATIONS
-- =====================================
-- Vérifier que les données sont bien insérées
SELECT 'Types de personne' as table_name, COUNT(*) as count FROM type_personne
UNION ALL
SELECT 'Secteurs (Faritra)', COUNT(*) FROM secteur
UNION ALL
SELECT 'Sections (Fizarana)', COUNT(*) FROM section
UNION ALL
SELECT 'Andraikitra', COUNT(*) FROM andraikitra
UNION ALL
SELECT 'Utilisateurs admin', COUNT(*) FROM utilisateur WHERE role = 'ADMIN';

-- =====================================
-- NOTES IMPORTANTES
-- =====================================
-- 1. Le compte admin par défaut :
--    - Email : admin@tily.mg
--    - Mot de passe : Admin@Tily2024!
--    - CHANGEZ CE MOT DE PASSE APRÈS LE PREMIER LOGIN !
--
-- 2. Les données de référence (secteurs, sections, andraikitra) peuvent être 
--    modifiées via l'interface d'administration si nécessaire.
--
-- 3. Pour ajouter d'autres utilisateurs, utilisez l'interface d'inscription
--    ou insérez-les manuellement dans la table utilisateur avec un hash BCrypt.
--
-- 4. Les tables FAFI seront créées automatiquement lors de l'ajout de personnes
--    avec des informations FAFI.
--
-- 5. Ordre d'exécution des scripts SQL :
--    a) schema.sql (création des tables)
--    b) spring_session.sql (tables de session)
--    c) production.sql (données de référence + admin)
