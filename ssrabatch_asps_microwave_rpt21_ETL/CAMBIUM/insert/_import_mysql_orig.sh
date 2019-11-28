#!/bin/bash
dir_bin=/ssrabatchprgs/ssrabatch_asps_microwave_rpt21_ETL/cambium/insert
dir_csv=/SSRA_FileTransferDataEntries/DataEntry_CAMBIUM_tmp/ 
file_csv=$dir_bin"/tmp/data_cambium.csv" 
file_sql=$dir_bin"/batch_sql.sql"
# fecha_filter=`date --date "-10 minutes" +"%Y_%m_%dT%H_%M"`
fecha_filter='2019_04_08T'
# filtro="device_statistics_"${fecha_filter:0:15}
filtro="device_statistics_"$fecha_filter
arr_file=`find $dir_csv -name $filtro*`
count_file=`find $dir_csv -name $filtro* |wc -l`
count=0
echo -e "Total archivos: "$count_file
B=($arr_file)
if [ ! -f $file_csv ]; then
	echo -e "event_date,nodo_id,element_identifier,power_level,transmission_bit_rate,dl_modulation,rltavg,nbi_sys" > $file_csv
fi

 while [ $count -le  `expr $count_file - 1 `  ]; do
	  csv=`echo ${B[$count]} |cut -d "/" -f 4 | cut -d "." -f 1`
	  fecha_cambium=`echo ${csv:18:10} | sed s/_/-/g`" "`echo ${csv:29:8} | sed s/_/:/g`
	  IFS=','
	  while read -r mac name type mode status ip frequency channel dl_rssi ul_rssi tx_power dl_snr ul_snr dl_modulation ul_modulation dl_mcs ul_mcs dl_throughput ul_throughput ul_pkts dl_pkts
	  do 
		if [ $type == "pmp" ] && [ $mode == "sm" ]; then
			name_inverse=`echo $name |cut -d " " -f 3`" "`echo $name |cut -d " " -f 2`" "`echo $name |cut -d " " -f 1`
			id_cambium=`echo $name |cut -d " " -f 3`
			identity="FTP/CAMBIUM"
			echo -e $fecha_cambium,$id_cambium,$name,$tx_power,$ul_pkts,$ul_modulation,$identity >>$file_csv
			# echo -e "nombre real":$name
			# echo -e "nombre inverso":$identity
		fi
	  done < ${B[$count]}
	 let count=$count+1
 done

#Ejecutar importar datos en BD mysql
/usr/bin/mysql -u root -p123456*abc gilatssra2 < $file_sql
sleep 2
# mv $file_gilat $dir_bin"/tmp/"$ayer"_data_gilat.csv"
echo -e "Fin."