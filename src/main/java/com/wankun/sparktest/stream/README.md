Counts words in UTF8 encoded, '\n' delimited text received from the network every second.
Usage: StreamWordCount <hostname> <port>
  <hostname> and <port> describe the TCP server that Spark Streaming would connect to receive data.

To run this on your local machine, you need to first run a Netcat server
   `$ nc -lk 9999`
and then run the example
   `$ bin/run-example org.apache.spark.examples.streaming.StreamWordCount localhost 9999`
   spark-submit --class com.wankun.sparktest.sql.SparkSQL --master yarn-cluster target/sparktest-1.0.0.jar