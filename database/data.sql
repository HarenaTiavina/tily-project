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
-- INSERTION DES SECTEURS
-- =====================================
INSERT INTO secteur (nom) VALUES ('Antananarivo');
INSERT INTO secteur (nom) VALUES ('Fianarantsoa');
INSERT INTO secteur (nom) VALUES ('Toamasina');
INSERT INTO secteur (nom) VALUES ('Mahajanga');
INSERT INTO secteur (nom) VALUES ('Antsiranana');
INSERT INTO secteur (nom) VALUES ('Toliara');

-- =====================================
-- INSERTION DES SECTIONS
-- =====================================
INSERT INTO section (nom) VALUES ('Analamanga');
INSERT INTO section (nom) VALUES ('Itasy');
INSERT INTO section (nom) VALUES ('Vakinankaratra');
INSERT INTO section (nom) VALUES ('Bongolava');
INSERT INTO section (nom) VALUES ('Haute Matsiatra');
INSERT INTO section (nom) VALUES ('Amoron''i Mania');
INSERT INTO section (nom) VALUES ('Atsinanana');
INSERT INTO section (nom) VALUES ('Analanjirofo');
INSERT INTO section (nom) VALUES ('Boeny');
INSERT INTO section (nom) VALUES ('Betsiboka');
INSERT INTO section (nom) VALUES ('DIANA');
INSERT INTO section (nom) VALUES ('SAVA');
INSERT INTO section (nom) VALUES ('Atsimo-Andrefana');
INSERT INTO section (nom) VALUES ('Menabe');

-- =====================================
-- INSERTION DES ASSURANCES
-- =====================================
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-01-15', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-02-20', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-03-10', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-01-25', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-04-05', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-02-14', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-05-01', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-03-22', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES (NULL, NULL, 'Inactive');
INSERT INTO assurance (datepaiement, montant, statut) VALUES (NULL, NULL, 'Inactive');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-06-10', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-07-15', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES (NULL, NULL, 'Inactive');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-08-20', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-09-05', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES (NULL, NULL, 'Inactive');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-10-12', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-11-01', 8000.00, 'Active');
INSERT INTO assurance (datepaiement, montant, statut) VALUES (NULL, NULL, 'Inactive');
INSERT INTO assurance (datepaiement, montant, statut) VALUES ('2024-01-30', 8000.00, 'Active');

-- =====================================
-- INSERTION DES PERSONNES (RESPONSABLES)
-- =====================================
INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RAKOTO', 'Jean', 'Akanga', '1990-03-15', 'Routier', '034 12 345 67', NULL, '101 251 456 789', 'Pierre Rakoto', 'Marie Rasoa', '2020-01-12', 1, 1, 1, 1);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RABE', 'Marie', 'Vorona', '1988-07-22', 'Routier', '033 45 678 90', NULL, '101 352 789 123', 'Paul Rabe', 'Sophie Ravao', '2019-03-05', 1, 2, 1, 2);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('ANDRIA', 'Paul', 'Saka', '1995-11-10', 'Éclaireur', '032 98 765 43', NULL, '101 456 123 456', 'Luc Andria', 'Emma Rajo', '2021-06-20', 1, 9, 2, 5);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RAZAF', 'Sophie', 'Tsingy', '1992-09-05', 'Routier', '034 56 789 12', NULL, '101 789 456 789', 'Marc Razaf', 'Noro Ranaivo', '2018-02-15', 1, 3, 3, 7);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('HARI', 'Luc', 'Hazo', '1985-12-28', 'Routier', '033 12 345 67', NULL, '101 321 654 987', 'Hery Hari', 'Lala Ratsima', '2017-11-08', 1, 4, 4, 9);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RANDRIA', 'Haja', 'Fosa', '1993-04-18', 'Éclaireur', '034 87 654 32', NULL, '101 654 321 987', 'Solo Randria', 'Vola Rasoana', '2022-04-10', 1, 5, 1, 3);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RAKOTON', 'Feno', 'Lambo', '1991-08-07', 'Routier', '033 23 456 78', NULL, '101 987 654 321', 'Tiana Rakoton', 'Soa Rabary', '2019-09-25', 1, 6, 2, 6);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RASOLO', 'Aina', 'Fody', '1987-02-14', 'Routier', '032 34 567 89', NULL, '101 147 258 369', 'Nirina Rasolo', 'Mialy Rabe', '2016-12-03', 1, 7, 5, 11);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RAMANAM', 'Tojo', 'Akanga', '1994-06-30', 'Éclaireur', '034 45 678 90', NULL, '101 258 369 147', 'Bema Ramanam', 'Fara Rason', '2023-01-18', 1, 10, 6, 13);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RAJAO', 'Vonjy', 'Sifaka', '1989-10-25', 'Routier', '033 56 789 01', NULL, '101 369 147 258', 'Mamy Rajao', 'Tahina Ravelo', '2018-07-22', 1, 8, 3, 8);

-- =====================================
-- INSERTION DES PERSONNES (ÉLÈVES)
-- =====================================
INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RAKOTO', 'Aina', 'Akanga', '2010-03-15', 'Louveteau', NULL, NULL, NULL, 'Jean Rakoto', 'Marie Rasoa', '2023-01-12', 2, 11, 1, 1);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RABE', 'Feno', 'Vorona', '2008-07-22', 'Éclaireur', NULL, NULL, NULL, 'Paul Rabe', 'Sophie Ravao', '2022-03-05', 2, 12, 1, 2);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('ANDRIA', 'Tiana', 'Saka', '2012-11-10', 'Louveteau', NULL, NULL, NULL, 'Luc Andria', 'Emma Rajo', '2023-06-20', 2, 13, 2, 5);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RAZAF', 'Koto', 'Tsingy', '2006-09-05', 'Routier', NULL, NULL, NULL, 'Marc Razaf', 'Noro Ranaivo', '2020-02-15', 2, 14, 3, 7);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('HARI', 'Soa', 'Hazo', '2009-12-28', 'Éclaireur', NULL, NULL, NULL, 'Hery Hari', 'Lala Ratsima', '2021-11-08', 2, 15, 4, 9);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RAJAO', 'Mamy', 'Daka', '2011-04-14', 'Louveteau', NULL, NULL, NULL, 'Solo Rajao', 'Vola Rasoana', '2023-09-22', 2, 16, 1, 3);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RASOA', 'Ny Avo', 'Fosa', '2007-08-20', 'Routier', NULL, NULL, NULL, 'Bema Rasoa', 'Fara Rabe', '2019-05-10', 2, 17, 2, 6);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RANDRIA', 'Tsiry', 'Lambo', '2013-01-30', 'Louveteau', NULL, NULL, NULL, 'Tiana Randria', 'Soa Ravelo', '2024-02-28', 2, 18, 5, 11);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RATSIM', 'Mahery', 'Fody', '2010-06-15', 'Louveteau', NULL, NULL, NULL, 'Nirina Ratsim', 'Mialy Rason', '2023-04-05', 2, 19, 6, 13);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RANAIVO', 'Hery', 'Sifaka', '2008-10-12', 'Éclaireur', NULL, NULL, NULL, 'Mamy Ranaivo', 'Tahina Rabe', '2021-08-18', 2, 20, 3, 8);

-- Élèves supplémentaires sans assurance
INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RABEHA', 'Lalao', 'Vorona', '2011-05-22', 'Louveteau', NULL, NULL, NULL, 'Herilala Rabeha', 'Vola Ramana', '2023-10-15', 2, NULL, 1, 1);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RAHOVA', 'Mirana', 'Akanga', '2009-09-08', 'Éclaireur', NULL, NULL, NULL, 'Fidy Rahova', 'Soa Ralaivo', '2022-06-30', 2, NULL, 4, 10);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RAFENO', 'Toky', 'Tsingy', '2012-02-18', 'Louveteau', NULL, NULL, NULL, 'Bema Rafeno', 'Fara Rasolo', '2024-01-08', 2, NULL, 2, 5);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RALAY', 'Sitrakiniaina', 'Hazo', '2007-07-25', 'Routier', NULL, NULL, NULL, 'Solo Ralay', 'Mialy Rabe', '2020-04-12', 2, NULL, 3, 7);

INSERT INTO personne (nom, prenom, totem, datenaissance, niveau, numerotelephone, image, numerocin, nompere, nommere, datefanekena, idtypepersonne, idassurance, idsecteur, idsection)
VALUES ('RAMANA', 'Tojo', 'Fosa', '2010-12-03', 'Louveteau', NULL, NULL, NULL, 'Hery Ramana', 'Lala Rasoana', '2023-07-20', 2, NULL, 5, 12);

-- =====================================
-- VÉRIFICATION DES DONNÉES
-- =====================================
-- Nombre de responsables: 10
-- Nombre d'élèves: 15
-- Total personnes: 25
-- Responsables avec assurance active: 8
-- Élèves avec assurance active: 10

