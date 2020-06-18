package main

import scala.scalanative.unsafe._
import lib.LMDBTable

object Main {
  def main(args: Array[String]): Unit = {
    Demo.insertCompanies
    Demo.insertJobs
    Demo.insertPersons
    Demo.printData
  }
}
