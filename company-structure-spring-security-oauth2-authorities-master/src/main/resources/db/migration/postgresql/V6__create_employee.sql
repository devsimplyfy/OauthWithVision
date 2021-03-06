CREATE TABLE EMPLOYEE
(
  ID BIGINT(50) NOT NULL,
  NAME varchar(200) NOT NULL,
  SURNAME varchar(200) NOT NULL,
  ADDRESS_ID BIGINT(50),
  DEPARTMENT_ID BIGINT(50),
  CONSTRAINT EMPLOYEE_PKEY PRIMARY KEY (ID),
  CONSTRAINT FKBEJTWVG9BXUS2MFFSM3SWJ3U9 FOREIGN KEY (DEPARTMENT_ID)
      REFERENCES DEPARTMENT (ID) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT FKGA73HDTPB67TWLR9C1I337TYT FOREIGN KEY (ADDRESS_ID)
      REFERENCES ADDRESS (ID) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
#WITH (OIDS=FALSE);