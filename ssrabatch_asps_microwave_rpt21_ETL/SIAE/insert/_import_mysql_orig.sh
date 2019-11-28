#!/bin/bash
dir_bin=/ssrabatchprgs/ssrabatch_asps_microwave_rpt21_ETL/SIAE/insert
dir_csv=/SSRA_FileTransferDataEntries/DataEntry_SIAE/ 
file_csv=$dir_bin"/tmp/data_siae.csv" 
file_sql=$dir_bin"/batch_sql.sql"
fecha_filter=`date --date "-10 minutes" +"%Y_%m_%dT%H_%M"`
# filtro="device_statistics_"${fecha_filter:0:15}
filtro=`find $dir_csv -type f -newermt "2019-04-08" ! -newermt "2019-04-15" |wc -l`
# arr_file=`find $dir_csv -name $filtro*`
arr_file=`find $dir_csv -type f -newermt "2019-04-08" ! -newermt "2019-04-15"`
count_file=`find $dir_csv -name $filtro* |wc -l`
count=0
echo -e "Total archivos: "$filtro
B=($arr_file)
if [ ! -f $file_csv ]; then
	echo -e "event_date,nodo_id,element_identifier,power_level,transmission_bit_rate,dl_modulation,rltavg,nbi_sys" > $file_csv
fi

 while [ $count -le  `expr $filtro - 1 `  ]; do
	  csv=`echo ${B[$count]} |cut -d "/" -f 4 | cut -d "." -f 1`
	  fecha_siae=`echo ${csv:15:10} | sed s/_/-/g`" "`echo ${csv:26:4} | sed s/_/:/g`
	  IFS=';'
	  while read -r address identifier t_tx t_rx mod_rx mod_tx snr r_min r_max r_avg t_min t_max t_avg t_bytes
	  do
		# echo -e "Identifier:"$identifier
		t_max="z"$t_max
		if [ $t_max != "zTLTMAX" ] ; then
			parameter="z"`echo ${identifier:13:1}`
			parameter2="z"`echo ${identifier:0:2}`
			if [ $parameter == "zH" -o $parameter == "zC" ]; then
				if [ $parameter2 == "zAY" ]; then
					nodo_id=`echo $identifier |cut -d "C" -f 2 |cut -d "-" -f 2`
					nodo_id="HC-"$nodo_id
				else
					nodo_id=`echo $identifier |cut -d "C" -f 3 |cut -d "-" -f 2`
					nodo_id="HC-"$nodo_id
				fi
			else
				if [ $parameter2 == "zAY" ]; then
					nodo_id=`echo $identifier |cut -d "Y" -f 3 |cut -d "-" -f 2`
					nodo_id="AY-"$nodo_id
				else
					nodo_id=`echo $identifier |cut -d "Y" -f 2 |cut -d "-" -f 2`
					nodo_id="AY-"$nodo_id
				fi
			fi
			t_bytes=`echo $t_bytes |cut -d " " -f 1`
			identity="SNMP/SIAE"
			if [ $nodo_id != "AY-" ]; then
				echo -e $fecha_siae,$nodo_id,$identifier,$t_avg,$t_bytes,$mod_tx,$r_avg,$identity >>$file_csv
			fi
		fi
	  done < ${B[$count]}
	 let count=$count+1
 done

#Ejecutar importar datos en BD mysql
# /usr/bin/mysql -u root -p123456*abc gilatssra2 < $file_sql
# sleep 2
# mv $file_gilat $dir_bin"/tmp/"$ayer"_data_gilat.csv"
echo -e "Fin."