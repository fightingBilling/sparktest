# Spark 测试

## 代码说明

1. JavaWordCount.java  统计各行单词个数，最后所有行单词汇总统计个数
	
## 运行说明	

1. Spark 使用lazy evaluation，即只有当action操作发生的时候transformations才会在集群上执行
2. 程序运行命令：
	
	spark-submit --class com.wankun.sparktest.JavaWordCount --master local target/sparktest-1.0.0.jar /tmp/test1 2

3. 程序运行方式：
    
* --master local ： 本地运行
* --master spark://\<master host\>:\<master port\> 集群运行
* --master yarn ：在yarn上运行，程序自动检测ResourceManager's Address

## 资料
https://github.com/apache/spark apache spark mirror

## 20150423 
	
增加Java8 lambda的使用示例，更新工程的jdk为Java8

* 代码的可读性也比原来好的多
* 隐藏了很多spark封装的function API
* 整体代码量只会有原来的一半
 