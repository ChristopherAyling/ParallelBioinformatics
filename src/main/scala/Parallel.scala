package Parallel

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import edu.au.jacobi.pattern.{Match, Series}
import qut._

import scala.collection.JavaConverters._
import scala.collection.mutable

import java.io._

final case class Job(referenceGene: Gene, records: List[GenbankRecord])
final case class JobRequest()

object MasterActor {
  def props: Props = Props[MasterActor]

  final case class Prediction(name: String, prediction: Match)
  final case class AnalyserStarted(name: String)
  final case class InitialiseConsensus(names: List[String])
  final case class DoneMessage(name: String)
}

class MasterActor extends Actor with ActorLogging {

  import MasterActor._

  val consensus = new mutable.HashMap[String, Sigma70Consensus]()
  consensus.put("all", new Sigma70Consensus())

  var jobs: mutable.Queue[Job] = mutable.Queue()
  var jobsLeft: Int = 0

  def receive = {
    case Prediction(name, prediction) => {
      consensus(name).addMatch(prediction)
      consensus("all").addMatch(prediction)
    }
    case Job(referenceGene, records) => jobs.enqueue(Job(referenceGene, records))
    case JobRequest() => if (!jobs.isEmpty) {
      val job = jobs.dequeue()
      sender ! job
      log.info(job.referenceGene.name + " job sent to " +sender().toString())
    }
    case InitialiseConsensus(names) => {
        jobsLeft = names.length
        names.map{name => {
          log.info("Initializing consensus for " + name)
          consensus.put(name, new Sigma70Consensus())
        }
      }
    }
    case AnalyserStarted(name) => {
      log.info("Beginning Analysis of " + name + ". Number of jobs left: " + jobsLeft)
    }
    case DoneMessage(name) => {
      jobsLeft = jobsLeft - 1
      log.info(name + " is done. Number of actors still left: " + jobsLeft)
      if (jobsLeft == 0) {
        log.info("All actors have completed their jobs. now printing results & writing to file")
        println(consensus.toString())
        Utils.ConsensusMapToJSON(consensus.toMap, "ParaOutput.json")
        log.info("Shutting down system")
        println(System.nanoTime())
        context.system.terminate()
      }
    }
  }
}

object AnalyserActor {
  def props(consensusActor: ActorRef): Props = Props(new AnalyserActor(consensusActor))
}

class AnalyserActor(masterActor: ActorRef) extends Actor {

  import AnalyserActor._
  import MasterActor._

  masterActor ! JobRequest()

  val sigma70_pattern: Series = Sigma70Definition.getSeriesAll_Unanchored(0.7)

  def receive = {
    case Job(referenceGene, records) => {
      // initialise entry in consensus's hashmap
      masterActor ! AnalyserStarted(referenceGene.name)
      // do the job
      records.foreach { record =>
        record.genes forEach { gene =>
          if (Sequential.Homologous(gene.sequence, referenceGene.sequence)) {
            val upStreamRegion: NucleotideSequence = Sequential.GetUpstreamRegion(record.nucleotides, gene)
            val prediction: Match = BioPatterns.getBestMatch(sigma70_pattern, upStreamRegion.toString) // predict the promoter
            if (prediction != null) masterActor ! Prediction(referenceGene.name, prediction)
          }
        }
      }
      // say when we are done!
      masterActor ! JobRequest()
      masterActor ! DoneMessage(referenceGene.name)
    }
  }
}

object Parallel {
  def run(referenceFile: String, dir: String, numCores: Int = 8): Unit = {
    import AnalyserActor._
    import MasterActor.{InitialiseConsensus}

    // Load Files
    val referenceGenes: List[Gene] = Sequential.ParseReferenceGenes(referenceFile).asScala.toList.sortBy(_.sequence.toString.length).reverse
    val GenBankRecords: List[GenbankRecord] = Sequential.ListGenbankFiles(dir).asScala.toList.map(Sequential.Parse)

    // Construct actors
    val system: ActorSystem = ActorSystem("ReferenceGeneAnalysis")

    // stores results
    val masterActor: ActorRef = system.actorOf(MasterActor.props, "consensusActor")
    masterActor ! InitialiseConsensus(referenceGenes.map(_.name))
    referenceGenes.foreach(gene => masterActor ! Job(gene, GenBankRecords))

    //performs analysis
    val threadIds = 0 until numCores
    val analyserActors = threadIds.map( threadId =>
      system.actorOf(AnalyserActor.props(masterActor), "AnalyserActor"++threadId.toString)
    )
  }// end run
}// end parallel
