import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Harshad
 * Date: 10/12/15
 * Time: 9:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class PostingListLengthComparator implements Comparator<LinkedList<Posting>> {
    @Override
    public int compare(LinkedList<Posting> o1, LinkedList<Posting> o2) {
        return o1.size()-o2.size();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
