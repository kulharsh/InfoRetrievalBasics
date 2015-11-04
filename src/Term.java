/**
 * Created with IntelliJ IDEA.
 * User: Harshad
 * Date: 9/30/15
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Term implements Comparable<Term>{
    private  String text;
    private int postingListLength;

    public Term(String text, int count) {
        this.text = text;
        this.postingListLength = count;
    }

    @Override
    public int compareTo(Term otherTerm) {
        return otherTerm.getPostingListLength() - this.postingListLength;
    }

    public int getPostingListLength() {
        return postingListLength;
    }

    public String getText() {
        return text;
    }
}
