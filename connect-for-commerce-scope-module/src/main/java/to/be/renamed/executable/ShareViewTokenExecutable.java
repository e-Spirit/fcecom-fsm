package to.be.renamed.executable;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.module.ServiceFactory;
import com.espirit.moddev.components.annotations.PublicComponent;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.script.Executable;

import java.io.Writer;
import java.util.Date;
import java.util.Map;

/**
 * This Executable generates an encrypted and signed JWT token to securely
 * exchange information about ShareView between the module and the Frontend aPI Backend
 * through a client connection.
 */
@PublicComponent(
    name = ProjectAppHelper.MODULE_NAME + " - Generate ShareView Token",
    displayName = ProjectAppHelper.MODULE_NAME + " - Executable: Generate ShareView Token")
public class ShareViewTokenExecutable extends ExecutableUtilities implements Executable {

    // Lifetime
    private static final int FALLBACK_LIFETIME = 60 * 60 * 1000; // 1h

    // Parameters
    private static final String PAGE_ID_PARAM = "id";
    private static final String PAGETYPE_PARAM = "type";
    private static final String FS_DRIVEN_PARAM = "fsDriven";
    private static final String LIFETIME_MS_PARAM = "lifetimeMs";
    private static final String UNIVERSAL_ALLOW_PARAM = "universalAllow";

    // Claims
    private static final String CLAIM_UNIVERSAL_ALLOW = "UniversalAllow";
    private static final String CLAIM_PAGE_ID = "PageId";
    private static final String CLAIM_PAGE_TYPE = "PageType";
    private static final String CLAIM_FS_DRIVEN = "FsDriven";

    // Response
    private static final String OK_PROPERTY = "ok";
    private static final String ERROR_PROPERTY = "error";
    private static final String TOKEN_PROPERTY = "token";

    private BaseContext context;

    /**
     * Generates a token with specific information about allowed ShareView pages.
     *
     * @return Encoded, encrypted and signed JWT token.
     */
    public Object execute(Map<String, Object> parameters, Writer out, Writer err) {
        setParameters(parameters);
        createContext(parameters);

        TokenGenerationResponse response;

        try {
            response = TokenGenerationResponse.ok(createEncryptedJWT().serialize());
        } catch (JOSEException e) {
            context.logError("[ShareView Generate Token Executable] Encryption failed: " + e.getMessage(), e);
            response = TokenGenerationResponse.error("Encryption failed: " + e.getMessage());
        } catch (NumberFormatException e) {
            response = TokenGenerationResponse.error("Number parameter invalid.");
        } catch (IllegalArgumentException e) {
            context.logError("[ShareView Generate Token Executable] Invalid secret: " + e.getMessage(), e);
            response = TokenGenerationResponse.error("Invalid secret");
        }

        return response.asJson();
    }

    private void createContext(Map<String, Object> parameters) {
        context = (BaseContext) parameters.get("context");
        context.logInfo("Received a request to create a reference page");
    }

    /**
     * This method adds claims to the JWT token.
     * The information lets the client choose between preview and release content
     * on a request basis. The following restrictions can be saved inside the token:
     * - Every page is allowed to be seen in preview.
     * - a single page is allowed to be seen in preview, providing the id and type of that page.
     * - How long is the token valid?
     *
     * @return String with token in encrypted and signed format.
     * @throws NumberFormatException if the provided lifetimeMs parameter isn't a number.
     */
    private JWTClaimsSet buildClaims() throws NumberFormatException {
        final JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder();

        // Page Specific
        final String id = getParam(PAGE_ID_PARAM);
        final String type = getParam(PAGETYPE_PARAM);
        final Boolean isFsDriven = getBooleanParam(FS_DRIVEN_PARAM);

        // Validity
        final long lifetimeMs = getLongParam(LIFETIME_MS_PARAM, FALLBACK_LIFETIME);

        // Is allowed on every page
        final Boolean universalAllow = getBooleanParam(UNIVERSAL_ALLOW_PARAM);

        claims.claim(CLAIM_FS_DRIVEN, isFsDriven);

        if (universalAllow != null && universalAllow) {
            claims.claim(CLAIM_UNIVERSAL_ALLOW, true);
        } else {
            claims.claim(CLAIM_PAGE_ID, id)
                .claim(CLAIM_PAGE_TYPE, type);
        }

        return claims.expirationTime(new Date(new Date().getTime() + lifetimeMs))
            .issueTime(new Date())
            .issuer("com:crownpeak:ecom:cfc:module")
            .subject("com:crownpeak:ecom:cfc:storefront:sharePreview")
            .build();
    }

    /**
     * Create a header for the JWT token with information about encryption type.
     *
     * @return JWEHeader Header containing encryption information.
     */
    private static JWEHeader buildJWEHeader() {
        return new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
            .contentType("JWT")
            .build();
    }

    /**
     * Creates a JWT token, then encrypts and signs it.
     *
     * @return Encrypted and signed JWT token
     * @throws JOSEException if encryption fails cause e.g. the secret is invalid.
     */
    private EncryptedJWT createEncryptedJWT() throws JOSEException {
        EncryptedJWT jwt = new EncryptedJWT(buildJWEHeader(), buildClaims());

        Base64URL jwtSecret = ServiceFactory.getProjectAppConfigurationService(context)
            .loadConfiguration().getJwtConfig().getJwtSecret();

        // Encrypt the JWT
        jwt.encrypt(new DirectEncrypter(jwtSecret.decode()));

        return jwt;
    }

    private record TokenGenerationResponse(Boolean ok, String token, String errorMessage) {

        private static TokenGenerationResponse ok(final String token) {
            return new TokenGenerationResponse(true, token, null);
        }

        private static TokenGenerationResponse error(final String errorMessage) {
            return new TokenGenerationResponse(false, null, errorMessage);
        }

        /**
         * Returns a JSON string to the client, so that JavaScript is able to process it.
         * <p>
         * ok:           Whether the token generation was successful or not.
         * token:        Token string if generation was successful.
         * errorMessage: Error message if generation wasn't successful.
         *
         * @return JSON containing information about ok state, token and error, where applicable.
         */
        public String asJson() {
            JsonObject result = new JsonObject();

            result.add(OK_PROPERTY, new JsonPrimitive(this.ok));
            result.add(TOKEN_PROPERTY, token != null ? new JsonPrimitive(this.token) : JsonNull.INSTANCE);
            result.add(ERROR_PROPERTY, errorMessage != null ? new JsonPrimitive(this.errorMessage) : JsonNull.INSTANCE);

            return result.toString();
        }
    }
}
