# When2Meet
A JAVA replication of the [When2Meet](https://www.when2meet.com/) app.
- Frontend: Swing GUI
- Backend: Spring Boot (in-memory H2 Database) 

### Environment
```shell
>>> mvn -version
Apache Maven 3.9.6 (bc0240f3c744dd6b6ec2920b3cd08dcc295161ae)
Maven home: /opt/homebrew/Cellar/maven/3.9.6/libexec
Java version: 17.0.11, vendor: Homebrew, runtime: /opt/homebrew/Cellar/openjdk@17/17.0.11/libexec/openjdk.jdk/Contents/Home
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "12.5", arch: "aarch64", family: "mac"
```

### Usage
```shell
# run Spring Boot
mvn clean install -U
mvn spring-boot:run

# run Swing GUI
mvn compile
mvn exec:java -Dexec.mainClass="when2meet.gui.MainFrame"
```