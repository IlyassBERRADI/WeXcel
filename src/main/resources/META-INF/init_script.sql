-- Crée la table Reference si elle n'existe pas
CREATE TABLE IF NOT EXISTS Reference
(
    Id                   SERIAL,
    Name                 VARCHAR(50),
    CreationDate         DATE,
    LastModificationDate TIMESTAMP,
    PRIMARY KEY (Id)
);

-- Crée la table Adam si elle n'existe pas
CREATE TABLE IF NOT EXISTS Adam_1_
(
    Id            INTEGER,
    IdRow         BIGINT,
    ColumnNumber  DOUBLE PRECISION,
    ColumnFormule VARCHAR(255),
    ColumnVarchar VARCHAR(255),
    PRIMARY KEY (IdRow, Id),
    FOREIGN KEY (Id) REFERENCES Reference (Id)
);

-- Insère des données dans la table Reference si elles n'existent pas déjà
INSERT INTO Reference (Name, CreationDate, LastModificationDate)
SELECT 'Adam', CURRENT_DATE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM Reference WHERE Name = 'Adam');