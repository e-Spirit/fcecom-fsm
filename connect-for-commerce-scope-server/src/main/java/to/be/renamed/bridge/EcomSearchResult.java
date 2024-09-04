package to.be.renamed.bridge;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class EcomSearchResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7831290492561868299L;
    private final List<T> result;
    private final int total;

    public EcomSearchResult(final List<T> result, final int total) {
        this.result = List.copyOf(result);
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public Iterator<T> getIterator() {
        return result.iterator();
    }

    public List<T> getResults() {
        return result;
    }
}
