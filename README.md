#droitatedDB ![License](https://img.shields.io/badge/license-Apache%202-green.svg?style=flat) ![Version](https://img.shields.io/badge/Release-0.1.9-blue.svg)

## The name
droitatedDB is a shortened version of **Android** **annotated** **DB**.

An**droi**d anno**tated** **DB**

The result is **droitatedDB**


## At a glance ##
droitatedDB is a lightweight framework, which frees you from the burden of dealing with the Android SQLite database directly if you don't want to but lets you access it directly if you need to.

With an annotation based approach you are able to get a database up and running in no time. Simply annotate your data classes and let droitatedDB do the work.


## Features ##
 * [Annotation based implementation](https://github.com/arconsis/droitatedDB/wiki/Annotations)
 * No hard coded Strings in your code
 * Code generation for the ceremonial tasks
 * Compile time protection when renaming fields or tables
 * Fall back to Android default API is always possible

## Download ##
Add the android-apt plugin to your project-level build.gradle:

```gradle
buildscript {
  repositories {
    mavenCentral()
   }
  dependencies {
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
  }
}
```
After that, apply inside your module-level build.gradle the android-apt plugin and add droitatedDB dependencies:

```gradle
apply plugin: 'android-apt'

apt {
  arguments {
    manifest variant.outputs[0].processResources.manifestFile
  }
}

dependencies {
  compile 'org.droitateddb:processor:0.1.9'
  apt 'org.droitateddb:api:0.1.9'
}
```
## How do I use DroitatedDb? ##
For the full Tutorial check out our Wiki here: [Tutorial](https://github.com/arconsis/droitatedDB/wiki/Tutorial)  
First we need to create our database:

```java
@Update
@Persistence(dbName = "my_database.db", dbVersion = 1)
public class PersistenceConfig implements DbUpdate {
    @Override
    public void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        // here you can put your update code, when changing the database version
    }
}
```
After that we can create our first entitiy:

```java
@Entity
public class User {

   @PrimaryKey
   @AutoIncrement
   @Column
   private Integer _id;
   @Column
   private String name;

	public User() {
		//droitatedDB needs an empty constructor
	}
   
   public User(String name) {
   		this.name = name;
   }
   
   //...
}
```
Now we can store data:

```java
EntityService userService = new EntityService(context, User.class);
userService.save(new User("Bob"));
```

And read data:

```java
EntityService userService = new EntityService(context, User.class);
List<User> allUsers = userService.get();
```

## [Documentation](https://github.com/arconsis/droitatedDB/wiki) ##
Check out the documentation and introduction [here](https://github.com/arconsis/droitatedDB/wiki)


## [JavaDocs](http://arconsis.github.io/droitatedDB/) ##
Get into the API details reading the [technical API](http://arconsis.github.io/droitatedDB/)

## [Release notes](https://github.com/arconsis/droitatedDB/releases) ##
Check out the release notes, to see what's new in droitatedDB