#!/bin/bash
#echo "Hola Mundo"
#SERVIDOR=$1
#PORT=$2
#USUARIO=$3
#PASSWORD=$4
ruta_bin=/ssrabatchprgs/ssrabatch_asps_microwave_rpt21_ETL/SIAE
ruta_download=/SSRA_FileTransferDataEntries/DataEntry_SIAE/     #$7
SERVIDOR=10.254.12.129
PORT=22
USUARIO=ssrauser
PASSWORD=!R3p0rt3sGilat
fecha=`date --date "-10 minutes" +"%d_%m_%Y_%H%M"`
filtro="25_snmp_export_"${fecha:0:14}
echo -e "archivo:"$filtro


salida=`$ruta_bin/siae_demo.sh $SERVIDOR $PORT $USUARIO $PASSWORD |tail -10 | grep "$filtro"`
		echo -e "File"$salida
		B=($salida)
		echo -e "0:"${B[0]}             #$5
download=`$ruta_bin/siae_get.sh $SERVIDOR $PORT $USUARIO $PASSWORD ${B[0]} $ruta_download`		
echo -e "Fin."

