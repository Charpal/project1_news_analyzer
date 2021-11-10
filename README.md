# Wheel of Time News Analyzer

## Project Description

This is a simple application that imports articles from the News API in order to analyze the data.
## Technologies Used

* Hadoop MapReduce
* YARN
* HDFS
* Scala 2.11.8
* Hive
* Git + GitHub

## Features

List of features ready and TODOs for future development
* Simple User Interface
* User Login System
* Import data from News API and load into Hive tables
* Analyze data using Hive queries

To-do list:
* Add more analysis options
* Encrypt passwords

## Getting Started
   
(include all environment setup steps)
1. Git clone the repository\
`git clone git@github.com:Charpal/project1_news_analyzer.git`
2. Open the command line and copy the jar file into the Hortonworks Data Platform\
`scp -P 2222 <path to jar file> maria_dev@sandbox-hdp.hortonworks.com:/home/maria_dev`
3. Create the tables HeadlinesTB, WoTTB, GoTTB on Hive\
`CREATE TABLE HeadlinesTB(str String);`\
`CREATE TABLE WoTTB(str String);`\
`CREATE TABLE GoTTB(str String);`
4. Submit the jar file as a job on Spark \
`spark-submit newsanalyzercli_2.11-0.1.0-SNAPSHOT.jar`

## Usage

> How to use the application is simple: type in the number correlating to the action you want to take and let the application do the work. When creating a new user or logging in, follow the instructions given.
