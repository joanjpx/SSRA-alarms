#!/bin/bash
dir_bin=/ssrabatchprgs/ssrabatch_asps_microwave_rpt21_ETL/CAMBIUM/insert
dir_log=/ssrabatchprgs/ssrabatch_asps_microwave_rpt21_ETL/CAMBIUM/insert/log/
dir_csv=/SSRA_FileTransferDataEntries/DataEntry_CAMBIUM_tmp
file_csv=$dir_bin"/tmp/data_cambium.csv" 
file_sql=$dir_bin"/batch_sql.sql"
# fecha_filter=`date --date "-10 minutes" +"%Y_%m_%dT%H_%M"`
fecha_filter='2019_04_08T'
# filtro="device_statistics_"${fecha_filter:0:15}
patron=""
patron=$1
filtro="device_statistics_"$fecha_filter
ayer=`date -d "-10 minutes" '+%Y%m%d%H%M'`
#arr_file=`find $dir_csv -name $filtro*`
#count_file=`find $dir_csv -name $filtro* |wc -l`
arr_file=`ls -1 $dir_csv"/device_statistics_"*$patron*".csv"`
count_file=`ls -1 $dir_csv"/device_statistics_"*$patron*".csv" |wc -l`
count=0
echo -e "Total archivos: "$count_file
B=($arr_file)
if [ $count_file -gt 0 ]; then
	if [ ! -f $file_csv ]; then
		echo -e "event_date,nodo_id,element_identifier,power_level,transmission_bit_rate,dl_modulation,rltavg,nbi_sys" > $file_csv
	fi
	
	while [ $count -le  `expr $count_file - 1 `  ]; do
		csv=`echo ${B[$count]} |cut -d "/" -f 4 | cut -d "." -f 1`
		fecha_cambium=`echo ${csv:18:10} | sed s/_/-/g`" "`echo ${csv:29:8} | sed s/_/:/g`
		echo -e "File: "${B[$count]}
		IFS=','
		while read -r mac name type mode status ip frequency channel dl_rssi ul_rssi tx_power dl_snr ul_snr dl_modulation ul_modulation dl_mcs ul_mcs dl_throughput ul_throughput ul_pkts dl_pkts
		do 
			if [ $type == "pmp" ] && [ $mode == "sm" ]; then
				id_cambium=`echo $name |cut -d " " -f 3`
				identity="FTP/CAMBIUM"
				attenuation=`echo $tx_power + 10 - $dl_rssi |bc`
				echo -e $fecha_cambium,$id_cambium,$name,$tx_power,$dl_throughput,$attenuation,$ul_modulation,$dl_rssi,$identity >>$file_csv
			fi
		done < ${B[$count]}
	  mv ${B[$count]}  ${B[$count]}".old"
	  let count=$count+1
	done
	
	#Ejecutar importar datos en BD mysql
	/usr/bin/mysql -u fitel_userbd -pbdg1lat gilatssra2 < $file_sql


	if ! mysql -h10.254.12.100 -ussrauser -ps55R@D4 ssradb < $file_sql; then
		mysql -h10.254.12.100 -ussrauser -ps55R@D4 ssradb 2>> $dir_log error_mysql.log
	fi;
	
	sleep 2
	mv $file_csv $dir_bin"/tmp/"$ayer"_data_cambium.csv"
fi
echo -e "Fin."
