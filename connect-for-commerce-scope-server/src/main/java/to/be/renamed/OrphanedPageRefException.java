package to.be.renamed;

/**
 * Exception to throw if a page ref has no page
 */
public class OrphanedPageRefException extends RuntimeException {

    public OrphanedPageRefException(String message) {
        super(message);
    }
}
