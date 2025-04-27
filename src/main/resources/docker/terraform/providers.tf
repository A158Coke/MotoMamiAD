terraform {
  required_version = ">= 1.4.0"

  required_providers {
    postgresql = {
      source  = "cyrilgdn/postgresql"
      version = "~> 1.13.0"
    }
    keycloak = {
      source  = "mrparkers/keycloak"
      version = ">= 3.0.0"
    }
  }
}

provider "postgresql" {
  host     = "localhost"  # 使用 localhost 连接宿主机
  port     = 5434
  username = "admin"
  password = "admin"
  database = "motomamiDB"
  sslmode  = "disable"
}

provider "keycloak" {
  client_id = "admin-cli"
  username  = "admin"
  password  = "admin"
  url       = "http://localhost:8180"
  realm     = "master"
}


