import java.io.*;

public class CSE535Assignment {

    public static void main(String[] args) throws IOException
    {
        String outFileName = args[1];
        int topK = Integer.parseInt(args[2]);
        String queryFileName = args[3];
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFileName));
        IndexAnalyzer analyzer = new IndexAnalyzer(args[0]);
        System.out.println("TopK Terms : ");
        analyzer.getTopKTerms(topK,writer);
        BufferedReader queryReader = new BufferedReader(new FileReader(queryFileName));
        String line = queryReader.readLine();
        while(line != null)
        {
            String terms[] = line.split(" ");
            for(int termIndex = 0; termIndex < terms.length; termIndex++)
            {
                    analyzer.getPostings(terms[termIndex],writer);
            }
            System.out.println("TAAT And");
            analyzer.termAtATimeQueryAnd(line, writer);
            System.out.println("TAAT Or");
            analyzer.termAtATimeQueryOR(line, writer);
            System.out.println("DAAT And");
            analyzer.docAtATimeQueryAnd(line, writer);
            System.out.println("DAAT Or");
            analyzer.docAtATimeQueryOR(line, writer);
            line = queryReader.readLine();
        }
        queryReader.close();
        writer.close();
        //analyzer.docAtATimeQueryAnd("ltv mill nii");
        //analyzer.docAtATimeQueryOR("move lower expect");
        //analyzer.docAtATimeQueryOR("said set speak spokesman");
        //analyzer.docAtATimeQueryAnd("said set speak spokesman", writer);
        //analyzer.termAtATimeQueryOR("said set speak spokesman");
    }
}
