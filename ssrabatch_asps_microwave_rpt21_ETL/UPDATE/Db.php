<?php

class Db {

  private $_host   = '127.0.0.1';
  private $_user   = 'fitel_userbd';
  private $_pass   = 'bdg1lat';
  private $_dbname = 'gilatssra2';
  private $_conn;

  public function __construct() {
    $this->_conn = new mysqli($this->_host, $this->_user, $this->_pass, $this->_dbname);

    if (!$this->_conn) {
      throw new Exception("Connection failed: " . mysqli_connect_error(), 500);
    }
    echo "Conexión a la base de datos exitosa" . PHP_EOL;
  }

  public function close() {
    echo "Cerrando conexión de la base de datos" . PHP_EOL;
    $this->_conn->close();
  }

  public function queryAll($sql) {
    $query  = $this->_conn->query($sql);
    $result = [];

    if ($query->num_rows > 0) {
      while ($row = $query->fetch_assoc()) {
        $result[] = $row;
      }
      $query->free();
    }

    return $result;
  }

  public function queryRow($sql) {
    $query  = $this->_conn->query($sql);
    $result = [];

    if ($query) {
      $result = $query->fetch_assoc();
      $query->free();
    }

    return $result;
  }

  public function update($table, $sets, $where) {
    $columns = "";
    foreach ($sets as $column => $value) {
      $columns .= "{$column}= '{$value}',";
    }
    $columns = substr($columns, 0, -1);

    $sql    = "UPDATE  {$table} SET $columns WHERE {$where};";

    $update = $this->_conn->prepare($sql);
    $result = $update->execute();

    return $result;
  }

  public function insert($table, $columns, $rows) {
    $table_columns = implode(",", $columns);

    $sql = "INSERT INTO {$table}($table_columns) VALUES";

    foreach ($rows as $row) {
      $line = "(";
      foreach ($row as $value) {
        if (empty($value) || $value == '' || is_null($value)) {
          $line .= "NULL,";
        } else {
          if (is_string($value)) {
            $line .= "'{$value}',";
          }
          if (is_int($value)) {
            $line .= "{$value},";
          }
        }
      }
      $line = substr($line, 0, -1);
      $sql  .= "{$line}),";
    }

    $sql    = substr($sql, 0, -1);
//        echo $sql;die();
    $insert = $this->_conn->prepare($sql);
    $result = $insert->execute();

    return $result;
  }

}

class DbM {

  private $_host   = '10.254.12.100';
  private $_user   = 'ssrauser';
  private $_pass   = 's55R@D4';
  private $_dbname = 'ssradb';
  private $_conn;

  public function __construct() {
    $this->_conn = new mysqli($this->_host, $this->_user, $this->_pass, $this->_dbname);

    if (!$this->_conn) {
      throw new Exception("Connection failed: " . mysqli_connect_error(), 500);
    }
    echo "Conexión a la base de datos exitosa" . PHP_EOL;
  }

  public function close() {
    echo "Cerrando conexión de la base de datos" . PHP_EOL;
    $this->_conn->close();
  }

  public function queryAll($sql) {
    $query  = $this->_conn->query($sql);
    $result = [];

    if ($query->num_rows > 0) {
      while ($row = $query->fetch_assoc()) {
        $result[] = $row;
      }
      $query->free();
    }

    return $result;
  }

  public function queryRow($sql) {
    $query  = $this->_conn->query($sql);
    $result = [];

    if ($query) {
      $result = $query->fetch_assoc();
      $query->free();
    }

    return $result;
  }

  public function update($table, $sets, $where) {
    $columns = "";
    foreach ($sets as $column => $value) {
      $columns .= "{$column}= '{$value}',";
    }
    $columns = substr($columns, 0, -1);

    $sql    = "UPDATE  {$table} SET $columns WHERE {$where};";

    $update = $this->_conn->prepare($sql);
    $result = $update->execute();

    return $result;
  }

  public function insert($table, $columns, $rows) {
    $table_columns = implode(",", $columns);

    $sql = "INSERT INTO {$table}($table_columns) VALUES";

    foreach ($rows as $row) {
      $line = "(";
      foreach ($row as $value) {
        if (empty($value) || $value == '' || is_null($value)) {
          $line .= "NULL,";
        } else {
          if (is_string($value)) {
            $line .= "'{$value}',";
          }
          if (is_int($value)) {
            $line .= "{$value},";
          }
        }
      }
      $line = substr($line, 0, -1);
      $sql  .= "{$line}),";
    }

    $sql    = substr($sql, 0, -1);
//        echo $sql;die();
    $insert = $this->_conn->prepare($sql);
    $result = $insert->execute();

    return $result;
  }

}
