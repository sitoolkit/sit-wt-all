CREATE TABLE tab1 (
  ID char(3) PRIMARY KEY,
  col1 char(3),
  col2 char(3)
);

INSERT INTO tab1 (
    ID, col1, col2
) VALUES (
    '001',
    '111',
    '222'
),
(
    '002',
    '111',
    '333'
);