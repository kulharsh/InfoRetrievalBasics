import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Harshad
 * Date: 9/28/15
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class IndexAnalyzer {
    public HashMap<String, LinkedList<Posting>> docAtATimePostingsDictionary;
    public HashMap<String, LinkedList<Posting>> termAtATimePostingsDictionary;
    ArrayList<Term> listOfTerms;
    public IndexAnalyzer(String fileName) throws IOException {
        docAtATimePostingsDictionary = new HashMap<String, LinkedList<Posting>>();
        termAtATimePostingsDictionary = new HashMap<String, LinkedList<Posting>>();
        listOfTerms = new ArrayList<Term>();
        String line;
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        line = br.readLine();
        while(line != null)
        {
            String[] split = line.split("\\\\");
            LinkedList<Posting> postingList = generatePostingsListFromString(split[2]);
            docAtATimePostingsDictionary.put(split[0], postingList);
            LinkedList<Posting> termFrequencySortedPostings = (LinkedList<Posting>) postingList.clone();
            Collections.sort(termFrequencySortedPostings);
            termAtATimePostingsDictionary.put(split[0],termFrequencySortedPostings);
            listOfTerms.add(new Term(split[0],Integer.parseInt(split[1].substring(1,split[1].length()))));
            line = br.readLine();
        }
        Collections.sort(listOfTerms);
        //readFile line by line, construct index.
    }
    /*--------------------------------------------------------------------------------------------------------------------------------*/
    /* Get the top K terms in the index*/
    public String getTopKTerms(int k, BufferedWriter outFile) throws IOException {

        outFile.write("FUNCTION: getTopK ");
        outFile.write(k);
        outFile.newLine();
        String topKTerms="";
        Iterator<Term> termIterator = listOfTerms.iterator();
        while(termIterator.hasNext() && k > 0)
        {
            topKTerms = topKTerms.concat(termIterator.next().getText().concat(", "));
            k--;
        }
        outFile.write("Result:");
        outFile.write(topKTerms);
        outFile.newLine();
        return topKTerms;
    }
    /*--------------------------------------------------------------------------------------------------------------------------------*/

    /*Here we get all the postings for a given term*/
    public void getPostings(String term, BufferedWriter writer) throws IOException {
        writer.write("Function : getPostings ".concat(term));
        writer.newLine();
        if(!this.docAtATimePostingsDictionary.containsKey(term) || !this.termAtATimePostingsDictionary.containsKey(term))
        {
            writer.write("Term does not exist");
            return;
        }
        LinkedList<Posting>daat =  this.docAtATimePostingsDictionary.get(term);
        LinkedList<Posting>taat =  this.termAtATimePostingsDictionary.get(term);
        writer.write("Ordered by doc IDs : ");
        writer.write(Posting.StringifyLinkedList(daat));
        writer.newLine();
        writer.write("Ordered by TF : ");
        writer.write(Posting.StringifyLinkedList(taat));
        writer.newLine();
    }
    /*TAAT and : Here we go through the postings ist one at a time and create the AND list*/
    public LinkedList<Posting> termAtATimeQueryAnd(String termString, BufferedWriter writer) throws IOException {
        long start = System.currentTimeMillis();
        String[] terms = termString.split(" ");
        ArrayList<LinkedList<Posting>> allPostings = new ArrayList<LinkedList<Posting>>();

        writer.write("FUNCTION : termAtATimeQueryAnd ");
        writeTermsToFile(terms,writer);

        int postingsIndex=0;
        for(int index = 0; index < terms.length; index++)
        {
            if(!this.termAtATimePostingsDictionary.containsKey(terms[index]))
            {
                writer.write("Term not found ".concat(terms[index]));
                writer.newLine();
                return null;
            }
            allPostings.add(postingsIndex, getPostingsListFromTermDictionary(terms[index]));


        }
        LinkedList<Posting> AndLinkedList = (LinkedList<Posting>) allPostings.get(0).clone();
        LinkedListAggregator l = loopAndIntersect(allPostings,AndLinkedList);
        AndLinkedList = l.getList();
        int unOptimizedComparisons = l.getNumberOfComparisons();
        Collections.sort(allPostings,new PostingListLengthComparator());
        AndLinkedList = (LinkedList<Posting>) allPostings.get(0).clone();
        l = loopAndIntersect(allPostings,AndLinkedList);
        AndLinkedList = l.getList();
        int optimizedComparisons = l.getNumberOfComparisons();
        long end = System.currentTimeMillis();
        writer.write(String.valueOf(AndLinkedList.size()));
        writer.write(" documents are found");
        writer.newLine();
        writer.write(String.valueOf(unOptimizedComparisons));
        writer.write(" comparisons are made");
        writer.newLine();
        writer.write(String.valueOf(((end - start)/100.0)));
        writer.write(" seconds are used");
        writer.newLine();
        writer.write(String.valueOf(optimizedComparisons));
        writer.write(" comparisons are made with optimization");
        writer.newLine();
        writer.write("Result : ");
        writer.write(Posting.StringifyLinkedList(AndLinkedList));
        writer.newLine();
        //System.out.println(AndLinkedList.size());
        //System.out.println(Posting.StringifyLinkedList(AndLinkedList));
        return AndLinkedList;
    }
/*--------------------------------------------------------------------------------------------------------------------------------*/
    /*TAAT OR : Here we go through the lists one by one and create the OR list*/
    public LinkedList<Posting> termAtATimeQueryOR(String termString, BufferedWriter writer) throws IOException {
        long start = System.currentTimeMillis();
        String[] terms = termString.split(" ");
        ArrayList<LinkedList<Posting>> allPostings = new ArrayList<LinkedList<Posting>>();
        //ArrayList<ListIterator<Posting>> iterators  = new ArrayList<ListIterator<Posting>>();
        writer.write("FUNCTION : termAtATimeQueryOR ");
        writeTermsToFile(terms,writer);
        int postingsIndex=0;
        for(int index = 0; index < terms.length; index++)
        {
            if(!this.termAtATimePostingsDictionary.containsKey(terms[index]))
            {
                writer.write("Term not found "+terms[index]);
                writer.newLine();
                continue;
            }
            allPostings.add(postingsIndex, getPostingsListFromTermDictionary(terms[index]));
        }
        LinkedList<Posting> OrLinkedList = (LinkedList<Posting>) allPostings.get(0).clone();
        LinkedListAggregator l = loopAndUnion(allPostings,OrLinkedList);
        OrLinkedList = l.getList();
        int unOptimizedComparisons = l.getNumberOfComparisons();
        Collections.sort(allPostings,new PostingListLengthComparator());
        OrLinkedList = (LinkedList<Posting>) allPostings.get(0).clone();
        l = loopAndUnion(allPostings,OrLinkedList);
        OrLinkedList = l.getList();
        int optimizedComparisons = l.getNumberOfComparisons();


        long end = System.currentTimeMillis();
        writer.write(String.valueOf(OrLinkedList.size()));
        writer.write(" documents are found");
        writer.newLine();
        writer.write(String.valueOf(unOptimizedComparisons));
        writer.write(" comparisons are made");
        writer.newLine();
        writer.write(String.valueOf(((end - start)/100.0)));
        writer.write(" seconds are used");
        writer.newLine();
        writer.write(String.valueOf(optimizedComparisons));
        writer.write(" comparisons are made with optimization");
        writer.newLine();
        writer.write("Result : ");
        writer.write(Posting.StringifyLinkedList(OrLinkedList));
        writer.newLine();

        //Collections.sort(OrLinkedList);
        return OrLinkedList;
    }

/*--------------------------------------------------------------------------------------------------------------------------------*/

    /*DAAT AND : Here we go through all the postings lists, and create the required AND list*/
    public LinkedList<Posting> docAtATimeQueryAnd(String termString, BufferedWriter writer) throws IOException {
        String[] terms = termString.split(" ");
        long start  = System.currentTimeMillis();
        writer.write("FUNCTION : docAtATimeQueryAnd ");
        writeTermsToFile(terms,writer);
        ArrayList<LinkedList<Posting>> allPostings = new ArrayList<LinkedList<Posting>>();
        ArrayList<Integer> indexInList = new ArrayList<Integer>();
        int postingsIndex=0;
        for(int index = 0; index < terms.length; index++)
        {
            if(!this.docAtATimePostingsDictionary.containsKey(terms[index]))
            {
                writer.write("Term not found ".concat(terms[index]));
                writer.newLine();
                return null;
            }
            allPostings.add(postingsIndex, getPostingsList(terms[index]));
            indexInList.add(postingsIndex, 0);
        }
        LinkedList<Posting> addLinkedList = new LinkedList<Posting>();
        int numberOfComparisons = 0;
        while(true)
        {
            if(anyIteratorsReachedEnd(allPostings,indexInList))
                break;
            Posting first = allPostings.get(0).get(indexInList.get(0));
            int max = Integer.parseInt(first.docId);
            boolean equalTop = true;
            for(int index = 1; index < indexInList.size(); index++)
            {
                int current = Integer.parseInt(allPostings.get(index).get(indexInList.get(index)).docId);
                if(max < current)
                {
                    max = current;
                }
                if(current != Integer.parseInt(first.docId))
                    equalTop = false;
            }
            if(equalTop)
            {
                addLinkedList.add(first);
                incrementAllIndices(indexInList);
            }

            else
                numberOfComparisons += moveAllItersToMax(allPostings,indexInList,max);
        }
        long end = System.currentTimeMillis();
        writer.write(String.valueOf(addLinkedList.size()));
        writer.write(" documents are found");
        writer.newLine();
        writer.write(String.valueOf(numberOfComparisons));
        writer.write(" comparisons are made");
        writer.newLine();
        writer.write(String.valueOf(((end - start)/100.0)));
        writer.write(" seconds are used");
        writer.newLine();
        writer.write("Result : ");
        writer.write(Posting.StringifyLinkedList(addLinkedList));
        writer.newLine();
        //System.out.println(Posting.StringifyLinkedList(addLinkedList));
        return addLinkedList;
    }
/*--------------------------------------------------------------------------------------------------------------------------------*/
        /*DAAT Query : Here we go through all the postings list parallely, and create a OR list*/
    public LinkedList<Posting> docAtATimeQueryOR(String termString, BufferedWriter writer) throws IOException {
        long start = System.currentTimeMillis();
        String[] terms = termString.split(" ");
        Integer numberOfComparisons = new Integer(0);
        writer.write("FUNCTION : docAtATimeQueryOR ");
        writeTermsToFile(terms,writer);
        ArrayList<LinkedList<Posting>> allPostings = new ArrayList<LinkedList<Posting>>();
        ArrayList<ListIterator<Posting>> iterators  = new ArrayList<ListIterator<Posting>>();
        int postingsIndex=0;
        for(int index = 0; index < terms.length; index++)
        {
            if(!this.docAtATimePostingsDictionary.containsKey(terms[index]))
            {
                writer.write("Term not found ".concat(terms[index]));
                writer.newLine();
                continue;
            }
            allPostings.add(postingsIndex, getPostingsList(terms[index]));
            iterators.add(postingsIndex,allPostings.get(postingsIndex).listIterator());
        }
        LinkedList<Posting> orLinkedList = new LinkedList<Posting>();
        while(true)
        {
            if(allIteratorsReachedEnd(iterators))
                break;
            for(int index = 0; index < iterators.size(); index++)
            {
                if(iterators.get(index).hasNext()) {
                    Posting p = iterators.get(index).next();
                    LinkedListAggregator l = Posting.isPresentInLinkedList(orLinkedList,p);
                    numberOfComparisons +=l.getNumberOfComparisons();
                    if(!l.present)
                        orLinkedList.add(p);
                }
            }
        }
        Collections.sort(orLinkedList, new PostingComparator());
        long end = System.currentTimeMillis();
        writer.write(String.valueOf(orLinkedList.size()));
        writer.write(" documents are found");
        writer.newLine();
        writer.write(String.valueOf(numberOfComparisons));
        writer.write(" comparisons are made");
        writer.newLine();
        writer.write(String.valueOf(((end - start)/100.0)));
        writer.write(" seconds are used");
        writer.newLine();
        writer.write("Result : ");
        writer.write(Posting.StringifyLinkedList(orLinkedList));
        writer.newLine();
        //System.out.println(orLinkedList.size());
        //System.out.println(Posting.StringifyLinkedList(orLinkedList));
        return orLinkedList;
    }
/*--------------------------------------------------------------------------------------------------------------------------------
Beyond this are all the helper methods used in the above methods.
----------------------------------------------------------------------------------------------------------------------------------*/
    public LinkedList<Posting> getPostingsList(String term)
    {
        return this.docAtATimePostingsDictionary.get(term);
    }
    public  String getPostingsListString(String term)
    {
        if(!this.docAtATimePostingsDictionary.containsKey(term))
        {
                  return "term does not exist";
        }
        LinkedList<Posting> postingLinkedList = this.docAtATimePostingsDictionary.get(term);
        Iterator<Posting> postingIterator = postingLinkedList.iterator();
        String postingsListString = "";
        while(postingIterator.hasNext())
        {
                postingsListString = postingsListString.concat(postingIterator.next().getPostingString()).concat(", ");
        }
        return postingsListString;
    }
    private static LinkedList<Posting> generatePostingsListFromString(String longPostingsString) {

        String[] postings = longPostingsString.split(",");
        LinkedList<Posting> postingsList = new LinkedList<Posting>();
        Posting firstPosting = new Posting(postings[0].substring(2,postings[0].length()));
        postingsList.add(0,firstPosting);
        for(int postingNumber = 1; postingNumber < postings.length; postingNumber++)
        {
            Posting newPosting = new Posting(postings[postingNumber]);
            postingsList.add(newPosting);
        }
        return postingsList;
    }


    private void incrementAllIndices(ArrayList<Integer> indexInList) {
        for(int index = 0; index< indexInList.size();index++)
        {
            indexInList.set(index,indexInList.get(index)+1);
        }
    }

    private int moveAllItersToMax(ArrayList<LinkedList<Posting>> linkedLists, ArrayList<Integer> maxIterator, int max) {
        int comparisonCount = 0;
        for(int index = 0; index < linkedLists.size(); index++)
        {

            int indexInList = maxIterator.get(index);
            while(true)
            {
                if(indexInList < linkedLists.get(index).size())
                {
                    int current = Integer.parseInt(linkedLists.get(index).get(indexInList).docId);
                    if(current >= max)
                        break;
                    indexInList++;
                    comparisonCount++;
                }
                else
                    break;
            }
            maxIterator.set(index,indexInList);
        }
        return comparisonCount;
    }
    private boolean anyIteratorsReachedEnd(ArrayList<LinkedList<Posting>> allPostings, ArrayList<Integer> iterators) {
        for(int index = 0; index < iterators.size(); index++)
        {
            if(iterators.get(index) == allPostings.get(index).size())
                return true;
        }
        return false;
    }



    private boolean allIteratorsReachedEnd(ArrayList<ListIterator<Posting>> iterators)
    {
        for (ListIterator<Posting> iterator : iterators) {
            if (iterator.hasNext())
                return false;
        }
        return true;
    }


    private LinkedList<Posting> getPostingsListFromTermDictionary(String term) {
        return this.termAtATimePostingsDictionary.get(term);
    }

    private LinkedListAggregator loopAndIntersect(ArrayList<LinkedList<Posting>> allPostings, LinkedList<Posting> AndLinkedList)
    {
        int numberOfComparisons = 0;
        for(int postingIndex=1; postingIndex< allPostings.size(); postingIndex++)
        {
            LinkedListAggregator l =  Posting.AndLinkedLists(AndLinkedList,allPostings.get(postingIndex));
            numberOfComparisons += l.getNumberOfComparisons();
            AndLinkedList = l.getList();
        }
        return new LinkedListAggregator(AndLinkedList,numberOfComparisons);
    }


    private void writeTermsToFile(String[] terms, BufferedWriter writer) throws IOException {
        for(int index = 0; index < terms.length; index++)
        {
            writer.write(terms[index]);
            if(index < terms.length-1)
                writer.write(", ");
        }
        writer.newLine();
    }

    private LinkedListAggregator loopAndUnion(ArrayList<LinkedList<Posting>> allPostings, LinkedList<Posting> OrLinkedList) {
        int numberOfComparisons =0;
        for(int postingIndex=1; postingIndex< allPostings.size(); postingIndex++)
        {
            LinkedList<Posting> current = allPostings.get(postingIndex);
             LinkedListAggregator  l = Posting.OrLinkedLists(OrLinkedList,current);
            numberOfComparisons += l.getNumberOfComparisons();
            OrLinkedList = l.getList();

        }
        return new LinkedListAggregator(OrLinkedList,numberOfComparisons);
    }
}
