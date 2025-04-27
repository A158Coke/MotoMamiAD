terraform {
  required_providers {
    postgresql = {
      source = "cyrilgdn/postgresql"
      version = "~> 1.13.0"
    }
  }
}


resource "postgresql_schema" "insurance_schema" {
  name = "insurance"
}

resource "postgresql_role" "app_user" {
  name     = "app_user"
  login    = true
  password = "apppassword"
}

resource "postgresql_grant" "grant_usage_on_insurance_schema" {
  database    = "motomamiDB"
  role        = postgresql_role.app_user.name
  schema      = postgresql_schema.insurance_schema.name
  object_type = "schema"
  privileges  = ["USAGE", "CREATE"]
}