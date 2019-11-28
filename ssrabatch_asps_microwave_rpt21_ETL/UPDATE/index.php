<?php

require_once '/ssrabatchprgs/ssrabatch_asps_microwave_rpt21_ETL/UPDATE/Db.php';

$f_fin    = date('Y-m-d H:i:s', strtotime('-20 minute', strtotime(date('Y-m-d H:i:s'))));
$f_ini    = date('Y-m-d H:i:s', strtotime('-24 hour', strtotime($f_fin)));

if(isset($argv) && count($argv) == 3){

	$fechaInicio = isset($argv[1]) ? $argv[1] : false;
	$fechaFin = isset($argv[2]) ? $argv[2] : false;

	if(!$fechaInicio || !$fechaFin){
		echo "Parametros incorrectos\n"; die();
	}

	function is_date($fecha){
		$year = substr($fecha,0,4);
		$month = substr($fecha,5,2);
		$day = substr($fecha,8,2);
		return @checkdate($month,$day,$year);
	}

	if(!is_date($fechaInicio) || !is_date($fechaFin)){
		echo "Uno de los parametros no corresponde a una fecha.\n"; die();
	}
	
	$f_ini = "{$fechaInicio} 00:00:00";
	$f_fin = "{$fechaFin} 23:59:59";
}

// ##################### QUERY LOCAL ##########################
$db   = new Db();
$data = $db->queryAll("
  select
      a.event_date
      ,a.id
      ,a.power_level
      ,(
          select b.id 
          from asps_microwave b 
          where b.nodo_id = left(a.element_identifier,7)
          and left(b.element_identifier,7) = right(a.element_identifier,7)
          and b.event_date = a.event_date
      ) as dep
      ,left(a.element_identifier,7) as izquierda
      ,right(a.element_identifier,7) as derecha
      ,a.nodo_id
      ,a.element_identifier
  from asps_microwave a
  where a.event_date >= '{$f_ini}'
  and a.event_date <= '{$f_fin}'
  and (attenuation is null or attenuation = '');
");


foreach ($data as $item) {
  if (!is_null($item["dep"])) {
    $dep         = $db->queryRow("
      select b.rltavg 
      from asps_microwave b 
      where b.id = '{$item["dep"]}'
    ");
    $attenuation = $item["power_level"] - $dep["rltavg"] + 10;

    $update = $db->update("asps_microwave", [
        "attenuation" => $attenuation
      ], "id = {$item["id"]}");

    if (!$update) {
      echo "Error al momento de actualizar" . PHP_EOL;
    } {
      echo "Actualizado correctamente" . PHP_EOL;
    }
  }
}

$db->close();
die();


// ##################### QUERY MIRROR ##########################
$db   = new DbM();
$data = $db->queryAll("
  select
      a.event_date
      ,a.id
      ,a.power_level
      ,(
          select b.id 
          from asps_microwave b 
          where b.nodo_id = left(a.element_identifier,7)
          and left(b.element_identifier,7) = right(a.element_identifier,7)
          and b.event_date = a.event_date
      ) as dep
      ,left(a.element_identifier,7) as izquierda
      ,right(a.element_identifier,7) as derecha
      ,a.nodo_id
      ,a.element_identifier
  from asps_microwave a
  where a.event_date >= '{$f_ini}'
  and a.event_date <= '{$f_fin}'
  and (attenuation is null or attenuation = '');
");


foreach ($data as $item) {
  if (!is_null($item["dep"])) {
    $dep         = $db->queryRow("
      select b.rltavg 
      from asps_microwave b 
      where b.id = '{$item["dep"]}'
    ");
    $attenuation = $item["power_level"] - $dep["rltavg"] + 10;

    $update = $db->update("asps_microwave", [
        "attenuation" => $attenuation
      ], "id = {$item["id"]}");

    if (!$update) {
      echo "Error al momento de actualizar" . PHP_EOL;
    } {
      echo "Actualizado correctamente" . PHP_EOL;
    }
  }
}

$db->close();
die();
