package to.be.renamed.module.projectconfig.model;

import com.nimbusds.jose.util.Base64URL;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representation of the general configuration.
 */
public class JwtConfig implements Serializable {

    private static final long serialVersionUID = 2546331339325619361L;
    private final Base64URL jwtSecret;

    /**
     * Required by Gson.
     * Creates a general configuration with default values.
     * (jwtSecret = "")
     */
    public JwtConfig() {
        this(new Base64URL(""));
    }

    /**
     * Creates a general configuration
     *
     * @param jwtSecret Secret key to encrypt and sign JWT tokens.
     */
    public JwtConfig(Base64URL jwtSecret) {
        this.jwtSecret = Objects.requireNonNullElse(jwtSecret, new Base64URL(""));
    }

    /**
     * Creates a jwt config from strings, uses true if strings are not parseable.
     * Needed when reading values from file.
     *
     * @param jwtSecretString Secret key to encrypt and sign JWT tokens.
     * @return A jwt configuration
     */
    public static JwtConfig fromStrings(String jwtSecretString) {
        return new JwtConfig(new Base64URL(jwtSecretString));
    }

    public Base64URL getJwtSecret() {
        return jwtSecret;
    }

    @Override
    public String toString() {
        return "{jwtSecret: ****}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JwtConfig that = (JwtConfig) o;
        return Objects.equals(getJwtSecret(), that.getJwtSecret());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getJwtSecret());
    }
}
