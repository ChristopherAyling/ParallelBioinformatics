package Parallel

import scala.collection.JavaConverters._
import java.io._

import akka.actor.ActorRef
import edu.au.jacobi.pattern.Match
import qut._


object Main extends App {
  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) + "ns")
    result
  }

  val numThreads = 8
  val referenceFile = "./referenceGenes.list"
  val dir = "C:\\Users\\chris\\Documents\\uni_projects\\CAB401\\sc\\hello-world\\Ecoli"

//  println("running sequential")
//  time {
//    Sequential.run(referenceFile, dir)
//    Utils.ConsensusMapToJSON(Sequential.consensus.asScala.toMap, "SeqOutput.json")
//  }

  println("running parallel")
  Parallel.run(referenceFile, dir, 4)
}

object Utils {
  def ConsensusMapToJSON(consensus: Map[String, Sigma70Consensus], fname: String) = {
    val json: String = "{"++consensus.toList.map{ case (key: String, c: Sigma70Consensus) => {
        s""""$key": "${c.toString}""""
      }
    }.mkString(",")++"}"
    val pw = new PrintWriter(new File(fname))
    pw.write(json)
    pw.close()
  }

  def zipLongest[T](list1 : List[T], list2 : List[T]) : Seq[(T, T)] =
    if (list1.size < list2.size)
      Stream.continually(list1).flatten zip list2
    else
      list1 zip Stream.continually(list2).flatten

  def zipGensActs(gens: List[Gene], acts: List[ActorRef]): List[(Gene, ActorRef)] =
    if (gens.length < acts.length) gens zip acts
    else (gens zip Stream.continually(acts).flatten).toList
}