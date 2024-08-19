# Setting up

1. Set-up Tomcat
   - Use tomcat plugin if you are using intellij community (Default: http://localhost:8080/)
   - Smart Tomcat plugin
2. Add dependencies
   - javax.servlet-api
3. Create a Java folder under src/main and make it as Source Root

## Links
- https://medium.com/@seanliu_90343/how-to-set-up-a-tomcat-server-under-maven-project-structure-using-intellij-idea-macos-1475a975abf0
- https://tomcat.apache.org/tomcat-10.1-doc/setup.html
- https://stackoverflow.com/questions/22047860/tomcat-in-intellij-idea-community-edition
  - Core: https://tomcat.apache.org/download-10.cgi (Not required if using plugin)
  - 
- https://stackoverflow.com/questions/6451377/loading-context-in-spring-using-web-xml
- https://medium.com/@lakshyachampion/the-dispatcherservlet-the-engine-of-request-handling-in-spring-boot-3a85c2bdbe6b
- https://www.baeldung.com/spring-xml-vs-java-config
- 

## Running Tomcat using Intellij Community Version (Plugin)
- https://stackoverflow.com/questions/23775647/how-to-configure-tomcat7-maven-plugin-in-pom-xml
- https://stackoverflow.com/questions/39962624/intellij-adding-a-tomcat-plugin-in-maven-menu
- https://spsarolkar.github.io/esp8266/2018/03/18/Run-webapplications-using-tomcat-maven-plugin.html
- https://tomcat.apache.org/maven-plugin-2.0/tomcat7-maven-plugin/usage.html

## Troubleshoot
- https://stackoverflow.com/questions/69073664/why-intellij-is-reporting-uri-is-not-registered-and-attribute-xsi-schemalocat
- Web.xml ordering matters due to schema reference. If not change as follows:
  - https://stackoverflow.com/questions/12472541/the-content-of-element-type-must-match-in-web-xml
- https://www.baeldung.com/tomcat-deploy-war
- https://stackoverflow.com/questions/71179660/spring-boot-3-jakarta-and-javax
- - https://stackoverflow.com/questions/54406169/spring-default-consumes-and-produces
- https://stackoverflow.com/questions/67072530/spring-restcontroller-should-i-need-to-indicate-produces-mediatype-applicati
- Insert <context:annotation-config/> to read RestController
- Insert <mvc:annotation-driven /> to allow default JSON Converter during response


## Quartz
- AutoWiringSpringBeanJobFactory
  - https://stackoverflow.com/questions/6990767/inject-bean-reference-into-a-quartz-job-in-spring
  - https://github.com/eugenp/tutorials/blob/master/spring-quartz/src/main/java/org/baeldung/springquartz/basics/scheduler/QrtzScheduler.java
- Quartz Configuration
  - https://github.com/eugenp/tutorials/blob/master/spring-quartz/src/main/java/org/baeldung/springquartz/basics/scheduler/QrtzScheduler.java
  - https://stackoverflow.com/questions/17673883/spring-with-quartz
- https://stackoverflow.com/questions/2161054/where-to-place-and-how-to-read-configuration-resource-files-in-servlet-based-app
- https://maven.apache.org/plugins/maven-resources-plugin/examples/resource-directory.html
- Job Configuration
  - # https://stackoverflow.com/questions/17422989/how-could-i-integrate-spring-data-source-into-quartz
# https://www.quartz-scheduler.org/documentation/quartz-1.8.6/configuration/ConfigJDBCJobStoreClustering.html
# https://stackoverflow.com/questions/44020066/spring-boot-quartz-oracle-job-scheduling
#https://github.com/quartz-scheduler/quartz/wiki/How-to-Setup-Databases
#https://github.com/quartz-scheduler/quartz/wiki/How-to-Use-DB-Connection-Pool#using-custom-db-conn-pool

// https://github.com/eugenp/tutorials/blob/master/spring-quartz/src/main/java/org/baeldung/springquartz/basics/scheduler/QrtzScheduler.java
// https://stackoverflow.com/questions/48454924/shutdown-all-the-quartz-jobs-as-soon-as-app-server-is-shut-down
// https://stackoverflow.com/questions/65842715/application-context-returns-instance-of-org-quartz-impl-stdscheduler-instead-of
https://stackoverflow.com/questions/15020625/quartz-how-to-shutdown-and-restart-the-scheduler



https://www.digitalocean.com/community/tutorials/java-datasource-jdbc-datasource-example
https://www.digitalocean.com/community/tutorials/tomcat-datasource-jndi-example-java
https://stackoverflow.com/questions/9183321/how-to-use-jndi-datasource-provided-by-tomcat-in-spring
https://www.digitalocean.com/community/tutorials/spring-datasource-jndi-with-tomcat-example

https://github.com/quartz-scheduler/quartz/tree/main/quartz/src/main/resources/org/quartz/impl/jdbcjobstore
https://stackoverflow.com/questions/8298132/how-to-get-resource-from-the-context-xml-file-in-tomcat-webapp
https://www.digitalocean.com/community/tutorials/spring-datasource-jndi-with-tomcat-example



Mail:
- https://stackoverflow.com/questions/4334753/how-to-send-multiple-emails-in-one-session
- https://stackoverflow.com/questions/4184204/what-is-the-difference-between-getdefaultinstance-and-getinstance-in-session#:~:text=getInstance()%20%3A%20Get%20a%20new,and%20installed%20as%20the%20default.
- https://www.digitalocean.com/community/tutorials/javamail-example-send-mail-in-java-smtp#javamail-example-program


## Parser (CSV)
```java
<!-- https://mvnrepository.com/artifact/com.univocity/univocity-parsers -->
		<dependency>
			<groupId>com.univocity</groupId>
			<artifactId>univocity-parsers</artifactId>
			<version>2.9.1</version>
		</dependency>
```
```java
    @Parsed
        Integer id;
    @Parsed(field = "col1")
        String testString1;
    @Parsed(field = "col2")
        String testString2;
    @Parsed
    Timestamp timestamp;
	@Test
	void testReadCSV() throws IOException {
		System.out.println("Test CSV Parser...");
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "Book1.csv";

//		OutputStream os = response.getOutputStream()
//		MultipartFile file = null; // file.getInputStream()


		CsvParserSettings parserSettings = new CsvParserSettings();
		CsvRoutines routines = new CsvRoutines(parserSettings);

		for (SimpleBean2 eachBean : routines.iterate(SimpleBean2.class, new File(fileLocation), "UTF-8")) {
			//process your bean
			System.out.println(eachBean);
		}
	}

	@Test
	void restWriteCSV() {
		ArrayList<SimpleBean2> SimpleBean2List = new ArrayList<SimpleBean2>();
		SimpleBean2List.add(new SimpleBean2(1, null, null,null));
		SimpleBean2List.add(new SimpleBean2(null, "//t//\\n/r/wa\\\'/dsa\"12\"\"dsa'd'", " !@#$%^^& *& *() ()_) ", Timestamp.valueOf(ZonedDateTime.now().toLocalDateTime())));

		// https://www.baeldung.com/java-univocity-parsers
		new CsvRoutines().writeAll(SimpleBean2List, SimpleBean2.class, new File("output.csv"), "id", "col1", " testString2", "timestamp", "timestamp2");


	}

```