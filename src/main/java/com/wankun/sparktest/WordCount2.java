package com.wankun.sparktest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

/**
 * <pre>
 * 在程序中设置spark参数
 * 
 * park-submit --class com.wankun.sparktest.WordCount2 --master spark://quickstart.cloudera:7077 target/sparktest-1.0.0.jar /tmp/test1 2
 * </pre>
 * 
 * @author wankun
 * @date 2014年10月31日
 * @version 1.0
 */
public class WordCount2 {
	public static void main(String[] args) {
		JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("Spark Count")
				.setMaster("spark://quickstart.cloudera:7077").set("executor-memory", "40m"));
		final int threshold = Integer.parseInt(args[1]);

		// split each document into words
		JavaRDD<String> tokenized = sc.textFile(args[0]).flatMap(new FlatMapFunction<String, String>() {
			@Override
			public Iterable<String> call(String s) {
				return Arrays.asList(s.split(" "));
			}
		});

		// count the occurrence of each word
		JavaPairRDD<String, Integer> counts = tokenized.mapToPair(new PairFunction<String, String, Integer>() {
			@Override
			public Tuple2<String, Integer> call(String s) {
				return new Tuple2<String, Integer>(s, 1);
			}
		}).reduceByKey(new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer call(Integer i1, Integer i2) {
				return i1 + i2;
			}
		});

		// filter out words with less than threshold occurrences
		JavaPairRDD<String, Integer> filtered = counts.filter(new Function<Tuple2<String, Integer>, Boolean>() {
			@Override
			public Boolean call(Tuple2<String, Integer> tup) {
				return tup._2() >= threshold;
			}
		});

		// count characters
		JavaPairRDD<Character, Integer> charCounts = filtered
				.flatMap(new FlatMapFunction<Tuple2<String, Integer>, Character>() {
					@Override
					public Iterable<Character> call(Tuple2<String, Integer> s) {
						Collection<Character> chars = new ArrayList<Character>(s._1().length());
						for (char c : s._1().toCharArray()) {
							chars.add(c);
						}
						return chars;
					}
				}).mapToPair(new PairFunction<Character, Character, Integer>() {
					@Override
					public Tuple2<Character, Integer> call(Character c) {
						return new Tuple2<Character, Integer>(c, 1);
					}
				}).reduceByKey(new Function2<Integer, Integer, Integer>() {
					@Override
					public Integer call(Integer i1, Integer i2) {
						return i1 + i2;
					}
				});

		System.out.println(filtered.collect());
	}
}
