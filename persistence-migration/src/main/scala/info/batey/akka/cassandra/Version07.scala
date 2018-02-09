package info.batey.akka.cassandra

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.journal.Tagged
import akka.persistence.query.PersistenceQuery
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.StdIn

object Version07 extends App {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materialiser = ActorMaterializer()
  implicit val timeOut = Timeout(20.second)
  val p1 = system.actorOf(Persistor.props("p1"))
  val allDone = Future.sequence((0 until 5).map { i =>
    p1 ? Tagged(s"Version07Event$i", Set("version07", "tag"))
  })
  allDone.onComplete(println)
  Await.ready(allDone, Duration.Inf)

  val readJournal =
    PersistenceQuery(system).readJournalFor[CassandraReadJournal](
      "cassandra-query-journal")

//  readJournal.currentEventsByTag("tag", 1L).to(Sink.foreach(println)).run()


  StdIn.readLine()
  system.terminate()
}
