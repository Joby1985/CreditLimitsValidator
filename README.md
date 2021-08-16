# CreditLimitsValidator
Validate the credit limits with respect to added up utilization of the current node and those from nodes in the lower levels

A.) Prerequisites:

	You need to have  maven installed on the system, and as well have Java 1.8.


B.) Steps to build and run.

1. Go to the root of the project.

2. clean and create package

	> mvn clean package

	The jar file will be created under target/creditLimitsValidator-1.0-SNAPSHOT.jar

3. Copy the log config xml into the current dir

	> cp src/main/resources/log4j2.xml .

4. Run the tool
	>java -Dlog4j.configurationFile=file:./log4j2.xml target/creditLimitsValidator-1.0-SNAPSHOT.jar

		Entities: A/B/C/D/:
				No limit breaches
		Entities: E/F/:
				Limit breach at
				E (limit = 200.0, direct utilization = 150.0, combined utilization = 230.0

		Note: By default, it uses data file from src/main/resources

	This can be changed by giving the new path in the command line as below:

	>java -jar target/creditLimitsValidator-1.0-SNAPSHOT.jar  <Custom_path_of_data_file>

	eg:

	>java -jar target/creditLimitsValidator-1.0-SNAPSHOT.jar  creditsEntityInfo.csv

5. Observe the logs in

		./logs/creditLimits.log

	Note: This location may be changed in the log4j2.xml file mentioned above.


C.) Notes on functionality:

	1. It does not matter the order of columns in the data file as long as it contains a header info with below columns:

	entity
	parent
	limit
	utilization

	2. With an enhancement, the data rows may appear in any order.
	Parent rows may appear after child rows. There is a reconciliation logic that finds out danging child records and attaches them to the right parent.