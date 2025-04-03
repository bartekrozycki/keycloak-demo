resource "keycloak_realm" "realm" {
  realm             = "internal"
  enabled           = true
  display_name      = "internal"
  display_name_html = "<b>internal</b>"

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

resource "keycloak_openid_client" "oidcInternal" {
  realm_id    = keycloak_realm.realm.id
  name        = "my-client-internal"
  description = "internal"

  client_id     = "oidcInternal"
  client_secret = "wppl2"

  enabled     = true
  access_type = "CONFIDENTIAL"

  standard_flow_enabled = true

  valid_redirect_uris = ["*"]
  web_origins = ["*"]
}

resource "keycloak_openid_client" "directGrantInternal" {
  realm_id    = keycloak_realm.realm.id
  name        = "direct-grant-internal"
  description = "internal"

  client_id     = "directGrantInternal"
  client_secret = "wppl2"

  enabled     = true
  access_type = "CONFIDENTIAL"

  direct_access_grants_enabled = true

  web_origins = ["*"]
}

resource "keycloak_openid_client" "technicalInternal" {
  realm_id    = keycloak_realm.realm.id
  name        = "my-app-internal"
  description = "myAppClient"

  client_id     = "technicalInternal"
  client_secret = "wppl2"

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
