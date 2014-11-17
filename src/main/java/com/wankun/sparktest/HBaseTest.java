package com.wankun.sparktest;

import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * <pre> 
 *   	spark-submit --class com.wankun.sparktest.HBaseTest --master spark://node1:7077 --executor-memory 50m target/sparktest-1.0.0.jar 
 *   
 *   1. 需要引入hbase第三方类库，在spark-env.sh中设置export SPARK_CLASSPATH=$SPARK_CLASSPATH:/usr/lib/hbase/lib/* 参数
 *   	spark-submit --class com.wankun.sparktest.HBaseTest --master spark://node1:7077 --executor-memory 50m target/sparktest-1.0.0.jar
 *   	
 *   	如果类库不多的话，也可以使用 --driver-class-path yourlib.jar:abc.jar:xyz.jar 显示制定application 需要的Jar
 *   
 *   2. 程序中使用Kryo序列化工具将ImmutableBytesWritable类进行序列化
 *   	2.1 自定义序列化注册类MyRegistrator 
 *   	2.2 spark conf中进行声明 spark.serializer 和 spark.kryo.registrator
 * 
 * 
 * Please instead use:
 - ./spark-submit with --driver-class-path to augment the driver classpath
 - spark.executor.extraClassPath to augment the executor classpath
        
14/11/17 14:06:36 WARN spark.SparkConf: Setting 'spark.executor.extraClassPath' to ':/usr/lib/hbase/lib/*' as a work-around.
14/11/17 14:06:36 WARN spark.SparkConf: Setting 'spark.driver.extraClassPath' to ':/usr/lib/hbase/lib/*' as a work-around.

 * </pre>
 * @author root
 *
 */
public class HBaseTest {

	public static void main(String[] args) {
		SparkConf sparkconf = new SparkConf().setAppName("hbaseTest");
		sparkconf.set("spark.serializer","org.apache.spark.serializer.KryoSerializer");
		sparkconf.set("spark.kryo.registrator","com.wankun.sparktest.MyRegistrator");
		JavaSparkContext sc = new JavaSparkContext(sparkconf);

		Configuration conf = HBaseConfiguration.create();
		conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
		Scan scan = new Scan();
		scan.addFamily(Bytes.toBytes("f"));
		scan.addColumn(Bytes.toBytes("f"), Bytes.toBytes("age"));
		scan.setStartRow(Bytes.toBytes("k002"));
		scan.setStopRow(Bytes.toBytes("k004"));

		try {
			String tableName = "t1";
			conf.set(TableInputFormat.INPUT_TABLE, tableName);
			ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
			String ScanToString = Base64.encodeBytes(proto.toByteArray());
			conf.set(TableInputFormat.SCAN, ScanToString);

			JavaPairRDD<ImmutableBytesWritable, Result> myRDD = sc
					.newAPIHadoopRDD(conf, TableInputFormat.class,
							ImmutableBytesWritable.class, Result.class);
			System.out.println(myRDD.collect());
			myRDD.cache();
			System.out.println("row count:"+myRDD.count());
			
			List<Tuple2<ImmutableBytesWritable, Result>> result=myRDD.take(100);
			for(Tuple2<ImmutableBytesWritable, Result> tup:result){
				Result res=tup._2;
				System.out.println("key:"+tup._1+"\t value:"+res.rawCells());
				System.out.println("rowkey:"+new String(res.getRow())+"  cf:"+new String(res.getValue(Bytes.toBytes("f"), Bytes.toBytes("age"))));
				//for(keyvalue <- kv) println("rowkey:"+ new String(keyvalue.getRow)+ " cf:"+new String(keyvalue.getFamily()) + " column:" + new String(keyvalue.getQualifier) + " " + "value:"+new String(keyvalue.getValue()))
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}