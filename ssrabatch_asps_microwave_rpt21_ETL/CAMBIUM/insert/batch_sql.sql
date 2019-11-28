LOAD DATA LOCAL INFILE '/ssrabatchprgs/ssrabatch_asps_microwave_rpt21_ETL/CAMBIUM/insert/tmp/data_cambium.csv'
INTO TABLE asps_microwave
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(@event_date,nodo_id,element_identifier,power_level,transmission_bit_rate,attenuation,dl_modulation,rltavg,nbi_sys)
SET event_date = str_to_date(@event_date,'%Y-%m-%d %H:%i:%s');
