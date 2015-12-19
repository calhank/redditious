import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark._
// To make some of the examples work we will also need RDD
import org.apache.spark.rdd.RDD
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.DefaultFormats._

object Main extends App {

 	//println(args(0))
  	val conf = new SparkConf().setAppName("Simple Application")
	val sc = new SparkContext(conf)

       /* test data
        val result = """{
    "status": "OK",
    "usage": "By accessing AlchemyAPI or using information generated by AlchemyAPI, you are agreeing to be bound by the AlchemyAPI Terms of Use: http://www.alchemyapi.com/company/terms.html",
    "totalTransactions": "1",
    "language": "english",
    "docSentiment": {
        "score": "0.5677",
        "type": "negative"
    }
}"""
*/

	val URL = "http://gateway-a.watsonplatform.net/calls/text/TextGetTextSentiment?apikey=<put api key here>&text=I%20am%20unhappy&outputMode=json"

	try {
		//call alchemy service, result is in json
  		val result = get(URL)

	        // json is a JValue instance
	        val json = parse(result)
        	implicit val formats = DefaultFormats

        	println ("Sentiment score is:")
        	val score = (json \\ "score").extract[String]
        	println (score)
        	println ("Sentiment type is")
        	val stype = (json \\ "type").extract[String]
        	println (stype)
	} catch {
  		case ioe: java.io.IOException =>  // handle this
  		case ste: java.net.SocketTimeoutException => // handle this
	}



/**
 * Returns the text (content) from a REST URL as a String.
 * Inspired by http://matthewkwong.blogspot.com/2009/09/scala-scalaiosource-fromurl-blockshangs.html
 * and http://alvinalexander.com/blog/post/java/how-open-url-read-contents-httpurl-connection-java
 *
 * The `connectTimeout` and `readTimeout` comes from the Java URLConnection
 * class Javadoc.
 * @param url The full URL to connect to.
 * @param connectTimeout Sets a specified timeout value, in milliseconds,
 * to be used when opening a communications link to the resource referenced
 * by this URLConnection. If the timeout expires before the connection can
 * be established, a java.net.SocketTimeoutException
 * is raised. A timeout of zero is interpreted as an infinite timeout.
 * Defaults to 5000 ms.
 * @param readTimeout If the timeout expires before there is data available
 * for read, a java.net.SocketTimeoutException is raised. A timeout of zero
 * is interpreted as an infinite timeout. Defaults to 5000 ms.
 * @param requestMethod Defaults to "GET". (Other methods have not been tested.)
 *
 * @example get("http://www.example.com/getInfo")
 * @example get("http://www.example.com/getInfo", 5000)
 * @example get("http://www.example.com/getInfo", 5000, 5000)
 */
@throws(classOf[java.io.IOException])
@throws(classOf[java.net.SocketTimeoutException])
def get(url: String,
        connectTimeout:Int =5000,
        readTimeout:Int =5000,
        requestMethod: String = "GET") = {
  import java.net.{URL, HttpURLConnection}
  val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
  connection.setConnectTimeout(connectTimeout)
  connection.setReadTimeout(readTimeout)
  connection.setRequestMethod(requestMethod)
  val inputStream = connection.getInputStream
  val content = scala.io.Source.fromInputStream(inputStream).mkString
  if (inputStream != null) inputStream.close
  content
}
}

