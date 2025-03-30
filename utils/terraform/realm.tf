resource "keycloak_realm" "realm" {
  realm             = "terraform"
  enabled           = true
  display_name      = "terraform"
  display_name_html = "<b>terraform</b>"

  login_theme = "keycloak.v2"

  access_code_lifespan = "1h"

  ssl_required    = "external"
  password_policy = ""


  registration_allowed           = true
  reset_password_allowed         = true
  remember_me                    = true
  login_with_email_allowed       = false
  registration_email_as_username = false
}

resource "keycloak_openid_client" "myClient" {
  realm_id    = keycloak_realm.realm.id
  name        = "my-client-terraform"
  description = "myClient"

  client_id     = "myClient"
  client_secret = "Py3ogB0RO389QsfH00yQBkWP1qeOu0Pf"

  enabled     = true
  access_type = "CONFIDENTIAL"

  standard_flow_enabled = true

  valid_redirect_uris = ["*"]
  web_origins = ["*"]
}

resource "keycloak_openid_client" "myBackendClient" {
  realm_id    = keycloak_realm.realm.id
  name        = "my-app-client"
  description = "myAppClient"

  client_id     = "myAppClient"
  client_secret = "kfASasd412ks;fdLADFfmds%124agmsd"

  enabled     = true
  access_type = "CONFIDENTIAL"
  admin_url = "*"

  # Enable Service Accounts
  service_accounts_enabled = true
}


resource "keycloak_user" "test_customer_1" {
  realm_id       = keycloak_realm.realm.id
  username       = "alicja"
  first_name     = "Alicja"
  last_name      = "Prohibicja"
  email          = "alicja@example.com"
  email_verified = true

  initial_password {
    value     = "alicja"
    temporary = false
  }

  attributes = {
    picture = "https://i.wpimg.pl/1200x/filerepo.grupawp.pl/api/v1/display/embed/7070aa6d-b760-437d-bf30-e6250b7e2a0b"
  }
}
