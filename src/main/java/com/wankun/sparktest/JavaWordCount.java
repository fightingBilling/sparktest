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
 * spark-submit --class com.wankun.sparktest.JavaWordCount --master local target/sparkwordcount-0.0.1-SNAPSHOT.jar <input file> 2
 * 
 * 运行有三种方式
 *  --master local with --master spark://<master host>:<master port>.
 * 
 * If the cluster is running YARN, you can replace --master local with --master yarn.
 * 
 * </pre>
 * 
 * @author wankun
 * @date 2014年10月31日
 * @version 1.0
 */
public class JavaWordCount {
	public static void main(String[] args) {
		JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("Spark Count"));
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

		System.out.println(charCounts.collect());
	}
}