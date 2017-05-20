package maze.data;

/**
 * Created by dvird on 17/04/15.
 */
public enum PostgreSQLErrorCodes {

    INTEGRITY_CONSTRAINT_VIOLATION (23000),
    RESTRICT_VIOLATION (23001),

    NOT_NULL_VIOLATION (23502),
    FOREIGN_KEY_VIOLATION(23503),
    UNIQUE_VIOLATION(23505),
    CHECK_VIOLIATION  (23514);


    private final int errorCode;
    PostgreSQLErrorCodes(int errorCode)
    {
        this.errorCode = errorCode;
    }
    public int getValue()
    {
        return errorCode;
    }
}
