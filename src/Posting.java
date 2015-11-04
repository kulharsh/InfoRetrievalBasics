import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: Harshad
 * Date: 9/28/15
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class Posting implements Comparable<Posting> {
    String docId;
    int frequencyCount;

    public Posting(String postingString) {
        postingString = postingString.replaceAll("]","");
        postingString = postingString.trim();
        String[] split = postingString.split("/");
        this.docId = split[0];
        this.frequencyCount = Integer.parseInt(split[1]);
    }

    public String getPostingString()
    {
        return this.docId + "/" + String.valueOf(frequencyCount);
    }

    @Override
    public int compareTo(Posting o) {
        return this.frequencyCount - o.frequencyCount;
    }
    public static String StringifyLinkedList(LinkedList<Posting> list)
    {
        String result = "";
        ListIterator<Posting> iter = list.listIterator();
        while(iter.hasNext())
            result = result.concat(iter.next().docId).concat(", ");
        return result.substring(0,result.length()-2);
    }

    public static LinkedListAggregator AndLinkedLists(LinkedList<Posting> firstList, LinkedList<Posting> secondList)
    {
        LinkedList<Posting> result  = new LinkedList<Posting>();
        int numberOfComparisons = 0;
        int indexInFirst = 0;
        while(indexInFirst < firstList.size())
        {
            Posting firstPosting = firstList.get(indexInFirst);
            int indexInSecond = 0;
            while(indexInSecond < secondList.size())
            {
                Posting secondPosting = secondList.get(indexInSecond);
                if( firstPosting.docId.equals(secondPosting.docId))
                    result.add(firstPosting);
                else
                    numberOfComparisons++;
            indexInSecond++;
            }
            indexInFirst++;
        }

        return new LinkedListAggregator(result,numberOfComparisons);
    }

    public static LinkedListAggregator OrLinkedLists(LinkedList<Posting> first, LinkedList<Posting> second)
    {
        int numberOfComparisons = 0;
        LinkedList<Posting> result  = new LinkedList<Posting>();
        int index = 0;
        while( index < first.size())
        {
            result.add(first.get(index));
            index++;
        }
        index = 0;
        while(index < second.size())
        {
            Posting secondPosting = second.get(index);
            ListIterator<Posting> resultIterator = result.listIterator();
            boolean found = false;
            while(resultIterator.hasNext())
            {
                Posting temp = resultIterator.next();
                if(temp.docId.equals(secondPosting.docId))
                {
                    found = true;
                    break;
                }
                else
                    numberOfComparisons++;
            }
            if(!found)
                resultIterator.add(secondPosting);
            index++;
        }
        return new LinkedListAggregator(result,numberOfComparisons);
    }

    public static LinkedListAggregator isPresentInLinkedList(LinkedList<Posting> linkedList, Posting posting)
    {
        int numberOfComparisons = 0;
        ListIterator<Posting> iterator = linkedList.listIterator();
        boolean checkValue = false;
        while(iterator.hasNext())
        {
            if(iterator.next().docId.equals(posting.docId))
                checkValue = true;
            numberOfComparisons++;
        }
        return new LinkedListAggregator(linkedList,numberOfComparisons,checkValue);
    }
}
