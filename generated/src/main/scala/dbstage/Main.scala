package main

import scala.scalanative.unsafe._
import lib.LMDBTable

object Main {
  def main(args: Array[String]): Unit = {
    println("[Press Enter] to print data:")
    wait_for_input()
    Demo.printData

    println("\n\n[Press Enter] to insert all data:")
    wait_for_input()
    Demo.insertCompanies
    Demo.insertJobs
    Demo.insertPersons

    println("\n\n[Press Enter] to print data:")
    wait_for_input()
    Demo.printData

    println("\n\n[Press Enter] to delete Dave:")
    wait_for_input()
    Demo.deleteDave

    println("\n\n[Press Enter] to print data:")
    wait_for_input()
    Demo.printData

    println("\n\n[Press Enter] to print data in columns:")
    wait_for_input()
    Demo.printDataColumns

    println("\n\n[Press Enter] to compute minimum salary:")
    wait_for_input()
    val (person, minSalary) = Demo.computeMinimumSalary
    println(s"${person} has minimum salary at ${minSalary}")

    println("\n\n[Press Enter] to compute max salary difference between community managers:")
    wait_for_input()
    var (p11, p21, diff1) = Demo.computeBiggestSalaryDifferenceCommunityManager
    println(s"Biggest salary difference for community managers is ${diff1}, between ${p11} and ${p21}")

    println("\n\n[Press Enter] to increase Robert's salary by 750:")
    wait_for_input()
    Demo.increaseRobertSalaryBy750

    println("\n\n[Press Enter] to print data in columns:")
    wait_for_input()
    Demo.printDataColumns

    println("\n\n[Press Enter] to compute max salary difference between community managers:")
    wait_for_input()
    val (p12, p22, diff2) = Demo.computeBiggestSalaryDifferenceCommunityManager
    println(s"Biggest salary difference for community managers is ${diff2}, between ${p12} and ${p22}")

    println("\n\n[Press Enter] to compute cumulated salary for each company:")
    wait_for_input()
    Demo.computeSalaryCompany
  }

  def wait_for_input(): Unit = {
    Console.readLine()
  }
}
