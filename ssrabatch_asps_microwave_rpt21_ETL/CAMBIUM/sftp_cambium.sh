#!/bin/bash
#echo "Hola Mundo"
#SERVIDOR=$1
#PORT=$2
#USUARIO=$3
#PASSWORD=$4
ruta_bin=/ssrabatchprgs/ssrabatch_asps_microwave_rpt21_ETL/CAMBIUM
ruta_download=/SSRA_FileTransferDataEntries/DataEntry_CAMBIUM_tmp/     #$7
SERVIDOR=10.254.12.129
PORT=22
USUARIO=ssrauser
PASSWORD=!R3p0rt3sGilat
fecha=`date --date "-10 minutes" +"%Y_%m_%dT%H_%M"`
filtro="device_statistics_"${fecha:0:15}

salida=`$ruta_bin/cambium_demo.sh $SERVIDOR $PORT $USUARIO $PASSWORD |tail -10 | grep "$filtro"`
		B=($salida)
		echo -e "0:"${B[0]}             #$5
		echo -e "1:"${B[1]}             #$6
download=`$ruta_bin/cambium_get.sh $SERVIDOR $PORT $USUARIO $PASSWORD ${B[0]} ${B[1]} $ruta_download`		
echo -e "Fin."

