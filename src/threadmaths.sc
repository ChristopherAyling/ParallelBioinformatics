val nCores = 2

(0 until 1) toList

import scala.collection.mutable.Queue

var q = Queue(1)

q.dequeue()
q.dequeue()

def zipLongest[T](list1 : List[T], list2 : List[T]) : Seq[(T, T)] =
  if (list1.size < list2.size)
    Stream.continually(list1).flatten zip list2
  else
    list1 zip Stream.continually(list2).flatten

val letters = List('a', 'b', 'c', 'd', 'e')
val numbers = List(1, 2)

println(zipLongest(letters, numbers))
(0 until 5).toList.sorted

val jobs = List(
  "asdnsajdsjkadsjkadjbsajkdskadbsjkadbsjkabdjksabdjksadbsjkabdsjkakdb",
  "asdfsdfsdef",
  "sadsa",
  "dfsadfsdfsdfsdfsdfsdfsdfsd",
  "sad",
  "adsafsfsadsadsadsadsa",
  "a"
)

(0 until 10).toList.slice(0, 3)

def split[A](xs: List[A], n: Int): List[List[A]] =
  if (xs.isEmpty) Nil
  else (xs take n) :: split(xs drop n, n)

def balanceWork1(jobs: List[String], workers: Int): List[List[String]] = {
  split(jobs, workers)
}

def balanceInner(balanced: List[List[String]], jobs: List[String]): List[List[String]] = {
  println(balanced, jobs)
  if (jobs.isEmpty) return balanced
  else balanceInner(balanced ++ jobs.reverse.take(3).map(List(_)), jobs drop 3)
}

def balance(jobs: List[String]) = {
  val prev = List()
  balanceInner(prev, jobs.sortBy(_.length))
}



def balancedDispatch(jobs: List[String]): Boolean = {
  def inner(jobs: List[String]): Boolean = {
    if (jobs.isEmpty) return true
    else {
      //! job(0)
      inner(jobs.drop(0))
    }
  }
  val s = jobs.sortBy(_.length)
  inner(s)
}



balance(jobs)

balanceWork1(jobs, nCores)