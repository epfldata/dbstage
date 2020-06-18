package main

import scala.scalanative.unsafe._
import lib.LMDBTable

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length == 1) {
      val query = args(0)

      if (query == "scan") {
        println(Queries.big_scan)
      } else if (query == "key") {
        val result = Queries.key_join
        println(f"$result%1.6f")
      } else if (query == "cross") {
        println(Queries.cross_join)
      } else if (query == "tpch") {
        val result = Queries.tpch_query_6
        println(f"$result%1.6f")
      }
    }
  }

  def run(): Unit = {
    println(Queries.tpch_query_6)
  }

  def mainInsertRegions: Unit = {
    println("Inserting regions")
    Insertions.insertRegions
  }

  def mainInsertNations: Unit = {
    println("Inserting nations")
    Insertions.insertNations
  }

  def mainInsertParts: Unit = {
    println("Inserting parts")
    var from: Int = 0
    val step: Int = 10000
    val total_size: Int = 200000

    while(from < total_size) {
      val to = math.min(from+step, total_size)
      println(s"Inserting parts from ${from} to ${to}")
      Insertions.insertParts(from, to)
      from = to
    }
  }

  def mainInsertSuppliers: Unit = {
    println("Inserting suppliers")
    Insertions.insertSuppliers
  }

  def mainInsertPartSuppliers: Unit = {
    println("Inserting part suppliers")
    var from: Int = 0
    val step: Int = 10000
    val total_size: Int = 800000

    while(from < total_size) {
      val to = math.min(from+step, total_size)
      println(s"Inserting part suppliers from ${from} to ${to}")
      Insertions.insertPartSupps(from, to)
      from = to
    }
  }

  def mainInsertCustomers: Unit = {
    println("Inserting customers")
    var from: Int = 0
    val step: Int = 10000
    val total_size: Int = 150000

    while(from < total_size) {
      val to = math.min(from+step, total_size)
      println(s"Inserting customers from ${from} to ${to}")
      Insertions.insertCustomers(from, to)
      from = to
    }
  }

  def mainInsertOrders: Unit = {
    println("Inserting orders")
    var from: Int = 0
    val step: Int = 10000
    val total_size: Int = 1500000

    while(from < total_size) {
      val to = math.min(from+step, total_size)
      println(s"Inserting orders from ${from} to ${to}")
      Insertions.insertOrders(from, to)
      from = to
    }
  }

  def mainInsertLineItems: Unit = {
    println("Inserting line items")
    var from: Int = 0
    val step: Int = 10000
    val total_size: Int = 4423659

    while(from < total_size) {
      val to = math.min(from+step, total_size)
      println(s"Inserting line items from ${from} to ${to}")
      Insertions.insertLineitems(from, to)
      from = to
      Insertions.printAllSizes
    }
  }
}
