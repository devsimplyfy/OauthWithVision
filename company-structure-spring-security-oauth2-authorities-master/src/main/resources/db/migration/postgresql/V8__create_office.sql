CREATE TABLE OFFICE
(
  ID BIGINT(50) NOT NULL,
  NAME varchar(200) NOT NULL,
  ADDRESS_ID BIGINT(50),
  DEPARTMENT_ID BIGINT(50),
  CONSTRAINT OFFICE_PKEY PRIMARY KEY (ID),
  CONSTRAINT FK4FFKMQMLNYV67LD0DCTCVJSFJ FOREIGN KEY (DEPARTMENT_ID)
      REFERENCES DEPARTMENT (ID) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FKAK81M3GKJ8XQ5T48XUFLBJ0KN FOREIGN KEY (ADDRESS_ID)
      REFERENCES ADDRESS (ID) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
#WITH (OIDS=FALSE);