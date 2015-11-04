import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Harshad
 * Date: 10/19/15
 * Time: 12:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class LinkedListAggregator
{
    LinkedList<Posting> list;
    int numberOfComparisons;

    public LinkedListAggregator(LinkedList<Posting> list, int numberOfComparisons) {
        this.list = list;
        this.numberOfComparisons = numberOfComparisons;
    }

    boolean present;

    public LinkedListAggregator(LinkedList<Posting> list, int numberOfComparisons, boolean present) {
        this.list = list;
        this.numberOfComparisons = numberOfComparisons;
        this.present = present;
    }

    public LinkedList<Posting> getList() {
        return list;
    }

    public void setList(LinkedList<Posting> list) {
        this.list = list;
    }

    public int getNumberOfComparisons() {
        return numberOfComparisons;
    }

    public void setNumberOfComparisons(int numberOfComparisons) {
        this.numberOfComparisons = numberOfComparisons;
    }
}

