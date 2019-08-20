import java.sql.*;
import org.json.simple.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Scanner;  

public class MySQL {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private Scanner scan;
	private String Query;
	

	public static void main(String[] args) throws Exception{
		MySQL mysql= new MySQL();
		mysql.readDataBase();

	}

	public void readDataBase() throws Exception {
		scan = new Scanner(System.in); 
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/Matala?characterEncoding=latin1","root","password");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			System.out.println("Write...");
			String input=scan.nextLine();
			input=input.toLowerCase();
			if (input.equals("flights")) {
				Date current_date = new Date(System.currentTimeMillis());
				String date= current_date.toString();
				date=date.substring(0, 10);
				Query= "select flight_no, dep_time, dep_loc, arr_loc, arr_time, dep_time FROM flights WHERE dep_date='"+date+"';";
				resultSet = statement
						.executeQuery(Query);
				ResultSet resultSet2;
				while (resultSet.next()) {
					String a_id= resultSet.getString("arr_loc");
					String dep_time = resultSet.getString("dep_time");
					String arr_time = resultSet.getString("arr_time");

					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					java.util.Date date1 =  format.parse(arr_time);
					java.util.Date date2 =  format.parse(dep_time);
					long difference = date2.getTime() - date1.getTime();
					int ans=(int) (difference/1000);
					if (ans<0)
					{
						difference = date1.getTime() - date2.getTime();
						difference=86400000-difference;
						ans=(int) (difference/1000);
					}
					int sec=ans%60;
					ans/=60;
					ans=ans/60;
					int min= ans%60;
					int hour=ans/60;
					String flight_num=resultSet.getString("flight_no");
					String Query2="select country, city FROM airports WHERE a_id="+a_id+";";
					Statement statement2= connect.createStatement();
					resultSet2=statement2
							.executeQuery(Query2);
					while (resultSet2.next()) {
						System.out.println("flight num: "+flight_num+", Cuntry Destination: "
								+resultSet2.getString("country")+" , City destination: "+resultSet2.getString("city")+
								" The flight time is: "+hour +" hours, "  +min+" minutes, "+sec+" seconds");
					}
				}	
			}
			else if (input.equals("airports")) {
				Query="SELECT arr_loc ,a2.country,a2.city, flight_no, arr_date, arr_time"
						+ " from `flights` f, `airports` a2 , `airports` a "
						+ " where f.arr_loc=a2.a_id AND a.city='BGU' AND a.a_id= f.dep_loc;"; 
				 JSONObject jasonObj = new JSONObject();
				 resultSet = statement
							.executeQuery(Query);
				 while (resultSet.next()) {
					 String arr_loc= resultSet.getString("arr_loc");
					 String country= resultSet.getString("country");
					 String city= resultSet.getString("city");
					 String flight_no= resultSet.getString("flight_no");
					 String arr_date= resultSet.getString("arr_date");
					 String arr_time= resultSet.getString("arr_time");
					 
					 jasonObj.put("airport_id", arr_loc);
					 jasonObj.put("country", country);
					 jasonObj.put("city", city);
					 jasonObj.put("flight_number", flight_no);
					 jasonObj.put("Arrival date", arr_date);
					 jasonObj.put("Arrival time", arr_time);
					 System.out.println(jasonObj);
				 }
				 

			}
			else if (input.split(" ").length==2) {
				System.out.println("What is the passenger id?");
				String p_id=scan.nextLine();
				System.out.println("What is the flight number?");
				String flight_no=scan.nextLine();
				Query= "Insert  INTO onboard (p_id, flight_no)" + 
						"Values ("+p_id+","+flight_no+");";
				try {
					statement.executeUpdate(Query);
					System.out.println("The ticket successfully assigned");
				}
				catch (Exception e){
					System.out.println("The ticket assigned failed - The capacity of the plane is full");
				}
			}
			else {
				CallableStatement stm = connect.prepareCall("{call num_of_countrires(?, ?)}");
				stm.setInt(1,Integer.parseInt(input));
				stm.registerOutParameter(2, java.sql.Types.INTEGER);
				stm.execute();
				int m_count = stm.getInt(2);
				stm.close();
				System.out.println(m_count); 
			}




		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

	}

	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

}
