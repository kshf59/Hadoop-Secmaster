#!/bin/bash
HADOOP_HOME=/opt/hadoop
HIVE_HOME=/opt/hive2
HADOOP_CORE=/opt/hadoop/share/hadoop/common/hadoop-common-2.8.2.jar:/opt/hadoop/share/hadoop/common/hadoop-common-2.8.2-tests.jar
CLASSPATH=.:$HADOOP_CORE:$HIVE_HOME/conf:/opt 
for i in ${HIVE_HOME}/lib/*.jar ; do
    CLASSPATH=$CLASSPATH:$i
done
java -cp $CLASSPATH HiveJdbcClient
