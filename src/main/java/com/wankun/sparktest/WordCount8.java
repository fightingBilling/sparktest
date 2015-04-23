package com.wankun.sparktest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * 使用lambda编程，整体代码量只会有原来的一半，而且代码的可读性也比原来好的多
 * 
 * @author kunwan
 *
 */
public class WordCount8 {

	public static void main(String[] args) {
		JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("Spark Count"));
		final int threshold = Integer.parseInt(args[1]);

		// split each document into words
		JavaRDD<String> tokenized = sc.textFile(args[0]).flatMap((s) -> Arrays.asList(s.split(" ")));

		// count the occurrence of each word
		JavaPairRDD<String, Integer> counts = tokenized.mapToPair((s) -> new Tuple2<String, Integer>(s, 1))
				.reduceByKey((i1, i2) -> i1 + i2);

		// filter out words with less than threshold occurrences
		JavaPairRDD<String, Integer> filtered = counts.filter((tup) -> tup._2() >= threshold);

		// count characters
		JavaPairRDD<Character, Integer> charCounts = filtered.flatMap((s) -> {
			Collection<Character> chars = new ArrayList<Character>(s._1().length());
			for (char c : s._1().toCharArray()) {
				chars.add(c);
			}
			return chars;
		}).mapToPair((c) -> new Tuple2<Character, Integer>(c, 1)).reduceByKey((i1, i2) -> i1 + i2);

		System.out.println(filtered.collect());
	}
}
