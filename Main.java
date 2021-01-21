import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List; 
import java.util.ArrayList; 

class Employer {

	private int id;
	private int age;
	private String name;
	private String address;
	private double salary;
	
    public Employer() {}
    public Employer(int id, String name, int age, String address, double salary) {
    	this.setId(id);
    	this.setAge(age);
    	this.setName(name);
    	this.setAddress(address);
    	this.setSalary(salary);
    }
        

	public int getId() { return this.id; }
	public int getAge() { return this.age; }
	public String getName() { return this.name; }
	public String getAddress() { return this.address; }
	public double getSalary() { return this.salary; }

	public void setId(int id) { this.id = id; }
	public void setAge(int age) { this.age = age; }
	public void setName(String name) { this.name = name; }
	public void setAddress(String address) { this.address = address; }
	public void setSalary(double salary) { this.salary = salary; }


	@Override
	public String toString(){
		return "ID      : " + this.getId()      + " \n" +
			   "Name    : " + this.getName()    + " \n" +
			   "Age     : " + this.getAge()     + " \n" +
			   "Address : " + this.getAddress() + " \n" +
			   "Salary  : $" + this.getSalary() + " \n";
	}
}

class Error {
	static public String message(Exception e) {
		return e.getClass().getName() + ": " + e.getMessage();
	}
}


public class Main {

	static Connection conn = null;

	public static void connectDB( String name ) {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");	
			//conn.setAutoCommit(false);
		}
		catch(Exception e) {
			System.err.println(Error.message(e));
			System.exit(0);
		}
	}

	public static void executeSQL(String sql) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			//conn.commit();
			stmt.close();
		}
		catch(Exception e) {
			System.err.println(Error.message(e));
			System.exit(0);			
		}
	}

	public static void closeDB() {
		try {
			conn.close();
		}
		catch(Exception e) {
			System.err.println(Error.message(e));
			System.exit(0);			
		}
	}

	public static List<Employer> getEmployers(String sql) {
	
		List<Employer> employers  = new ArrayList<Employer>(); 
			
		try {
			
			Statement stmt = conn.createStatement();
			 ResultSet response = stmt.executeQuery(sql);
	
				
			 while ( response.next() ) {
			    employers.add(
			    		new Employer(
			    			response.getInt("id"), 
			    			response.getString("name"),
			    			response.getInt("age"),
			    			response.getString("address"),
			    			response.getFloat("salary")
			    		)
			    	);
			 }
			 stmt.close();
		}
		catch(Exception e) {
			System.err.println(Error.message(e));
			System.exit(0);			
		}
	
		return employers;
	}

	public static void printEmployers(List<Employer> employers) {

		if(employers.size() != 0)
			System.out.println(":=====================:"); 

		for(var employer : employers) {
			System.out.println(employer);
			System.out.println(":=====================:"); 
		}
	}
	
	public static void main(String[] args) {
		
		connectDB("company");
				
		String create = "create table if not exists COMPANY " +
			         "(ID int primary key not null, " +
			         " NAME text not null, " + 
			         " AGE int not null, " + 
			         " ADDRESS char(50), " + 
			         " SALARY real)";

		executeSQL(create);
			
		String[] inserts = {
			"insert into COMPANY (ID,NAME,AGE,ADDRESS,SALARY) values (1, 'Paul', 32, 'California', 20000.00 );", 
			"insert into COMPANY (ID,NAME,AGE,ADDRESS,SALARY) values (2, 'Allen', 25, 'Texas', 15000.00 );", 
			"insert into COMPANY (ID,NAME,AGE,ADDRESS,SALARY) values (3, 'Teddy', 23, 'New York', 20000.00 );" 
		};
		
		String update = "update COMPANY set SALARY = 1250.00 where ID = 2";
		String select = "select * from COMPANY";

		if(getEmployers(select).size() == 0) {
			for(var insert : inserts)
				executeSQL(insert);
		}

		var employers = getEmployers(select);

		printEmployers(employers);
		
		closeDB(); 
	}
}
