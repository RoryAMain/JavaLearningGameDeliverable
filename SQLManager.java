import java.sql.*;
import java.util.*;
 
/**
 *
 * @author sqlitetutorial.net
 */
public class SQLManager {
	
	public ArrayList<String> topicNames;
	private static ArrayList<String> allTopicNames;
	
	public static ArrayList<String> getAllTopicNames(){
		return allTopicNames;
	}
	
	private Connection connect() {
		
        // SQLite connection string
		String url = "jdbc:sqlite:TriviaGame.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
 
    private void selectAll(){
        String sql = "SELECT Answer , Question FROM QA";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("Answer") + "\t" + rs.getString("Question"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private ResultSet getQuery(String sqlIn){
        String sql = sqlIn;
        ResultSet rs = null;
        try{ Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             rs = stmt.executeQuery(sql);
            }
            
         catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        	return rs;
    }
    
    private ResultSet validTopics()
    {
    	ResultSet valid = getQuery("SELECT DISTINCT Topic FROM QA GROUP BY Topic HAVING count(*) > 2");
    	return valid;
    	
    }
    
    public void initializeAllTopics(){
    	ResultSet allTopics = validTopics();
    	allTopicNames = new ArrayList<String>();
    	try{
    		while(allTopics.next())
    		{
    			//System.out.print(allTopics.getString("Topic") + ", ");
    			allTopicNames.add(allTopics.getString("Topic"));
    		}
    	}
    	 catch(SQLException e)
         {
         	System.out.println(e.getMessage());
         }
    }
    
    
    private ArrayList<ResultSet> chooseTopics()
    {
    	ArrayList<String> topicsChosen = new ArrayList<String>();
    	topicsChosen = TriviaGlue.getCategoryNames();
    	
    	topicNames = topicsChosen;
    	
    	ResultSet tempSet;
    	ArrayList<ResultSet> topicResults = new ArrayList<ResultSet>();
    	for(int y =0;y < topicsChosen.size();y++)
    	{
    		tempSet = getQuery("SELECT * FROM QA WHERE Topic = \"" + topicsChosen.get(y) + "\"");
    		topicResults.add(tempSet);
    	}
    	
    	return topicResults;
    }
    
    private int maxTopics()
    {
    	ResultSet distinctTopics = getQuery("SELECT DISTINCT Topic FROM QA");
    	int count = 0;
    	try{
    		while(distinctTopics.next())
    		{
    			count++;
    		}
    	}
    	
    	 catch(SQLException e)
         {
         	System.out.println(e.getMessage());
         }
    	
    	return count;
    }
    
    public void generateBoard(SQLManager app, TriviaBoard theBoard)
    {
		ArrayList<ResultSet> topicList = app.chooseTopics();
		        
		        ArrayList<ArrayList<ArrayList<TriviaTile>>> possibleTiles = new ArrayList<ArrayList<ArrayList<TriviaTile>>>();
		        try{
		        
			        for(int x = 0; x < topicList.size();x++)
			        {
			            ArrayList<ArrayList<TriviaTile>> pointSeperator = new ArrayList<ArrayList<TriviaTile>>();
			            ArrayList<TriviaTile> Points100 = new ArrayList<TriviaTile>();
		        		ArrayList<TriviaTile> Points200 = new ArrayList<TriviaTile>();
		        		ArrayList<TriviaTile> Points300 = new ArrayList<TriviaTile>();
			        	while(topicList.get(x).next())
			        	{
			        		String tempQ = topicList.get(x).getString("Question");
			        		String tempA = topicList.get(x).getString("Answer");
			        		int tempP = topicList.get(x).getInt("Points");
			        		if(tempP == 100)
			        		{
			        			TriviaTile tempTile = new TriviaTile(tempQ,tempA,tempP);
			        			Points100.add(tempTile);
			        		}
			        		else if(tempP == 200)
			        		{
			        			TriviaTile tempTile = new TriviaTile(tempQ,tempA,tempP);
			        			Points200.add(tempTile);
			        		}
			        		else{
			        			TriviaTile tempTile = new TriviaTile(tempQ,tempA,tempP);
			        			Points300.add(tempTile);
			        		}
			        		
			        	}
			        	pointSeperator.add(Points100);
			        	pointSeperator.add(Points200);
			        	pointSeperator.add(Points300);
			        	possibleTiles.add(pointSeperator);
			        }
		        
		        }
		        
		        catch(SQLException e)
		        {
		        	System.out.println(e.getMessage());
		        }
		        
		        for(int x = 0; x < possibleTiles.size();x++)
		        {
		        	ArrayList<TriviaTile> column = new ArrayList<TriviaTile>();
		        	for(int y = 0; y < possibleTiles.get(x).size();y++)
		        	{
		        		Random rand = new Random();
		        		int randomTile = rand.nextInt(possibleTiles.get(x).get(y).size());
		        		
		        		column.add(possibleTiles.get(x).get(y).get(randomTile));
		 
		        	}
		        	theBoard.addColumn(column);
		        }
		        //theBoard.displayBoard();
    }
    
   
    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String[] args) {
        SQLManager app = new SQLManager();
        
        TriviaBoard theBoard = new TriviaBoard();
         
        app.generateBoard(app, theBoard);
        
        theBoard.chooseTile(0,0);
        theBoard.chooseTile(1,0);
        theBoard.displayBoard();
    }*/
}