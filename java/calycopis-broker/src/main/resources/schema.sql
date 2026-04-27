--
-- <meta:header>
--   <meta:licence>
--     Copyright (c) 2024, Manchester (http://www.manchester.ac.uk/)
--
--     This information is free software: you can redistribute it and/or modify
--     it under the terms of the GNU General Public License as published by
--     the Free Software Foundation, either version 3 of the License, or
--     (at your option) any later version.
--
--     This information is distributed in the hope that it will be useful,
--     but WITHOUT ANY WARRANTY; without even the implied warranty of
--     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--     GNU General Public License for more details.
--
--     You should have received a copy of the GNU General Public License
--     along with this program.  If not, see <http://www.gnu.org/licenses/>.
--   </meta:licence>
-- </meta:header>
--


-- Create our transfer times table.
DROP TABLE IF EXISTS TransferTimes;
CREATE TABLE TransferTimes (
    ident   LONG GENERATED ALWAYS AS IDENTITY,
    source  VARCHAR(50) NOT NULL,
    seconds LONG
    );

INSERT INTO TransferTimes(source) VALUES ('AARNET_PER');
INSERT INTO TransferTimes(source) VALUES ('AARNET_PER_ND');
INSERT INTO TransferTimes(source) VALUES ('AUSRC_STORM');
INSERT INTO TransferTimes(source) VALUES ('AUSSRC_STORM');
INSERT INTO TransferTimes(source) VALUES ('CASRC_XRD');
INSERT INTO TransferTimes(source) VALUES ('CHSRC_DCACHE');
INSERT INTO TransferTimes(source) VALUES ('CHSRC_S3');
INSERT INTO TransferTimes(source) VALUES ('CHSRC_XRD');
INSERT INTO TransferTimes(source) VALUES ('CHSRC_XRD_DEV');
INSERT INTO TransferTimes(source) VALUES ('CHSRC_XRD_PROD');
INSERT INTO TransferTimes(source) VALUES ('CNAF');
INSERT INTO TransferTimes(source) VALUES ('CNSRC-SHAO-T1');
INSERT INTO TransferTimes(source) VALUES ('CNSRC_STORM');
INSERT INTO TransferTimes(source) VALUES ('CNSRC_STORM_YUN');
INSERT INTO TransferTimes(source) VALUES ('CNSRC_XRD');
INSERT INTO TransferTimes(source) VALUES ('DESY_DCACHE');
INSERT INTO TransferTimes(source) VALUES ('DESY_DCACHE_ND');
INSERT INTO TransferTimes(source) VALUES ('IDIA');
INSERT INTO TransferTimes(source) VALUES ('IDIA_ND');
INSERT INTO TransferTimes(source) VALUES ('IMPERIAL');
INSERT INTO TransferTimes(source) VALUES ('ITSRC_IRA_XRD');
INSERT INTO TransferTimes(source) VALUES ('ITSRC_OACT_XRD');
INSERT INTO TransferTimes(source) VALUES ('JPSRC_STORM');
INSERT INTO TransferTimes(source) VALUES ('JPSRC_STORM_PROD');
INSERT INTO TransferTimes(source) VALUES ('KRSRC_STORM');
INSERT INTO TransferTimes(source) VALUES ('LANCASTER');
INSERT INTO TransferTimes(source) VALUES ('LANCASTER_ND');
INSERT INTO TransferTimes(source) VALUES ('MANCHESTER');
INSERT INTO TransferTimes(source) VALUES ('MANCHESTER_ND');
INSERT INTO TransferTimes(source) VALUES ('NLSRC_DCACHE');
INSERT INTO TransferTimes(source) VALUES ('NLSRC_PROD_DCACHE');
INSERT INTO TransferTimes(source) VALUES ('SESRC_XRD');
INSERT INTO TransferTimes(source) VALUES ('SESRC_XRD_RBD');
INSERT INTO TransferTimes(source) VALUES ('SKAO_S3');
INSERT INTO TransferTimes(source) VALUES ('SPSRC_STORM');
INSERT INTO TransferTimes(source) VALUES ('STFC_STORM');
INSERT INTO TransferTimes(source) VALUES ('STFC_STORM_ND');
INSERT INTO TransferTimes(source) VALUES ('SWESRC-OSO-T0');
INSERT INTO TransferTimes(source) VALUES ('SWESRC-OSO-T1');
INSERT INTO TransferTimes(source) VALUES ('UKSRC-RAL-T1-TEST');
INSERT INTO TransferTimes(source) VALUES ('UKSRC_RAL_XRD_DEVCEPHFS');

UPDATE TransferTimes SET seconds = (ASCII(source) - 65) / 4 ;

DROP TABLE IF EXISTS ResponseTimes;
CREATE TABLE ResponseTimes (
    ident   LONG GENERATED ALWAYS AS IDENTITY,
    service VARCHAR(50) NOT NULL,
    seconds LONG
    );

INSERT INTO ResponseTimes(service, seconds) VALUES ('rucio.replica.delay',  60);
INSERT INTO ResponseTimes(service, seconds) VALUES ('manila.storage.delay', 30);
INSERT INTO ResponseTimes(service, seconds) VALUES ('cinder.storage.delay', 30);


