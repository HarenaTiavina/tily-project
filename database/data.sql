-- =====================================
-- DONNÉES DE TEST POUR TILY
-- =====================================
-- Exécuter ce script APRÈS schema.sql sur la base de données 'tily'

-- =====================================
-- INSERTION DES TYPES DE PERSONNE
-- =====================================
INSERT INTO type_personne (nom) VALUES ('Responsable');
INSERT INTO type_personne (nom) VALUES ('Eleve');

-- =====================================
-- INSERTION DES SECTEURS - FARITRA
-- =====================================
INSERT INTO secteur (nom) VALUES ('Avo');
INSERT INTO secteur (nom) VALUES ('Ira');
INSERT INTO secteur (nom) VALUES ('Miandra');
INSERT INTO secteur (nom) VALUES ('Orimbato');
INSERT INTO secteur (nom) VALUES ('Sompitra');

-- =====================================
-- INSERTION DES SECTIONS (Fizarana en malgache - pour les Beazina)
-- =====================================
INSERT INTO section (nom) VALUES ('Mavo');
INSERT INTO section (nom) VALUES ('Maintso');
INSERT INTO section (nom) VALUES ('Mena');
INSERT INTO section (nom) VALUES ('Menafify');
INSERT INTO section (nom) VALUES ('Sompitra');

-- =====================================
-- INSERTION DES ANDRAIKITRA (Andraikitra en malgache - postes pour les Mpiandraikitra)
-- =====================================
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
-- INSERTION DES VONDRONA (Vondrona en malgache - groupes pour les Beazina et Mpiandraikitra)
-- =====================================
INSERT INTO vondrona (nom) VALUES ('Vondrona 1');
INSERT INTO vondrona (nom) VALUES ('Vondrona 2');
INSERT INTO vondrona (nom) VALUES ('Vondrona 3');
INSERT INTO vondrona (nom) VALUES ('Vondrona 4');
INSERT INTO vondrona (nom) VALUES ('Vondrona 5');

-- =====================================
-- INSERTION DES FIVONDRONANA (Districts - pour la gestion des accès)
-- =====================================
INSERT INTO fivondronana (nom, code) VALUES ('Antananarivo Renivohitra', 'ANT-R');
INSERT INTO fivondronana (nom, code) VALUES ('Antananarivo Atsimondrano', 'ANT-A');
INSERT INTO fivondronana (nom, code) VALUES ('Antananarivo Avaradrano', 'ANT-V');
INSERT INTO fivondronana (nom, code) VALUES ('Antsirabe I', 'ATS-1');
INSERT INTO fivondronana (nom, code) VALUES ('Antsirabe II', 'ATS-2');

-- =====================================
-- INSERTION DES FAFI (FAFI en malgache - remplace assurance)
-- =====================================
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-01-15', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-02-20', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-03-10', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-01-25', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-04-05', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-02-14', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-05-01', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-03-22', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES (NULL, NULL, 'Inactive');
INSERT INTO fafi (datepaiement, montant, statut) VALUES (NULL, NULL, 'Inactive');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-06-10', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-07-15', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES (NULL, NULL, 'Inactive');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-08-20', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-09-05', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES (NULL, NULL, 'Inactive');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-10-12', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-11-01', 8000.00, 'Active');
INSERT INTO fafi (datepaiement, montant, statut) VALUES (NULL, NULL, 'Inactive');
INSERT INTO fafi (datepaiement, montant, statut) VALUES ('2024-01-30', 8000.00, 'Active');

-- =====================================
-- INSERTION DES PERSONNES (RESPONSABLES - Mpiandraikitra)
-- Note: Pas de niveau pour les responsables, utilise idAndraikitra au lieu de idSection
-- Les personnes sont affectées à un Fivondronana
-- =====================================
INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RAKOTO', 'Jean', 'Akanga', '1990-03-15', NULL, '034 12 345 67', NULL, '101 251 456 789', 'Pierre Rakoto', 'Marie Rasoa', '2020-01-12', 1, 1, 1, NULL, 1, 1);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RABE', 'Marie', 'Vorona', '1988-07-22', NULL, '033 45 678 90', NULL, '101 352 789 123', 'Paul Rabe', 'Sophie Ravao', '2019-03-05', 1, 2, 1, NULL, 2, 1);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('ANDRIA', 'Paul', 'Saka', '1995-11-10', NULL, '032 98 765 43', NULL, '101 456 123 456', 'Luc Andria', 'Emma Rajo', '2021-06-20', 1, 9, 2, NULL, 3, 2);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RAZAF', 'Sophie', 'Tsingy', '1992-09-05', NULL, '034 56 789 12', NULL, '101 789 456 789', 'Marc Razaf', 'Noro Ranaivo', '2018-02-15', 1, 3, 3, NULL, 1, 2);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('HARI', 'Luc', 'Hazo', '1985-12-28', NULL, '033 12 345 67', NULL, '101 321 654 987', 'Hery Hari', 'Lala Ratsima', '2017-11-08', 1, 4, 4, NULL, 2, 3);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RANDRIA', 'Haja', 'Fosa', '1993-04-18', NULL, '034 87 654 32', NULL, '101 654 321 987', 'Solo Randria', 'Vola Rasoana', '2022-04-10', 1, 5, 1, NULL, 4, 3);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RAKOTON', 'Feno', 'Lambo', '1991-08-07', NULL, '033 23 456 78', NULL, '101 987 654 321', 'Tiana Rakoton', 'Soa Rabary', '2019-09-25', 1, 6, 2, NULL, 5, 4);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RASOLO', 'Aina', 'Fody', '1987-02-14', NULL, '032 34 567 89', NULL, '101 147 258 369', 'Nirina Rasolo', 'Mialy Rabe', '2016-12-03', 1, 7, 5, NULL, 6, 4);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RAMANAM', 'Tojo', 'Akanga', '1994-06-30', NULL, '034 45 678 90', NULL, '101 258 369 147', 'Bema Ramanam', 'Fara Rason', '2023-01-18', 1, 10, 1, NULL, 7, 5);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RAJAO', 'Vonjy', 'Sifaka', '1989-10-25', NULL, '033 56 789 01', NULL, '101 369 147 258', 'Mamy Rajao', 'Tahina Ravelo', '2018-07-22', 1, 8, 3, NULL, 8, 5);

-- =====================================
-- INSERTION DES PERSONNES (ÉLÈVES - Beazina)
-- Note: Les Beazina utilisent idSection et ont un niveau (ambaratonga) - champ texte libre
-- Les personnes sont affectées à un Fivondronana
-- =====================================
INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RAKOTO', 'Aina', 'Akanga', '2010-03-15', 'Louveteau', NULL, NULL, NULL, 'Jean Rakoto', 'Marie Rasoa', '2023-01-12', 2, 11, 1, 1, NULL, 1);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RABE', 'Feno', 'Vorona', '2008-07-22', 'Éclaireur', NULL, NULL, NULL, 'Paul Rabe', 'Sophie Ravao', '2022-03-05', 2, 12, 1, 2, NULL, 1);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('ANDRIA', 'Tiana', 'Saka', '2012-11-10', 'Louveteau', NULL, NULL, NULL, 'Luc Andria', 'Emma Rajo', '2023-06-20', 2, 13, 2, 5, NULL, 2);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RAZAF', 'Koto', 'Tsingy', '2006-09-05', 'Routier', NULL, NULL, NULL, 'Marc Razaf', 'Noro Ranaivo', '2020-02-15', 2, 14, 3, 3, NULL, 2);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('HARI', 'Soa', 'Hazo', '2009-12-28', 'Éclaireur', NULL, NULL, NULL, 'Hery Hari', 'Lala Ratsima', '2021-11-08', 2, 15, 4, 4, NULL, 3);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RAJAO', 'Mamy', 'Daka', '2011-04-14', 'Louveteau', NULL, NULL, NULL, 'Solo Rajao', 'Vola Rasoana', '2023-09-22', 2, 16, 1, 3, NULL, 3);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RASOA', 'Ny Avo', 'Fosa', '2007-08-20', 'Routier', NULL, NULL, NULL, 'Bema Rasoa', 'Fara Rabe', '2019-05-10', 2, 17, 2, 2, NULL, 4);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RANDRIA', 'Tsiry', 'Lambo', '2013-01-30', 'Louveteau', NULL, NULL, NULL, 'Tiana Randria', 'Soa Ravelo', '2024-02-28', 2, 18, 5, 5, NULL, 4);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RATSIM', 'Mahery', 'Fody', '2010-06-15', 'Louveteau', NULL, NULL, NULL, 'Nirina Ratsim', 'Mialy Rason', '2023-04-05', 2, 19, 1, 1, NULL, 5);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RANAIVO', 'Hery', 'Sifaka', '2008-10-12', 'Éclaireur', NULL, NULL, NULL, 'Mamy Ranaivo', 'Tahina Rabe', '2021-08-18', 2, 20, 3, 4, NULL, 5);

-- Élèves supplémentaires sans FAFI
INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RABEHA', 'Lalao', 'Vorona', '2011-05-22', 'Louveteau', NULL, NULL, NULL, 'Herilala Rabeha', 'Vola Ramana', '2023-10-15', 2, NULL, 1, 1, NULL, 1);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RAHOVA', 'Mirana', 'Akanga', '2009-09-08', 'Éclaireur', NULL, NULL, NULL, 'Fidy Rahova', 'Soa Ralaivo', '2022-06-30', 2, NULL, 4, 2, NULL, 2);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RAFENO', 'Toky', 'Tsingy', '2012-02-18', 'Louveteau', NULL, NULL, NULL, 'Bema Rafeno', 'Fara Rasolo', '2024-01-08', 2, NULL, 2, 1, NULL, 3);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RALAY', 'Sitrakiniaina', 'Hazo', '2007-07-25', 'Routier', NULL, NULL, NULL, 'Solo Ralay', 'Mialy Rabe', '2020-04-12', 2, NULL, 3, 3, NULL, 4);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idfafi, idsecteur, idsection, idandraikitra, idfivondronana)
VALUES ('RAMANA', 'Tojo', 'Fosa', '2010-12-03', 'Louveteau', NULL, NULL, NULL, 'Hery Ramana', 'Lala Rasoana', '2023-07-20', 2, NULL, 5, 5, NULL, 5);

-- =====================================
-- INSERTION DES UTILISATEURS
-- =====================================
-- Mot de passe pour tous: "123456" (hashé avec BCrypt)
-- Hash BCrypt pour "123456": $2a$10$z89dW1lU6u6Dc6m280iVxuuQ7S/.xHcWSzJcqYa8JONDrOEHXMm0.

-- Admin (pas de Fivondronana - peut tout voir)
INSERT INTO utilisateur (email, motdepasse, role, actif, datecreation, idfivondronana)
VALUES ('admin@tily.mg', '$2a$10$z89dW1lU6u6Dc6m280iVxuuQ7S/.xHcWSzJcqYa8JONDrOEHXMm0.', 'ADMIN', TRUE, NOW(), NULL);

-- Utilisateurs par Fivondronana
INSERT INTO utilisateur (email, motdepasse, role, actif, datecreation, idfivondronana)
VALUES ('antananarivo.renivohitra@tily.mg', '$2a$10$z89dW1lU6u6Dc6m280iVxuuQ7S/.xHcWSzJcqYa8JONDrOEHXMm0.', 'USER', TRUE, NOW(), 1);

INSERT INTO utilisateur (email, motdepasse, role, actif, datecreation, idfivondronana)
VALUES ('antananarivo.atsimondrano@tily.mg', '$2a$10$z89dW1lU6u6Dc6m280iVxuuQ7S/.xHcWSzJcqYa8JONDrOEHXMm0.', 'USER', TRUE, NOW(), 2);

INSERT INTO utilisateur (email, motdepasse, role, actif, datecreation, idfivondronana)
VALUES ('antananarivo.avaradrano@tily.mg', '$2a$10$z89dW1lU6u6Dc6m280iVxuuQ7S/.xHcWSzJcqYa8JONDrOEHXMm0.', 'USER', TRUE, NOW(), 3);

INSERT INTO utilisateur (email, motdepasse, role, actif, datecreation, idfivondronana)
VALUES ('antsirabe1@tily.mg', '$2a$10$z89dW1lU6u6Dc6m280iVxuuQ7S/.xHcWSzJcqYa8JONDrOEHXMm0.', 'USER', TRUE, NOW(), 4);

INSERT INTO utilisateur (email, motdepasse, role, actif, datecreation, idfivondronana)
VALUES ('antsirabe2@tily.mg', '$2a$10$z89dW1lU6u6Dc6m280iVxuuQ7S/.xHcWSzJcqYa8JONDrOEHXMm0.', 'USER', TRUE, NOW(), 5);

-- =====================================
-- VÉRIFICATION DES DONNÉES
-- =====================================
-- Nombre de responsables: 10
-- Nombre d'élèves: 15
-- Total personnes: 25
-- Responsables avec FAFI active: 8
-- Élèves avec FAFI active: 10
-- 
-- Comptes de test:
-- Email: admin@tily.mg / Mot de passe: 123456 (Admin - voit tout)
-- Email: antananarivo.renivohitra@tily.mg / Mot de passe: 123456 (Fivondronana 1)
-- Email: antananarivo.atsimondrano@tily.mg / Mot de passe: 123456 (Fivondronana 2)
-- Email: antananarivo.avaradrano@tily.mg / Mot de passe: 123456 (Fivondronana 3)
-- Email: antsirabe1@tily.mg / Mot de passe: 123456 (Fivondronana 4)
-- Email: antsirabe2@tily.mg / Mot de passe: 123456 (Fivondronana 5)
