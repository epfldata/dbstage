package dbstage

import squid.utils._
import cats.Monoid
import squid.lib.transparencyPropagating

// TODO a type class for storage of non-bijective wrappers (ie: for which things like Monoid's wrapper instance don't make sense)

trait DataSource[+Row] { self =>
  
  // TODO:
  //def materialize[M:Materalizer[Row]]
  
  def iterator: Iterator[Row]
  
  @transparencyPropagating
  def map[B:Monoid](f: Row => B): B = flatMap(f)
  
  @transparencyPropagating
  def flatMap[B:Monoid](f: Row => B): B = iterator.map(f).fold(Monoid[B].empty)(Monoid[B].combine)
  
  @transparencyPropagating
  def filter(pred: Row => Bool): DataSource[Row] = new DataSource[Row] { def iterator = self.iterator.filter(pred) }
  @transparencyPropagating
  def withFilter(pred: Row => Bool): DataSource[Row] = filter(pred)
  @transparencyPropagating
  def where(pred: Row => Bool): DataSource[Row] = filter(pred)
  
}

trait DataTable[Row] extends DataSource[Row] {
  
}

//RecordRead
class InputFile[Row:RecordRead](fileName: String)(implicit codec: scala.io.Codec, sep: Separator) extends DataSource[Row] {
  def sepChr = sep.chr
  
  def iterator: Iterator[Row] = scala.io.Source.fromFile(fileName).getLines.map(the[RecordRead[Row]].read(sepChr))
}

import Embedding.Predef._

trait StagedDataSource[Row] {
  def stagedIterator: ClosedCode[Row |> Iterator]
}

class StagedInputFile[Row:RecordRead:CodeType](fileName: String)(implicit sread: Staged[RecordRead[Row]], codec: scala.io.Codec, sep: Separator)
extends InputFile[Row](fileName) with StagedDataSource[Row]
{
  
  def stagedIterator = {
    val readCode = the[Staged[RecordRead[Row]]].embedded(Embedding)
    
    // TODO fix Squid so this is not necessary:
    val fileName_ = fileName
    val sepChr_ = sepChr
    val codec_ = codec
    
    code"""
      scala.io.Source.fromFile(fileName_)(codec_).getLines.map($readCode.read(sepChr_))
    """
  }
  
}

//class ArrayStore[T]
//class ColumnStore[Row:ArrayStore] extends DataTable[Row] {
//  
//}

