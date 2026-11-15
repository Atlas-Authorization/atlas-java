package net.atlasauth.atlas.machine;

/** Atlas refused a machine request, or the machine is not enrolled. {@code code} is the error code when the response had one. */
public class MachineClientException extends RuntimeException {
    private final int status;
    private final String code;

    public MachineClientException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    /** The HTTP status, or 0 for a local failure ({@code not_enrolled}, {@code dpop_not_configured}). */
    public int status() {
        return status;
    }

    public String code() {
        return code;
    }
}
