#!/bin/bash
SERVIDOR=$1
PORT=$2
USUARIO=$3
PASSWORD=$4
#SERVIDOR=10.254.12.129
#PORT=22
#USUARIO=ssrauser
#PASSWORD=!R3p0rt3sGilat
cd /root/
export SSHPASS=$PASSWORD
sshpass -e sftp -oBatchMode=no -b - $USUARIO@$SERVIDOR << !
cd reportes/SIAE_Reportes/SnmpSampler/
get $5 $6
bye
!