import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Harshad
 * Date: 10/10/15
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class PostingComparator implements Comparator<Posting> {
    @Override
    public int compare(Posting o1, Posting o2) {
        return Integer.parseInt(o2.docId)-Integer.parseInt(o1.docId);
    }
}
