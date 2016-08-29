CREATE TABLE boxes (
  id     SERIAL PRIMARY KEY,
  mac    MACADDR NOT NULL,
  label  INTEGER NOT NULL,
  online BOOLEAN NOT NULL,
  UNIQUE (mac, label)
);

CREATE TABLE labels (
  id     SERIAL PRIMARY KEY,
  box_id INTEGER REFERENCES boxes (id),
  name   TEXT NOT NULL UNIQUE
);

CREATE TABLE events (
  id       SERIAL PRIMARY KEY,
  occurred TIMESTAMP NOT NULL,
  box_id   INTEGER REFERENCES boxes (id),
  vote     INTEGER   NOT NULL
);
