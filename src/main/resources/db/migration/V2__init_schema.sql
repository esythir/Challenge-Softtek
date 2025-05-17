CREATE TABLE TB_AUTH_MAP (
    sub        VARCHAR2(255)  PRIMARY KEY,
    user_uuid  RAW(16)        NOT NULL UNIQUE
);
