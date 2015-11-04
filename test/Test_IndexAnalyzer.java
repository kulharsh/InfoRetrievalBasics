import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Harshad
 * Date: 9/30/15
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test_IndexAnalyzer
{
    @Test
    public void test_dictionaryLength() throws IOException {
         IndexAnalyzer analyzer = new IndexAnalyzer("term.idx");
        Assert.assertEquals(24217,analyzer.docAtATimePostingsDictionary.size());

    }
    @Test
    public void test_dictionaryContainsKey() throws IOException {
        IndexAnalyzer analyzer = new IndexAnalyzer("term.idx");
        Assert.assertEquals(true,analyzer.docAtATimePostingsDictionary.containsKey("zone"));
    }
    @Test
    public void test_postingListLength() throws IOException {
        IndexAnalyzer analyzer = new IndexAnalyzer("term.idx");
        Assert.assertEquals(6, analyzer.getPostingsList("zone").size());
    }
    @Test
    public void test_postingListString() throws IOException {
        IndexAnalyzer analyzer = new IndexAnalyzer("term.idx");
        Assert.assertEquals("0010083/2, 0010193/1, 0010193/1, 0010353/2, 0011026/1, 0012135/2, ", analyzer.getPostingsListString("zone"));
    }
    @Test
    public void test_dictionaryContainsKey2() throws IOException {
        IndexAnalyzer analyzer = new IndexAnalyzer("term.idx");
        Assert.assertEquals(true,analyzer.docAtATimePostingsDictionary.containsKey("end"));
    }
    @Test
    public void test_postingListLength2() throws IOException {
        IndexAnalyzer analyzer = new IndexAnalyzer("term.idx");
        Assert.assertEquals(420, analyzer.getPostingsList("end").size());
    }
    @Test
    public void test_dictionaryContainsKey3() throws IOException {
        IndexAnalyzer analyzer = new IndexAnalyzer("term.idx");
        Assert.assertEquals(true,analyzer.docAtATimePostingsDictionary.containsKey("encroach"));
    }
    @Test
    public void test_postingListLength3() throws IOException {
        IndexAnalyzer analyzer = new IndexAnalyzer("term.idx");
        Assert.assertEquals(1, analyzer.getPostingsList("encroach").size());
    }
    @Test
    public void test_postingListString2() throws IOException {
        IndexAnalyzer analyzer = new IndexAnalyzer("term.idx");
        Assert.assertEquals("term does not exist", analyzer.getPostingsListString("UnKnownStuff"));
    }
    }
