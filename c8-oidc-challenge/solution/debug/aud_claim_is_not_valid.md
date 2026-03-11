### Scenario: `aud` mismatch – step-by-step debugging checklist

Use these checkpoints whenever you see:

`www-authenticate: Bearer error="invalid_token",error_description="An error occurred while attempting to decode the Jwt: The aud claim is not valid"`

This usually means the aud (audience) in the JWT does not match what Web Modeler REST API is configured to accept.
1. **Confirm what Camunda expects**
    - Find the audience value in your config for the failing component:
        - Web Modeler REST API: `webModeler.publicApiAudience` and `CAMUNDA_MODELER_SECURITY_JWT_AUDIENCE_PUBLIC_API`
        - Orchestration REST API: `orchestration.security.authentication.oidc.audience`
        - Other components: their respective `audience` / `*_AUDIENCE` settings
    - Write down the *exact* GUID/string Camunda expects as `aud`.

2. **Decode the actual token**
    - Get the `access_token`
      - ```bash
        curl --location --request POST '${TOKENURL}' \
        --header 'Content-Type: application/x-www-form-urlencoded' \
        --data-urlencode "client_id=${CLIENT_ID}" \
        --data-urlencode "client_secret=${CLIENT_SECRET}" \
        --data-urlencode "scope=${SCOPE}" \
        --data-urlencode 'grant_type=client_credentials'
        ```
    - Sample response: 
      - ```bash
        {"token_type": "Bearer",
        "expires_in": 3599,
        "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIn..."
        }
        ```
    - Take the `access_token` used in the failing call and paste it into https://jwt.io/.
    - Note the value of:
        - `aud` (audience)
        - `iss` (issuer)
        - `azp` or `client_id` (which app issued the token, if present)

3. **Compare expected vs actual audience**
    - Check: `token.aud == <expected audience from config>?`
    - If **no**:
        - The token was issued **for a different app** (e.g. WebApp or SPA instead of WebAPI).
        - Fix the **token request**:
            - Use the correct resource in `scope`, e.g. `<WebAPI_API_ID>/.default` instead of `<WebApp_APP_ID>/.default`.
            - Or switch to the correct client_id if you’re using the wrong app as caller.

4. **Check Azure AD permissions (client-credentials only)**
    - If you changed `scope` to the correct API but now see `{"error":"invalid_grant","error_description":AADSTS501051: Application...is not assigned to a role for the application`
        - Go to the **resource API** app in Entra (the one whose ID you use as `aud`):
            - Ensure it **Exposes an API** and defines at least one **App role** (Application permission).
        - Go to the **calling app** (pipeline client or WebAPI client):
            - Under **API permissions → My APIs**, add the Application permission for that resource API.
            - Click **Grant admin consent**.
    - Retry the token request; it should now succeed and produce a token with the correct `aud`.

5. **Re-test against the Camunda endpoint**
    - Call the failing endpoint again, e.g.:
    - ```bash 
      curl -i \
      -H "Authorization: Bearer ${TOKEN}" \
      "${COMPONENT_BASE_URL}/api/v1/info"
      ```
   - If `aud` and `iss` now match **and** the JWKS is correct, the `aud claim is not valid` error should disappear.
