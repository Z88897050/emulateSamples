#!/usr/bin/env bash
shopt -s nullglob
basepath=$(cd `dirname $0`; pwd)

echo "determine env.........."

if [ -z "$JAVA_HOME" ]; then
   echo "Your JAVA_HOME is not configured, please configure and then execute again!"
   exit 1
else
   JAVAC="$JAVA_HOME/bin/javac"
   JAVA="$JAVA_HOME/bin/java"
   JAR="$JAVA_HOME/bin/jar"
fi

lib_dir="$basepath"/lib/;

#add lib
for f in $lib_dir/*.jar
do  
   CLASSPATH=${CLASSPATH}:$f;  
done  

jar_file="$basepath"/MetricTool.jar

if [ ! -f "$jar_file" ]; then
   echo "Compile source file........"
   $JAVAC -classpath ${CLASSPATH} -sourcepath "$basepath"/sources "$basepath"/sources/*.java -d "$basepath"

   echo "Create KafkaUtils jar.........."
   $JAR -cf MetricTool.jar  com/ethan/flume_kafka_metric/
   rm -rf "$basepath"/com
fi

CLASSPATH=${CLASSPATH}:$jar_file

$JAVA -cp $CLASSPATH com.ethan.flume_kafka_metric.Command "$@"
